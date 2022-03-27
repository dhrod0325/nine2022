package ks.model;

import ks.app.LineageAppContext;
import ks.app.config.prop.CodeConfig;
import ks.constants.L1SkillId;
import ks.core.ObjectIdFactory;
import ks.core.datatables.SkillsTable;
import ks.core.datatables.npc.NpcTable;
import ks.model.instance.L1EffectInstance;
import ks.model.map.L1Map;
import ks.model.map.L1WorldMap;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.*;
import ks.scheduler.npc.NpcDeleteScheduler;
import ks.util.L1CharPosUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@Component
public class L1EffectSpawn {
    private static final Logger logger = LogManager.getLogger();

    private final Map<L1Character, List<L1EffectInstance>> fireWallMap = new HashMap<>();

    private final Map<L1Character, L1EffectInstance> lifeStreamMap = new HashMap<>();

    public static L1EffectSpawn getInstance() {
        return LineageAppContext.getBean(L1EffectSpawn.class);
    }

    public L1EffectInstance spawnEffect(int npcId, long time, int locX, int locY, short mapId) {
        try {
            L1Npc template = NpcTable.getInstance().getTemplate(npcId);

            if (template == null) {
                return null;
            }

            L1EffectInstance effect = new L1EffectInstance(template);

            effect.setId(ObjectIdFactory.getInstance().nextId());
            effect.getGfxId().setGfxId(template.getGfxid());
            effect.setX(locX);
            effect.setY(locY);
            effect.setHomeX(locX);
            effect.setHomeY(locY);
            effect.setHeading(0);
            effect.setMap(mapId);

            L1World.getInstance().storeObject(effect);
            L1World.getInstance().addVisibleObject(effect);

            List<L1PcInstance> list = L1World.getInstance().getRecognizePlayer(effect);

            for (L1PcInstance pc : list) {
                effect.getNearObjects().addKnownObject(pc);
                pc.getNearObjects().addKnownObject(effect);
                pc.sendPackets(new S_NPCPack(effect));
                Broadcaster.broadcastPacket(pc, new S_NPCPack(effect));
            }

            NpcDeleteScheduler.getInstance().addNpcDelete(effect, time);

            return effect;
        } catch (Exception e) {
            logger.error(e);
        }

        return null;
    }

    public void doSpawnLifeStream(L1Character cha, int x, int y) {
        int duration = SkillsTable.getInstance().getTemplate(L1SkillId.LIFE_STREAM).getBuffDuration();

        L1EffectInstance old = lifeStreamMap.get(cha);

        if (old != null) {
            old.deleteMe();
        }

        L1EffectInstance effect = spawnEffect(81169, duration * 1000L, x, y, cha.getMapId());
        lifeStreamMap.put(cha, effect);
    }

    public void doSpawnFireWall(L1Character cha, int targetX, int targetY) {
        L1Npc firewall = NpcTable.getInstance().getTemplate(81157);

        int duration = SkillsTable.getInstance().getTemplate(L1SkillId.FIRE_WALL).getBuffDuration();

        if (firewall == null) {
            throw new NullPointerException("FireWall data not found:npcid=81157");
        }

        List<L1EffectInstance> fireWall = new ArrayList<>();
        List<L1EffectInstance> oldList = fireWallMap.getOrDefault(cha, fireWall);

        for (L1EffectInstance o : oldList) {
            o.deleteMe();
        }

        fireWallMap.remove(cha);

        L1Character base = cha;

        for (int i = 0; i < 8; i++) {
            int a = L1CharPosUtils.targetDirection(base, targetX, targetY);
            int x = base.getX();
            int y = base.getY();

            x += CodeConfig.HEADING_TABLE_X[a];
            y += CodeConfig.HEADING_TABLE_Y[a];

            if (!L1CharPosUtils.isAttackPosition(base, x, y, 1)) {
                x = base.getX();
                y = base.getY();
            }

            L1Map map = L1WorldMap.getInstance().getMap(cha.getMapId());

            if (!map.isArrowPassable(x, y, cha.getHeading())) {
                break;
            }

            L1EffectInstance effect = spawnEffect(81157, duration * 1000L, x, y, cha.getMapId());
            fireWall.add(effect);

            if (effect == null) {
                break;
            }

            if (cha instanceof L1PcInstance) {
                L1PcInstance pc = (L1PcInstance) cha;
                effect.setSpawner(pc);
            }

            if (targetX == x && targetY == y) {
                break;
            }

            base = effect;
        }

        fireWallMap.put(cha, fireWall);
    }

    private final Map<Integer, Map<L1Character, L1EffectInstance>> cubeContainer = new HashMap<>();

