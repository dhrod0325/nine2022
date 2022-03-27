package ks.scheduler;

import ks.app.LineageAppContext;
import ks.app.config.prop.CodeConfig;
import ks.constants.L1PacketBoxType;
import ks.core.datatables.CastleTable;
import ks.core.datatables.DoorSpawnTable;
import ks.core.datatables.clan.ClanTable;
import ks.core.datatables.pc.CharacterTable;
import ks.core.network.opcode.L1Opcodes;
import ks.model.*;
import ks.model.instance.L1CrownInstance;
import ks.model.instance.L1DoorInstance;
import ks.model.instance.L1FieldObjectInstance;
import ks.model.instance.L1TowerInstance;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_CastleMaster;
import ks.packets.serverpackets.S_PacketBox;
import ks.packets.serverpackets.S_SystemMessage;
import ks.packets.serverpackets.ServerPacket;
import ks.util.L1WarUtils;
import ks.util.common.DateUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;

@Component
public class WarTimeScheduler {
    private static final Logger logger = LogManager.getLogger();

    private final L1Castle[] castles = new L1Castle[8];

    private final Calendar[] warStartTime = new Calendar[8];

    private final Calendar[] warEndTime = new Calendar[8];

    private final boolean[] nowWar = new boolean[8];

    public static WarTimeScheduler getInstance() {
        return LineageAppContext.getBean(WarTimeScheduler.class);
    }

    @Scheduled(fixedDelay = 1000)
    public void run() {
        if (!LineageAppContext.isRun()) {
            return;
        }

        try {
            checkWarTime(); // 전쟁 시간을 체크
        } catch (Exception e) {
            logger.error("오류", e);
        }
    }

    public void timeCheck() {
        for (int i = 0; i < castles.length; i++) {
            castles[i] = CastleTable.getInstance().getCastleTable(i + 1);
            warStartTime[i] = castles[i].getWarTime();
            warEndTime[i] = (Calendar) castles[i].getWarTime().clone();
            warEndTime[i].add(CodeConfig.ALT_WAR_TIME_UNIT, CodeConfig.CASTLE_WAR_TIME);
        }
    }

    public void setWarStartTime(String name, Calendar cal) {
        if (name == null) {
            return;
        }

        if (name.length() == 0) {
            return;
        }

        Collection<L1PcInstance> players = L1World.getInstance().getAllPlayers();

        for (int i = 0; i < castles.length; i++) {
            L1Castle castle = castles[i];
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");

            if (castle.getName().startsWith(name)) {
                castle.setWarTime(cal);
                warStartTime[i] = (Calendar) cal.clone();
                warEndTime[i] = (Calendar) cal.clone();
                warEndTime[i].add(CodeConfig.ALT_WAR_TIME_UNIT, CodeConfig.CASTLE_WAR_TIME);

                // 모든 유저에게 공성시간 알리기
                for (L1PcInstance pc : players) {
                    pc.sendPackets(new S_SystemMessage(String.format("%s공성시간: %s ~ %s", castle.getName(), formatter.format(warStartTime[i].getTime()), formatter.format(warEndTime[i].getTime()))));
                }
            }
        }
    }

    public void reload() {
        timeCheck();
    }

    public boolean isNowWar(int castleId) {
        if (castleId == 0)
            return false;

        return nowWar[castleId - 1];
    }

    public boolean isTodayWar(int castleId) {
        if (castleId == 0)
            return false;

        return nowWar[castleId - 1];
    }

    public void checkCastleWar(L1PcInstance player) {
        for (int i = 0; i < 8; i++) {
            if (nowWar[i]) {
                player.sendPackets(new S_PacketBox(L1PacketBoxType.MSG_WAR_GOING, i + 1));
            }
        }
    }

    private void checkWarTime() {
        Calendar realTime = DateUtils.getRealTimeCalendar();

        for (int i = 0; i < 8; i++) {
            try {
                L1Castle castle = castles[i];

                if (warStartTime[i].before(realTime) && warEndTime[i].after(realTime)) {
                    startCastleWar(i, castle);
                } else { // 전쟁 종료
                    if (nowWar[i]) {
                        stopCastleWar(i, castle);
                    }
                }
            } catch (Exception e) {
                logger.error(e);
            }
        }
    }

    private void startCastleWar(int i, L1Castle castle) {
        if (!nowWar[i]) {
            nowWar[i] = true;

            L1WarSpawn warSpawn = new L1WarSpawn();
            warSpawn.spawnFlag(i + 1);

            for (L1DoorInstance door : DoorSpawnTable.getInstance().getDoorList()) {
                if (L1CastleLocation.checkInWarArea(i + 1, door)) {
                    door.setAutoStatus(0);
                    door.repairGate();
                }
            }

            if (castles[i].getCastleSecurity() == 1) {
                securityStart(castles[i]);
            }

            L1Clan leaderClan = ClanTable.getInstance().getCastleLeaderClan(castle.getId());
            String startMsg = castle.getName() + "의 공성전이 시작되었습니다. 혈맹주 : " + leaderClan.getClanName();

            L1World.getInstance().broadcastPacketToAll(new S_PacketBox(L1PacketBoxType.MSG_WAR_BEGIN, i + 1));
            L1World.getInstance().broadcastPacketToAll(new S_PacketBox(L1PacketBoxType.GREEN_MESSAGE, startMsg));
            L1World.getInstance().broadcastServerMessage(startMsg);

            Collection<L1PcInstance> players = L1World.getInstance().getAllPlayers();

            try {
                ServerPacket packet = new ServerPacket();
                packet.writeC(L1Opcodes.S_OPCODE_BOARDREAD);
                packet.writeD(0);
                packet.writeS("공성전안내");
                packet.writeS("공성전안내");
                packet.writeS("");

                String msg = "# 공성전 선포 \r\n\r\n";
                msg += ".전쟁 " + leaderClan.getClanName() + "\r\n\r\n";

                msg += "# 공성전 진행 \r\n\r\n";
                msg += "수호탑파괴 -> 면류관 클릭 \r\n";
                msg += "무기 착용 면류관 클릭 안됨\r\n";
                msg += "변신상태 면류관 클릭 안됨";

                packet.writeS(msg);

                L1World.getInstance().broadcastPacketToAll(packet);
            } catch (Exception e) {
                logger.error(e);
            }

            players.stream().filter(L1PcInstance::isGm).forEach(pc -> {
                int castleId = i + 1;

                if (L1CastleLocation.checkInWarArea(castleId, pc)) {
                    teleportBack(pc, castleId);
                }
            });

            L1WarUtils.getInstance().startDefenseTimer(castle.getId());
        }
    }

