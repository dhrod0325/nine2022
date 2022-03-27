package ks.util;

import ks.app.LineageAppContext;
import ks.app.config.prop.CodeConfig;
import ks.constants.L1SkillId;
import ks.core.datatables.CastleTable;
import ks.core.datatables.clan.ClanTable;
import ks.model.*;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_Message_YN;
import ks.packets.serverpackets.S_ServerMessage;
import ks.packets.serverpackets.S_SystemMessage;
import ks.scheduler.WarTimeScheduler;

import java.io.File;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ScheduledFuture;

public class L1WarUtils {
    private static final L1WarUtils instance = new L1WarUtils();

    private final Map<Integer, DefenseTimer> timerMap = new HashMap<>();
    private int repeatCount;

    public static L1WarUtils getInstance() {
        return instance;
    }

    public static void war(L1PcInstance pc, String targetClanName, int type) {
        if (pc == null) {
            return;
        }

        String clanName = pc.getClanName();
        int clanId = pc.getClanId();

        if (!pc.isCrown()) {
            pc.sendPackets(new S_ServerMessage(478));
            return;
        }

        if (clanId == 0) {
            pc.sendPackets(new S_ServerMessage(272));
            return;
        }

        L1Clan clan = L1World.getInstance().getClan(clanName);

        if (clan == null) {
            pc.sendPackets(new S_SystemMessage(clanName + " 혈맹이 존재하지 않습니다."));
            return;
        }

        if (pc.getId() != clan.getLeaderId()) {
            pc.sendPackets(new S_ServerMessage(478));
            return;
        }

        if (clan.getCastleId() != 0) {
            pc.sendPackets("이미 성을 소유하고 있습니다");
            return;
        }

        if (clanName.equalsIgnoreCase(targetClanName)) {
            pc.sendPackets("자신의 혈맹에게 선포가 불가능합니다");
            return;
        }

        L1Clan enemyClan = null;
        String enemyClanName = null;

        for (L1Clan checkClan : L1World.getInstance().getAllClans()) {
            if (checkClan.getClanName().equalsIgnoreCase(targetClanName)) {
                enemyClan = checkClan;
                enemyClanName = checkClan.getClanName();
                break;
            }
        }

        if (enemyClan == null) {
            pc.sendPackets(targetClanName + " 혈맹은 존재하지 않습니다");
            return;
        }

        if (clan.getAlliance() == enemyClan.getClanId()) {
            pc.sendPackets(new S_ServerMessage(1205));
            return;
        }

        boolean inWar = false;

        Collection<L1War> warList = L1World.getInstance().getWarList();

        for (L1War war : warList) {
            if (war.checkClanInWar(clanName)) {
                if (type == 0) {
                    pc.sendPackets(new S_ServerMessage(234));
                    return;
                }

                inWar = true;
                break;
            }
        }

        if (!inWar && (type == 2 || type == 3)) {
            return;
        }

        if (clan.getCastleId() != 0) {
            if (type == 0) {
                pc.sendPackets(new S_ServerMessage(474));
                return;
            } else if (type == 2 || type == 3) {
                return;
            }
        }

        if (enemyClan.getCastleId() == 0 && pc.getLevel() <= 15) {
            pc.sendPackets(new S_ServerMessage(232));
            return;
        }

        if (enemyClan.getCastleId() != 0 && pc.getLevel() < CodeConfig.CASTLE_WAR_MIN_CROWN_LEVEL) {
            pc.sendPackets(new S_SystemMessage("공성전을 선언하려면  레벨 " + CodeConfig.CASTLE_WAR_MIN_CROWN_LEVEL + "에 이르지 않으면 안됩니다."));
            return;
        }

        File file = new File("data/emblem/" + clan.getEmblemId());

        if (!file.exists()) {
            pc.sendPackets(new S_SystemMessage("혈마크 없이는 전쟁을 포고할 수 없습니다."));
            return;
        }

        if (!pc.isGm() && clan.getOnlineClanMember().size() <= CodeConfig.CASTLE_WAR_MIN_MEMBER_CNT) {
            pc.sendPackets(new S_SystemMessage("현재 접속한 혈맹 구성원이 [" + CodeConfig.CASTLE_WAR_MIN_MEMBER_CNT + "]명 이상 되어야 공성전 선포가 가능합니다."));
            return;
        }

        if (clan.getHouseId() == 0) {
            pc.sendPackets(new S_SystemMessage("아지트가 없는 상태에서는 선전 포고를 할 수 없습니다."));
            return;
        }

        if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.SHAPE_CHANGE)) {
            pc.sendPackets("변신 상태에서 전쟁을 선포할 수 없습니다");
            return;
        }

        if (enemyClan.getCastleId() != 0) {
            int castleId = enemyClan.getCastleId();

            if (WarTimeScheduler.getInstance().isNowWar(castleId)) {
                List<L1PcInstance> clanMember = clan.getOnlineClanMember();

                for (L1PcInstance member : clanMember) {
                    if (L1CastleLocation.checkInWarArea(castleId, member)) {
                        pc.sendPackets(new S_ServerMessage(477));
                        return;
                    }
                }

                boolean enemyInWar = false;

                for (L1War war : warList) {
                    if (war.checkClanInWar(enemyClanName)) {
                        if (type == 0) {
                            war.declareWar(clanName, enemyClanName);
                            war.addAttackClan(clanName);
                        } else if (type == 2 || type == 3) {
                            if (!war.checkClanInSameWar(clanName, enemyClanName)) {
                                return;
                            }

                            if (type == 2) { // 항복
                                war.surrenderWar(clanName, enemyClanName);
                            } else { // 종결
                                war.ceaseWar(clanName, enemyClanName);
                            }
                        }
                        enemyInWar = true;
                        break;
                    }
                }

                if (!enemyInWar && type == 0) {
                    L1War war = new L1War();
                    war.handleCommands(1, clanName, enemyClanName);
                }
            } else {
                if (type == 0) {
                    pc.sendPackets(new S_ServerMessage(476));
                }
            }
        } else {
            boolean enemyInWar = false;

            for (L1War war : warList) {
                if (war.checkClanInWar(enemyClanName)) {
                    if (type == 0) {
                        pc.sendPackets(new S_ServerMessage(236, enemyClanName));
                        return;
                    } else if (type == 2 || type == 3) { // 항복 또는 종결
                        if (!war.checkClanInSameWar(clanName, enemyClanName)) {
                            return;
                        }
                    }
                    enemyInWar = true;
                    break;
                }
            }

            if (!enemyInWar && (type == 2 || type == 3)) {
                return;
            }

            L1PcInstance enemyLeader = L1World.getInstance().getPlayer(enemyClan.getLeaderName());

            if (enemyLeader == null) {
                pc.sendPackets(new S_ServerMessage(218, enemyClanName));
                return;
            }

            if (type == 0) {
                enemyLeader.setTempID(pc.getId());
                enemyLeader.sendPackets(new S_Message_YN(217, clanName, pc.getName()));
            } else if (type == 2) { // 항복
                enemyLeader.setTempID(pc.getId()); // 상대의 오브젝트 ID를 보존해 둔다
                enemyLeader.sendPackets(new S_Message_YN(221, clanName));
            } else if (type == 3) { // 종결
                enemyLeader.setTempID(pc.getId());
                enemyLeader.sendPackets(new S_Message_YN(222, clanName));
            }
        }
    }

    public void startDefenseTimer(int castleId) {
        DefenseTimer defenseTimer = timerMap.getOrDefault(castleId, new DefenseTimer());
        defenseTimer.setCastleId(castleId);
        defenseTimer.start();
        timerMap.put(castleId, defenseTimer);
    }

    public void stopDefenseTimer(int castleId) {
        DefenseTimer defenseTimer = timerMap.getOrDefault(castleId, new DefenseTimer());
        defenseTimer.setCastleId(castleId);
        defenseTimer.stop();
    }

    private class DefenseTimer implements Runnable {
        private long startTime;

        private int castleId;
        private ScheduledFuture<?> future;

        public void setCastleId(int castleId) {
            this.castleId = castleId;
        }

        public void start() {
            stop();

            startTime = System.currentTimeMillis();
            future = LineageAppContext.commonTaskScheduler().scheduleWithFixedDelay(this, Instant.now(), Duration.ofMillis(1000));
        }

        private void stop() {
            repeatCount = 0;

            if (future != null) {
                future.cancel(true);
                future = null;
            }
        }

        @Override
        public void run() {
            long duration = System.currentTimeMillis() - startTime;

            L1Castle castle = CastleTable.getInstance().getCastleTable(castleId);
            L1Clan leaderClan = ClanTable.getInstance().getCastleLeaderClan(castle.getId());

            Calendar warTime = castle.getWarTime();
            Calendar endTime = Calendar.getInstance();

            endTime.setTime(warTime.getTime());
            endTime.add(CodeConfig.ALT_WAR_TIME_UNIT, CodeConfig.CASTLE_WAR_TIME);

            long realRemainingMinute = endTime.getTime().getTime() - System.currentTimeMillis();
            realRemainingMinute = realRemainingMinute / 1000 / 60;

            long maxDefenseMill = CodeConfig.CASTLE_WAR_DEFENSE_MAX_MINUTE * 1000L * 60;
            long remainingMill = maxDefenseMill - duration;
            long remainingSecond = (remainingMill / 1000);
            long remainingMinute = (long) Math.ceil(remainingSecond / 60.0);

            if (realRemainingMinute <= remainingMinute) {
                remainingMinute = realRemainingMinute;
                remainingSecond = remainingMinute * 60;
            }

            if (remainingSecond > 30) {
                if (repeatCount % 60 == 0) {
                    String msg = leaderClan.getClanName() + " 혈맹의 남은 수성시간 : " + remainingMinute + "분";
                    L1World.getInstance().broadcastPacketGreenMessage(msg);
                    L1World.getInstance().broadcastServerMessage(msg);
                }
            } else {
                if (remainingSecond > 0 && repeatCount % 5 == 0) {
                    String msg = leaderClan.getClanName() + " 혈맹의 남은 수성시간 : " + remainingSecond + "초";
                    L1World.getInstance().broadcastPacketGreenMessage(msg);
                    L1World.getInstance().broadcastServerMessage(msg);
                }
            }

            if (duration >= maxDefenseMill) {
                stop();
                WarTimeScheduler.getInstance().stopWar(castle.getName());
            }

            repeatCount++;
        }
    }
}