    public void doSpawnCube(L1Character cha, int npcId, int x, int y, int skillId) {
        if (cha.getSkillEffectTimerSet().hasSkillEffect(skillId)) {
            cha.sendPackets(new S_ServerMessage(1412));
            return;
        }

        Map<L1Character, L1EffectInstance> cubeMap = cubeContainer.getOrDefault(skillId, new HashMap<>());

        int duration = SkillsTable.getInstance().getTemplate(skillId).getBuffDuration();

        L1EffectInstance old = cubeMap.get(cha);

        if (old != null) {
            old.deleteMe();
        }

        L1EffectInstance effect = spawnEffect(npcId, duration * 1000L, x, y, cha.getMapId());

        if (effect != null) {
            effect.addDeleteCallBack(npc -> {
                List<L1Character> users = cubeBuffObjects.getOrDefault(effect.getId(), Collections.emptyList());
                users.clear();

                cubeContainer.remove(skillId);
                cubeBuffObjects.remove(effect.getId());
            });

            if (cha instanceof L1PcInstance) {
                effect.setSpawner((L1PcInstance) cha);
            }
        }

        cubeMap.put(cha, effect);

        cubeContainer.put(skillId, cubeMap);

        L1Skills skill = SkillsTable.getInstance().getTemplate(skillId);

        cha.getSkillEffectTimerSet().setSkillEffect(skillId, skill.getBuffDuration() * 1000);
    }

    public void doSpawnCubeIgnition(L1Character cha, int x, int y) {
        doSpawnCube(cha, 4500501, x, y, L1SkillId.CUBE_IGNITION);
    }

    public void doSpawnCubeBalance(L1Character cha, int x, int y) {
        doSpawnCube(cha, 4500504, x, y, L1SkillId.CUBE_BALANCE);
    }

    public void doSpawnCubeShock(L1Character cha, int x, int y) {
        doSpawnCube(cha, 4500503, x, y, L1SkillId.CUBE_SHOCK);
    }

    public void doSpawnCubeQuake(L1Character cha, int x, int y) {
        doSpawnCube(cha, 4500502, x, y, L1SkillId.CUBE_QUAKE);
    }

    private final Map<Integer, List<L1Character>> cubeBuffObjects = new HashMap<>();

    @Scheduled(fixedDelay = 200)
    public void scheduled() {
        for (int skillId : cubeContainer.keySet()) {
            Map<L1Character, L1EffectInstance> map = cubeContainer.get(skillId);

            map.values().forEach(effect -> {
                List<L1Character> cubeBuffObjects = this.cubeBuffObjects.getOrDefault(effect.getId(), new CopyOnWriteArrayList<>());

                List<L1Character> effectObjects = L1World.getInstance()
                        .getVisibleObjects(effect, 3)
                        .stream()
                        .filter(L1Character.class::isInstance)
                        .map(L1Character.class::cast)
                        .collect(Collectors.toList());

                for (L1Character obj : cubeBuffObjects) {
                    if (!effectObjects.contains(obj)) {
                        if (cubeBuffObjects.contains(obj)) {
                            cubeBuffObjects.remove(obj);
                            logger.debug("큐브 스탑 : {} ", skillId);
                        }
                    }
                }

                for (L1Character obj : effectObjects) {
                    if (!cubeBuffObjects.contains(obj)) {
                        cubeBuffObjects.add(obj);
                        logger.debug("큐브 시작 : {} ", skillId);
                    }
                }

                this.cubeBuffObjects.put(effect.getId(), cubeBuffObjects);
            });
        }
    }

    @Scheduled(fixedDelay = 500)
    public void buff() {
        for (int skillId : cubeContainer.keySet()) {
            Map<L1Character, L1EffectInstance> map = cubeContainer.get(skillId);

            map.values().forEach(effect -> {
                List<L1Character> buffUsers = cubeBuffObjects.getOrDefault(effect.getId(), new CopyOnWriteArrayList<>());

                buffUsers.forEach(cha -> {
                    L1PcInstance spawner = effect.getSpawner();

                    if (spawner == null) {
                        return;
                    }

                    boolean isTeam = false;

                    if (cha.equals(spawner)) {
                        isTeam = true;
                    } else {
                        if (cha instanceof L1PcInstance) {
                            L1PcInstance target = (L1PcInstance) cha;

                            L1Clan clan = spawner.getClan();
                            L1Party party = spawner.getParty();

                            if (party != null) {
                                isTeam = party.isMember(target);
                            }

                            if (!isTeam) {
                                if (clan != null) {
                                    isTeam = clan.getClanId() == target.getClanId();
                                }
                            }
                        }
                    }

                    if (isTeam) {
                        cubeBuffTeam(skillId, effect, cha, spawner);
                    } else {
                        cubeBuffEnemy(skillId, effect, cha, spawner);
                    }
                });
            });
        }
    }