    private void teleportBack(L1PcInstance pc, int castleId) {
        L1Clan clan = L1World.getInstance().getClan(pc.getClanName());

        if (clan != null) {
            if (clan.getCastleId() == castleId) {
                return;
            }
        }

        int[] loc = L1CastleLocation.getGetBackLoc(castleId);
        L1Teleport.teleport(pc, loc[0], loc[1], (short) loc[2], 5, true);
    }

    //공성종료
    public void stopWar(String name) {
        if (name == null) {
            return;
        }

        if (name.length() == 0) {
            return;
        }

        for (int i = 0; i < castles.length; i++) {
            L1Castle castle = castles[i];

            if (castle.getName().startsWith(name)) {
                stopCastleWar(i, castle);
            }
        }
    }

    private void stopCastleWar(int i, L1Castle castle) {
        nowWar[i] = false;

        warStartTime[i].add(CodeConfig.ALT_WAR_INTERVAL_UNIT, CodeConfig.CASTLE_WAR_TIME_INTERVAL);
        warEndTime[i].add(CodeConfig.ALT_WAR_INTERVAL_UNIT, CodeConfig.CASTLE_WAR_TIME_INTERVAL);
        castles[i].setTaxRate(0);
        castles[i].setPublicMoney(castles[i].getPublicMoney() + CodeConfig.CASTLE_WAR_WINNER_ADENA);
        castles[i].setWarTime(warStartTime[i]);

        CastleTable.getInstance().updateCastle(castles[i]);

        int castleId = i + 1;

        for (L1Object l1object : L1World.getInstance().getAllObject()) {
            if (l1object instanceof L1FieldObjectInstance) {
                L1FieldObjectInstance flag = (L1FieldObjectInstance) l1object;
                if (L1CastleLocation.checkInWarArea(castleId, flag)) {
                    flag.deleteMe();
                }
            }

            if (l1object instanceof L1CrownInstance) {
                L1CrownInstance crown = (L1CrownInstance) l1object;
                if (L1CastleLocation.checkInWarArea(castleId, crown)) {
                    crown.deleteMe();
                }
            }

            if (l1object instanceof L1TowerInstance) {
                L1TowerInstance tower = (L1TowerInstance) l1object;
                if (L1CastleLocation.checkInWarArea(castleId, tower)) {
                    tower.deleteMe();
                }
            }
        }

        L1WarSpawn warSpawn = new L1WarSpawn();
        warSpawn.spawnTower(castleId);

        for (L1DoorInstance door : DoorSpawnTable.getInstance().getDoorList()) {
            if (L1CastleLocation.checkInWarArea(castleId, door)) {
                door.repairGate();
            }
        }

        for (L1War war : L1World.getInstance().getWarList()) {
            if (war.getCastleId() == castleId) {
                war.ceaseCastleWar();
                break;
            }
        }

        ClanTable.getInstance().load();

        L1WarUtils.getInstance().stopDefenseTimer(castle.getId());

        L1Clan leaderClan = ClanTable.getInstance().getCastleLeaderClan(castle.getId());

        L1World.getInstance().broadcastPacketToAll(new S_PacketBox(L1PacketBoxType.MSG_WAR_END, i + 1));
        L1World.getInstance().broadcastPacketToAll(new S_PacketBox(L1PacketBoxType.GREEN_MESSAGE, castle.getName() + "의 공성전이 종료되었습니다. 승리혈맹 : " + leaderClan.getClanName()));

        for (L1PcInstance pc : leaderClan.getOnlineClanMember()) {
            pc.sendPackets("성혈 아데나관리NPC에게 " + NumberFormat.getInstance().format(CodeConfig.CASTLE_WAR_WINNER_ADENA) + "아데나가 입금되었습니다");
        }

        L1World.getInstance().broadcastPacketToAll(new S_CastleMaster(castle.getId(), leaderClan.getLeaderId()));
    }

    //공성종료
    private void securityStart(L1Castle castle) {
        int castleId = castle.getId();
        int a = 0, b = 0, c = 0, d = 0, e = 0;

        switch (castleId) {
            case 1:
            case 2:
            case 3:
            case 4:
                a = 52;
                b = 248;
                c = 249;
                d = 250;
                e = 251;
                break;
            case 5:
            case 6:
            case 7:
            default:
                break;
        }

        Collection<L1PcInstance> players = L1World.getInstance().getAllPlayers();

        for (L1PcInstance pc : players) {
            if ((pc.getMapId() == a
                    || pc.getMapId() == b
                    || pc.getMapId() == c
                    || pc.getMapId() == d
                    || pc.getMapId() == e)
                    && !pc.isGm()) {
                teleportBack(pc, castleId);
            }
        }

        castle.setCastleSecurity(0);
        CastleTable.getInstance().updateCastle(castle);
        CharacterTable.getInstance().updateLoc(castleId, a, b, c, d, e);
    }
}
