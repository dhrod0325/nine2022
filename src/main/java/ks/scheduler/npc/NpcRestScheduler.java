package ks.scheduler.npc;

import ks.app.LineageAppContext;
import ks.app.config.prop.CodeConfig;
import ks.constants.L1ActionCodes;
import ks.model.Broadcaster;
import ks.model.L1CastleLocation;
import ks.model.L1World;
import ks.model.attack.utils.L1MagicUtils;
import ks.model.instance.*;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_DoActionGFX;
import ks.util.L1CharPosUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class NpcRestScheduler {
    private static final Logger logger = LogManager.getLogger(NpcRestScheduler.class);

    private final List<L1NpcInstance> list = new CopyOnWriteArrayList<>();

    private final List<L1DoorInstance> doorCloseList = new CopyOnWriteArrayList<>();

    private final List<L1GuardianInstance> restoreList = new CopyOnWriteArrayList<>();

    private final List<L1EffectInstance> fireWallList = new CopyOnWriteArrayList<>();

    public static NpcRestScheduler getInstance() {
        return LineageAppContext.getBean(NpcRestScheduler.class);
    }

    @Scheduled(fixedDelay = 1000)
    public void run() {
        if (!LineageAppContext.isRun()) {
            return;
        }

        list.stream().filter(Objects::nonNull).forEach(npc -> {
            if (npc.restTime <= System.currentTimeMillis()) {
                npc.setRest(false);
                removeNpc(npc);
            }
        });

        doorCloseList.stream().filter(Objects::nonNull).forEach(npc -> {
            if (npc.destroyed) {
                removeDoor(npc);
                return;
            }

            if (npc.closeTime <= System.currentTimeMillis()) {
                npc.close();
                removeDoor(npc);
            }
        });

        restoreList.stream().filter(Objects::nonNull).forEach(npc -> {
            if (npc.restoreTime <= System.currentTimeMillis()) {
                if (npc.getTemplate().getNpcId() == 70848) { // 엔트
                    if (!npc.getInventory().checkItem(40506, 1)) {
                        npc.getInventory().storeItem(40506, 1);
                    }

                    if (!npc.getInventory().checkItem(40507, 96 * CodeConfig.RATE_CRAFT)) {
                        npc.getInventory().storeItem(40507, CodeConfig.RATE_CRAFT);
                    }

                }
                if (npc.getTemplate().getNpcId() == 70850) { // 판
                    if (!npc.getInventory().checkItem(40519, 60 * CodeConfig.RATE_CRAFT)) {
                        npc.getInventory().storeItem(40519, CodeConfig.RATE_CRAFT);
                    }
                }
            }
        });

        fireWallList.stream().filter(Objects::nonNull).forEach(npc -> {
            if (npc.destroyed) {
                removeFireWall(npc);
                return;
            }

            fireWall(npc);
        });
    }

    public void addNpc(L1NpcInstance npc) {
        if (!list.contains(npc))
            list.add(npc);
    }

    public void removeNpc(L1NpcInstance npc) {
        if (list.contains(npc)) {
            list.remove(npc);
            if (npc != null)
                npc.restTime = 0;
        }
    }

    public void addDoor(L1DoorInstance npc) {
        if (!doorCloseList.contains(npc))
            doorCloseList.add(npc);
    }

    public void removeDoor(L1DoorInstance npc) {
        if (doorCloseList.contains(npc)) {
            doorCloseList.remove(npc);

            if (npc != null)
                npc.closeTime = 0;
        }
    }

    public void addGuardian(L1GuardianInstance npc) {
        if (!restoreList.contains(npc))
            restoreList.add(npc);
    }

    public void removeGuardian(L1GuardianInstance npc) {
        if (restoreList.contains(npc)) {
            restoreList.remove(npc);
            if (npc != null)
                npc.restoreTime = 0;
        }
    }

    public void addFireWall(L1EffectInstance npc) {
        if (!fireWallList.contains(npc))
            fireWallList.add(npc);
    }

    public void removeFireWall(L1EffectInstance npc) {
        fireWallList.remove(npc);
    }

    private void fireWall(L1EffectInstance effect) {
        L1World.getInstance()
                .getVisibleObjects(effect, 0)
                .stream()
                .filter(Objects::nonNull)
                .forEach(objects -> {
                    if (objects instanceof L1PcInstance) {
                        L1PcInstance pc = (L1PcInstance) objects;

                        if (pc.isDead()) {
                            return;
                        }

                        if (pc.getId() == effect.getSpawner().getId()) {
                            return;
                        }

                        if (L1CharPosUtils.isSafeZone(pc)) {
                            if (!L1CastleLocation.isNowWarByArea(pc)) {
                                return;
                            }
                        }

                        int damage = L1MagicUtils.calcPcFireWallDamage(pc);

                        if (damage == 0) {
                            return;
                        }

                        pc.sendPackets(new S_DoActionGFX(pc.getId(), L1ActionCodes.ACTION_Damage));
                        Broadcaster.broadcastPacket(pc, new S_DoActionGFX(pc.getId(), L1ActionCodes.ACTION_Damage));
                        pc.receiveDamage(effect, damage);
                    } else if (objects instanceof L1MonsterInstance) {
                        L1MonsterInstance mob = (L1MonsterInstance) objects;

                        if (mob.isDead()) {
                            return;
                        }

                        int damage = L1MagicUtils.calcNpcFireWallDamage(mob);

                        if (damage == 0) {
                            return;
                        }

                        Broadcaster.broadcastPacket(mob, new S_DoActionGFX(mob.getId(), L1ActionCodes.ACTION_Damage));
                        mob.receiveDamage(effect, damage);
                    }
                });
    }
}