    private void cubeBuffEnemy(int skillId, L1EffectInstance effect, L1Character cha, L1PcInstance spawner) {
        logger.debug("큐브 버프 적 : {}", cha);

        if (skillId == L1SkillId.CUBE_IGNITION) {
            if (!cha.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.STATUS_CUBE_IGNITION_ENEMY)) {
                cha.getSkillEffectTimerSet().setSkillEffect(L1SkillId.STATUS_CUBE_IGNITION_ENEMY, 4000);

                cha.receiveDamage(spawner, 15);

                cha.sendPackets(new S_SkillSound(cha.getId(), 6709));
                Broadcaster.broadcastPacket(cha, new S_SkillSound(cha.getId(), 6709));
            }
        } else if (skillId == L1SkillId.CUBE_QUAKE) {
            if (!cha.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.STATUS_CUBE_QUAKE_ENEMY)) {
                cha.getSkillEffectTimerSet().setSkillEffect(L1SkillId.STATUS_CUBE_QUAKE_ENEMY, 4000);

                cha.getSkillEffectTimerSet().setSkillEffect(L1SkillId.STATUS_FREEZE, 1000);
                cha.sendPackets(new S_Paralysis(S_Paralysis.TYPE_BIND, true));

                cha.sendPackets(new S_SkillSound(cha.getId(), 6715));
                Broadcaster.broadcastPacket(cha, new S_SkillSound(cha.getId(), 6715));
            }
        } else if (skillId == L1SkillId.CUBE_SHOCK) {
            if (!cha.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.STATUS_CUBE_SHOCK_ENEMY)) {
                cha.getSkillEffectTimerSet().setSkillEffect(L1SkillId.STATUS_CUBE_SHOCK_ENEMY, 4000);

                cha.getSkillEffectTimerSet().setSkillEffect(L1SkillId.STATUS_CUBE_SHOCK, 1000 * 8);
                cha.sendPackets(new S_SkillSound(cha.getId(), 6721));
                Broadcaster.broadcastPacket(cha, new S_SkillSound(cha.getId(), 6721));
            }
        } else if (skillId == L1SkillId.CUBE_BALANCE) {
            if (!cha.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.STATUS_CUBE_BALANCE_ENEMY)) {
                cha.getSkillEffectTimerSet().setSkillEffect(L1SkillId.STATUS_CUBE_BALANCE_ENEMY, 5000);

                cha.setCurrentMp(cha.getCurrentMp() + 5);
                cha.receiveDamage(spawner, 25);

                cha.sendPackets(new S_SkillSound(cha.getId(), 6727));
                Broadcaster.broadcastPacket(cha, new S_SkillSound(cha.getId(), 6727));
            }
        }
    }

    private void cubeBuffTeam(int skillId, L1EffectInstance effect, L1Character cha, L1PcInstance spawner) {
        if (skillId == L1SkillId.CUBE_IGNITION) {
            if (!cha.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.STATUS_CUBE_IGNITION_TEAM)) {
                cha.getSkillEffectTimerSet().setSkillEffect(L1SkillId.STATUS_CUBE_IGNITION_TEAM, 4000);

                cha.sendPackets(new S_SkillSound(cha.getId(), 6708));
                Broadcaster.broadcastPacket(cha, new S_SkillSound(cha.getId(), 6708));

                cha.getResistance().addFire(30);
                sendDefPacket(cha);
            }
        } else if (skillId == L1SkillId.CUBE_QUAKE) {
            if (!cha.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.STATUS_CUBE_QUAKE_TEAM)) {
                cha.getSkillEffectTimerSet().setSkillEffect(L1SkillId.STATUS_CUBE_QUAKE_TEAM, 4000);

                cha.sendPackets(new S_SkillSound(cha.getId(), 6714));
                Broadcaster.broadcastPacket(cha, new S_SkillSound(cha.getId(), 6714));

                cha.getResistance().addEarth(30);
                sendDefPacket(cha);
            }
        } else if (skillId == L1SkillId.CUBE_SHOCK) {
            if (!cha.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.STATUS_CUBE_SHOCK_TEAM)) {
                cha.getSkillEffectTimerSet().setSkillEffect(L1SkillId.STATUS_CUBE_SHOCK_TEAM, 4000);

                cha.sendPackets(new S_SkillSound(cha.getId(), 6720));
                Broadcaster.broadcastPacket(cha, new S_SkillSound(cha.getId(), 6720));

                cha.getResistance().addWind(30);
                sendDefPacket(cha);
            }
        } else if (skillId == L1SkillId.CUBE_BALANCE) {
            if (!cha.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.STATUS_CUBE_BALANCE_TEAM)) {
                cha.getSkillEffectTimerSet().setSkillEffect(L1SkillId.STATUS_CUBE_BALANCE_TEAM, 5000);

                cha.setCurrentMp(cha.getCurrentMp() + 5);
                cha.receiveDamage(spawner, 25);

                cha.sendPackets(new S_SkillSound(cha.getId(), 6727));
                Broadcaster.broadcastPacket(cha, new S_SkillSound(cha.getId(), 6727));
            }
        }
    }


    private void sendDefPacket(L1Character cha) {
        if (cha instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) cha;
            pc.sendPackets(new S_OwnCharStatus(pc));
        }
    }
}