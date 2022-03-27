package ks.model;

import ks.app.LineageAppContext;
import ks.app.config.prop.CodeConfig;
import ks.core.datatables.CastleTable;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_ServerMessage;
import ks.packets.serverpackets.S_War;
import ks.util.common.DateUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class L1War {
    private final List<String> attackClanList = new ArrayList<>();
    private String param1 = null;
    private String param2 = null;
    private String defenceClanName = null;

    private int warType = 0;

    private Calendar warEndTime;

    private boolean isWarTimerDelete = false;

    public L1War() {
    }

    public void handleCommands(int warType, String attackClanName, String defenceClanName) {
        SetWarType(warType);
        declareWar(attackClanName, defenceClanName);

        param1 = attackClanName;
        param2 = defenceClanName;

        initAttackClan();
        addAttackClan(attackClanName);
        setDefenceClanName(defenceClanName);

        if (warType == 1) {
            getCastleId();
            L1Castle castle = getCastle();

            if (castle != null) {
                Calendar cal = (Calendar) castle.getWarTime().clone();
                cal.add(CodeConfig.ALT_WAR_TIME_UNIT, CodeConfig.CASTLE_WAR_TIME);
                warEndTime = cal;
            }

            CastleWarTimer castleWarTimer = new CastleWarTimer();
            LineageAppContext.commonTaskScheduler().execute(castleWarTimer);
        } else if (warType == 2) {
            SimWarTimer simWarTimer = new SimWarTimer();
            LineageAppContext.commonTaskScheduler().execute(simWarTimer);
        }

        L1World.getInstance().addWar(this);
    }

    private void requestCastleWar(int type, String clan1Name, String clan2Name) {
        if (clan1Name == null || clan2Name == null) {
            return;
        }

        L1Clan clan1 = L1World.getInstance().getClan(clan1Name);

        if (clan1 != null) {
            List<L1PcInstance> onlineClanMember = clan1.getOnlineClanMember();

            for (L1PcInstance pc : onlineClanMember) {
                pc.sendPackets(new S_War(type, clan1Name, clan2Name));
            }
        }

        int attackClanNum = getAttackClanListSize();

        if (type == 1 || type == 2 || type == 3) {
            L1Clan clan2 = L1World.getInstance().getClan(clan2Name);

            if (clan2 != null) {
                List<L1PcInstance> onlineClanMembers = clan2.getOnlineClanMember();
                for (L1PcInstance onlineClanMember : onlineClanMembers) {
                    if (type == 1) {
                        onlineClanMember.sendPackets(new S_War(type, clan1Name, clan2Name));
                    } else if (type == 2) {
                        onlineClanMember.sendPackets(new S_War(type, clan1Name, clan2Name));
                        if (attackClanNum == 1) {
                            onlineClanMember.sendPackets(new S_War(4, clan2Name, clan1Name));
                        } else {
                            onlineClanMember.sendPackets(new S_ServerMessage(228, clan1Name, clan2Name));
                            removeAttackClan(clan1Name);
                        }
                    } else {
                        onlineClanMember.sendPackets(new S_War(type, clan1Name, clan2Name));
                        if (attackClanNum == 1) {
                            onlineClanMember.sendPackets(new S_War(4, clan2Name, clan1Name));
                        } else {
                            onlineClanMember.sendPackets(new S_ServerMessage(227, clan1Name, clan2Name));
                            removeAttackClan(clan1Name);
                        }
                    }
                }
            }
        }

        if ((type == 2 || type == 3) && attackClanNum == 1) {
            isWarTimerDelete = true;
            delete();
        }
    }

    private void requestSimWar(int type, String clan1_name, String clan2_name) {
        if (clan1_name == null || clan2_name == null) {
            return;
        }

        L1Clan clan1 = L1World.getInstance().getClan(clan1_name);

        if (clan1 != null) {
            List<L1PcInstance> clan1OnlineClanMembers = clan1.getOnlineClanMember();

            for (L1PcInstance member : clan1OnlineClanMembers) {
                member.sendPackets(new S_War(type, clan1_name, clan2_name));
            }
        }

        if (type == 1 || type == 2 || type == 3) {
            L1Clan clan2 = L1World.getInstance().getClan(clan2_name);

            if (clan2 != null) {
                List<L1PcInstance> clan2OnlineClanMember = clan2.getOnlineClanMember();
                for (L1PcInstance pcInstance : clan2OnlineClanMember) {
                    if (type == 1) {
                        pcInstance.sendPackets(new S_War(type, clan1_name, clan2_name));
                    } else {
                        pcInstance.sendPackets(new S_War(type, clan1_name, clan2_name));
                        pcInstance.sendPackets(new S_War(4, clan2_name, clan1_name));
                    }
                }
            }
        }

        if (type == 2 || type == 3) {
            isWarTimerDelete = true;
            delete();
        }
    }

    public void winCastleWar(String winnerClan) {
        String defenceClanName = getDefenceClanName();
        L1World.getInstance().broadcastPacketToAll(new S_ServerMessage(231, winnerClan, defenceClanName));

        L1Clan defenceClan = L1World.getInstance().getClan(defenceClanName);

        if (defenceClan != null) {
            List<L1PcInstance> defence_clan_member = defenceClan.getOnlineClanMember();
            for (L1PcInstance member : defence_clan_member) {
                for (String clanName : getAttackClanList()) {
                    member.sendPackets(new S_War(3, defenceClanName, clanName));
                }
            }
        }

        String[] clanList = getAttackClanList();

        for (String clanName : clanList) {
            if (clanName != null) {
                L1World.getInstance().broadcastPacketToAll(new S_ServerMessage(227, defenceClanName, clanName));
                L1Clan clan = L1World.getInstance().getClan(clanName);
                if (clan != null) {
                    List<L1PcInstance> clanMembers = clan.getOnlineClanMember();
                    for (L1PcInstance member : clanMembers) {
                        member.sendPackets(new S_War(3, clanName, defenceClanName));
                    }
                }
            }
        }

        setDefenceClanName(winnerClan);

        isWarTimerDelete = true;
        delete();
    }

    public void ceaseCastleWar() {
        String defenceClanName = getDefenceClanName();
        String[] clanList = getAttackClanList();

        if (defenceClanName != null) {
            L1World.getInstance().broadcastPacketToAll(new S_ServerMessage(231, defenceClanName, clanList[0]));
        }

        L1Clan onlineClanMember = L1World.getInstance().getClan(defenceClanName);

        if (onlineClanMember != null) {
            List<L1PcInstance> defence_clan_member = onlineClanMember.getOnlineClanMember();

            for (L1PcInstance l1PcInstance : defence_clan_member) {
                l1PcInstance.sendPackets(new S_War(4, defenceClanName, clanList[0]));
            }
        }

        for (String clanName : clanList) {
            if (clanName != null) {
                L1World.getInstance().broadcastPacketToAll(new S_ServerMessage(227, defenceClanName, clanName));
                L1Clan clan = L1World.getInstance().getClan(clanName);

                if (clan != null) {
                    List<L1PcInstance> clan_member = clan.getOnlineClanMember();
                    for (L1PcInstance l1PcInstance : clan_member) {
                        l1PcInstance.sendPackets(new S_War(3, clanName, defenceClanName));
                    }
                }
            }
        }

        isWarTimerDelete = true;
        delete();
    }

    public void declareWar(String clan1Name, String clan2Name) {
        if (getWarType() == 1) {
            requestCastleWar(1, clan1Name, clan2Name);
        } else {
            requestSimWar(1, clan1Name, clan2Name);
        }
    }

    public void surrenderWar(String clan1Name, String clan2Name) {
        if (getWarType() == 1) {
            requestCastleWar(2, clan1Name, clan2Name);
        } else {
            requestSimWar(2, clan1Name, clan2Name);
        }
    }

    public void ceaseWar(String clan1_name, String clan2_name) {
        if (getWarType() == 1) {
            requestCastleWar(3, clan1_name, clan2_name);
        } else {
            requestSimWar(3, clan1_name, clan2_name);
        }
    }

    public boolean checkClanInWar(String clanName) {
        if (getDefenceClanName().toLowerCase().equals(clanName.toLowerCase())) {
            return true;
        } else {
            return checkAttackClan(clanName);
        }
    }

    public boolean checkClanInSameWar(String playerClanName, String targetClanName) {
        boolean playerClanFlag;
        boolean targetClanFlag;

        if (getDefenceClanName().toLowerCase().equals(playerClanName.toLowerCase())) {
            playerClanFlag = true;
        } else {
            playerClanFlag = checkAttackClan(playerClanName);
        }

        if (getDefenceClanName().toLowerCase().equals(targetClanName.toLowerCase())) {
            targetClanFlag = true;
        } else {
            targetClanFlag = checkAttackClan(targetClanName);
        }

        return playerClanFlag && targetClanFlag;
    }

    public String getEnemyClanName(String playerClanName) {
        if (getDefenceClanName().toLowerCase().equals(playerClanName.toLowerCase())) {
            String[] clanList = getAttackClanList();

            for (String s : clanList) {
                if (s != null) {
                    return s;
                }
            }
        } else {
            return getDefenceClanName();
        }

        return null;
    }

    public void delete() {
        L1World.getInstance().removeWar(this);
    }

    public int getWarType() {
        return warType;
    }

    public void SetWarType(int war_type) {
        warType = war_type;
    }

    public String getDefenceClanName() {
        return defenceClanName;
    }

    public void setDefenceClanName(String defence_clan_name) {
        defenceClanName = defence_clan_name;
    }

    public void initAttackClan() {
        attackClanList.clear();
    }

    public void addAttackClan(String attack_clan_name) {
        if (!attackClanList.contains(attack_clan_name)) {
            attackClanList.add(attack_clan_name);
        }
    }

    public void removeAttackClan(String attack_clan_name) {
        attackClanList.remove(attack_clan_name);
    }

    public boolean checkAttackClan(String attackClanName) {
        return attackClanList.contains(attackClanName);
    }

    public String[] getAttackClanList() {
        return attackClanList.toArray(new String[attackClanList.size()]);
    }

    public int getAttackClanListSize() {
        return attackClanList.size();
    }

    public int getCastleId() {
        if (getWarType() == 1) {
            L1Clan clan = L1World.getInstance().getClan(getDefenceClanName());

            if (clan != null) {
                return clan.getCastleId();
            }
        }

        return 0;
    }

    public L1Castle getCastle() {
        if (getWarType() == 1) {
            L1Clan clan = L1World.getInstance().getClan(getDefenceClanName());

            if (clan != null) {
                int castle_id = clan.getCastleId();

                return CastleTable.getInstance().getCastleTable(castle_id);
            }
        }

        return null;
    }

    class CastleWarTimer implements Runnable {
        public void run() {
            for (; ; ) {
                try {
                    Thread.sleep(1000);
                    if (warEndTime.before(DateUtils.getRealTimeCalendar())) {
                        break;
                    }
                } catch (Exception exception) {
                    break;
                }
                if (isWarTimerDelete) {
                    return;
                }
            }

            ceaseCastleWar();
            delete();
        }
    }

    public class SimWarTimer implements Runnable {
        public void run() {
            for (int loop = 0; loop < 240; loop++) {
                try {
                    Thread.sleep(1000 * 60);
                } catch (Exception exception) {
                    break;
                }
                if (isWarTimerDelete) {
                    return;
                }
            }

            ceaseWar(param1, param2);
            delete();
        }
    }
}
