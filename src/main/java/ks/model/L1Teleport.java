package ks.model;

import ks.app.LineageAppContext;
import ks.app.config.prop.CodeConfig;
import ks.app.event.TeleportEvent;
import ks.constants.L1PacketBoxType;
import ks.constants.L1SkillId;
import ks.model.instance.*;
import ks.model.map.L1Map;
import ks.model.map.L1WorldMap;
import ks.model.pc.L1PcInstance;
import ks.model.warehouse.ClanWarehouse;
import ks.model.warehouse.WarehouseManager;
import ks.packets.serverpackets.*;
import ks.util.L1StatusUtils;
import ks.util.common.random.RandomUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashSet;
import java.util.List;

public class L1Teleport {
    public static final int EFFECT_SPR = 169;
    private static final Logger logger = LogManager.getLogger(L1Teleport.class);

    public static void teleport(L1PcInstance pc, L1Location loc, int head, boolean effect) {
        teleport(pc, loc.getX(), loc.getY(), (short) loc.getMapId(), head, effect);
    }

    public static void teleport(L1PcInstance pc, int x, int y, short mapId, int head, boolean effect) {
        teleport(pc, x, y, mapId, head, effect, 0);
    }

    public static void teleport(L1PcInstance pc, int x, int y, short mapId, int head, boolean effect, int randomRange) {
        LineageAppContext.commonTaskScheduler().execute(() -> {
            try {
                if (pc.isHuntMapAndNoHunt(mapId)) {
                    return;
                }

                if (pc.isDead() || pc.isTeleport()) {
                    return;
                }

                if (pc.isAutoKingBuff()) {
                    pc.sendPackets("자동군업 상태입니다");
                    return;
                }

                if (L1StatusUtils.isStatusLock(pc)) {
                    return;
                }

                if (pc.isFishing()) {
                    pc.endFishing();
                }

                int cX = x;
                int cY = y;

                if (randomRange > 0) {
                    cX = x + RandomUtils.nextInt(-randomRange, randomRange);
                    cY = y + RandomUtils.nextInt(-randomRange, randomRange);

                    L1Map m = pc.getMap();

                    for (int i = 0; i < 200; i++) {
                        if (!m.isPassable(cX, cY)) {
                            cX = x + RandomUtils.nextInt(-randomRange, randomRange);
                            cY = y + RandomUtils.nextInt(-randomRange, randomRange);
                        } else {
                            break;
                        }
                    }
                }

                pc.setTeleport(true);

                pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK, false));

                int oldMap = pc.getMapId();

                pc.setTeleportX(cX);
                pc.setTeleportY(cY);
                pc.setTeleportMapId(mapId);
                pc.setTeleportHeading(head);

                if (effect) {
                    pc.sendPackets(new S_SkillSound(pc.getId(), EFFECT_SPR));
                    Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), EFFECT_SPR));
                    Thread.sleep(CodeConfig.TELEPORT_SLEEP);
                }

                doTeleportation(pc);

                if (oldMap != (int) mapId) {
                    onChangeMap(pc, oldMap, mapId);
                }

                pc.setTeleport(false);
            } catch (Exception e) {
                logger.error(e);
            }
        });

    }

    private static void onChangeMap(L1PcInstance pc, int oldMap, int newmap) {
        if (newmap == L1Map.MAP_FISHING) {
            if (pc.getCurrentDoll() != null) {
                pc.getCurrentDoll().deleteDoll();
            }

            for (int itemId = 421000; itemId <= 421023; itemId++) {
                if (pc.getInventory().checkEquipped(itemId)) {
                    L1ItemInstance item = pc.getInventory().findEquippedItemId(itemId);
                    pc.getInventory().setEquipped(item, false);
                }
            }
        }

//        if (newmap == 4) {
//            pc.getHunt(oldMap).setEndTime(new Date());
//        } else {
//            pc.startHunt(newmap);
//        }
    }

    public static void npcTeleport(L1NpcInstance npc, int x, int y, short newMap, int head, boolean effect) {
        if (effect) {
            Broadcaster.broadcastPacket(npc, new S_SkillSound(npc.getId(), EFFECT_SPR));
        }

        List<L1PcInstance> list = npc.getNearObjects().getKnownPlayers();

        for (L1PcInstance target : list) {
            target.sendPackets(new S_RemoveObject(npc.getId()));
        }

        npc.setX(x);
        npc.setY(y);
        npc.setHeading(head);

        L1World.getInstance().moveVisibleObject(npc, newMap);

        npc.setMap(newMap);

        for (L1PcInstance visiblePc : L1World.getInstance().getVisiblePlayer(npc)) {
            npc.onPerceive(visiblePc);
        }
    }

    private static void doTeleportation(L1PcInstance pc) {
        int x = 0, y = 0, head = 0;
        short mapId = 4;

        try {
            x = pc.getTeleportX();
            y = pc.getTeleportY();
            mapId = pc.getTeleportMapId();
            head = pc.getTeleportHeading();

            L1Map map = L1WorldMap.getInstance().getMap(mapId);
            int tile = map.getTile(x, y);

            if ((tile == 0 || tile == 4 || tile == 12 || !map.isInMap(x, y)) && mapId != 4 && !pc.isGm()) { //원본
                x = pc.getX();
                y = pc.getY();
                mapId = pc.getMapId();
            }

            L1Clan clan = L1World.getInstance().getClan(pc.getClanName());

            if (clan != null) {
                ClanWarehouse clanWarehouse = WarehouseManager.getInstance().getClanWarehouse(clan.getClanName());

                if (clanWarehouse != null) {
                    clanWarehouse.unlock(pc.getId());
                }
            }

            List<L1PcInstance> list = pc.getNearObjects().getKnownPlayers();

            for (L1PcInstance target : list) {
                target.sendPackets(new S_RemoveObject(pc.getId()));
            }

            L1World.getInstance().moveVisibleObject(pc, mapId);

            pc.setLocation(x, y, mapId);
            pc.setHeading(head);
            pc.sendPackets(new S_MapID(pc.getMapId(), pc.getMap().isUnderwater()));

            if (pc.getMapId() != L1Map.MAP_2D) {
                Broadcaster.broadcastPacket(pc, new S_OtherCharPacks(pc));
            }

            pc.sendPackets(new S_OwnCharPack(pc));

            if (pc.isPinkName()) {
                pc.sendPackets(new S_PinkName(pc.getId(), pc.getSkillEffectTimerSet().getSkillEffectTimeSec(L1SkillId.STATUS_PINK_NAME)));
                Broadcaster.broadcastPacket(pc, new S_PinkName(pc.getId(), pc.getSkillEffectTimerSet().getSkillEffectTimeSec(L1SkillId.STATUS_PINK_NAME)));
            }

            pc.getNearObjects().removeAllKnownObjects();
            pc.sendVisualEffect();
            pc.updateObject();
            pc.sendPackets(new S_CharVisualUpdate(pc));

            pc.getSkillEffectTimerSet().killSkillEffectTimer(L1SkillId.MEDITATION);
            pc.setCallClanId(0);

            HashSet<L1PcInstance> subjects = new HashSet<>();
            subjects.add(pc);

            if (pc.getMap().isTakePets()) {
                for (L1NpcInstance petNpc : pc.getPetList().values()) {
                    L1Location loc = pc.getLocation().randomLocation(3, false);
                    int nx = loc.getX();
                    int ny = loc.getY();

                    npcTeleport(petNpc, nx, ny, mapId, head, false);

                    if (petNpc instanceof L1SummonInstance) {
                        L1SummonInstance summon = (L1SummonInstance) petNpc;
                        pc.sendPackets(new S_SummonPack(summon, pc));
                    } else if (petNpc instanceof L1PetInstance) {
                        L1PetInstance pet = (L1PetInstance) petNpc;
                        pc.sendPackets(new S_PetPack(pet, pc));
                    }
                }
            }

            if (pc.getCurrentDoll() != null) {
                L1Location loc = pc.getLocation().randomLocation(1, false);
                int nx = loc.getX();
                int ny = loc.getY();

                for (L1DollInstance doll : pc.getDollList().values()) {
                    npcTeleport(doll, nx, ny, mapId, head, false);
                    pc.sendPackets(new S_DollPack(doll));
                }
            }

            for (L1PcInstance up : subjects) {
                up.updateObject();
            }

            if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.STATUS_DRAGON_PERL)) {
                int remainingTime = pc.getSkillEffectTimerSet().getSkillEffectTimeSec(L1SkillId.STATUS_DRAGON_PERL);
                pc.sendPackets(new S_PacketBox(L1PacketBoxType.DRAGONPERL, 8, (remainingTime / 4) - 2));
                pc.sendPackets(new S_DragonPerl(pc.getId(), 8));
                pc.setDragonPerlSpeed(1);
            }

            if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.DANCING_BLADES)) {
                pc.sendPackets(new S_SkillBrave(pc.getId(), 1, pc.getSkillEffectTimerSet().getSkillEffectTimeSec(L1SkillId.DANCING_BLADES)));
                pc.sendPackets(new S_SkillIconAura(154, pc.getSkillEffectTimerSet().getSkillEffectTimeSec(L1SkillId.DANCING_BLADES)));
            }

            pc.getPierceCheck().resetMove();

            LineageAppContext.getCtx().publishEvent(new TeleportEvent(pc));
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("오류", e);
            logger.error(String.format("[teleport error] x:%d,y:%d,mapId:%d,head:%d", x, y, mapId, head));
        }
    }
}
