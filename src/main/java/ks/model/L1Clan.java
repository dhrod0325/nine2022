package ks.model;

import ks.model.pc.L1PcInstance;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class L1Clan {
    private final List<L1ClanMember> clanMemberList = new CopyOnWriteArrayList<>();

    private final List<String> gazeList = new ArrayList<>();

    private int clanId;
    private String clanName;
    private int leaderId;
    private String leaderName;
    private int castleId;
    private int houseId;
    private int alliance;
    private int maxUser;
    private int emblemId = 0;
    private int emblemStatus = 0;
    private String announcement;
    private int botStyle;
    private int botLevel;
    private double exp;
    private Timestamp clanBirthDay;

    public double getExp() {
        return exp;
    }

    public void setExp(double exp) {
        this.exp = exp;
    }

    public String getAnnouncement() {
        return announcement;
    }

    public void setAnnouncement(String announcement) {
        this.announcement = announcement;
    }

    public int getEmblemId() {
        return emblemId;
    }

    public void setEmblemId(int emblemId) {
        this.emblemId = emblemId;
    }

    public int getEmblemStatus() {
        return emblemStatus;
    }

    public void setEmblemStatus(int emblemStatus) {
        this.emblemStatus = emblemStatus;
    }

    public List<L1ClanMember> getClanMemberList() {
        return clanMemberList;
    }

    public void addClanMember(String name, int rank) {
        clanMemberList.add(new L1ClanMember(name, rank));
    }

    public void removeClanMember(String name) {
        for (L1ClanMember member : clanMemberList) {
            if (member.name.equalsIgnoreCase(name)) {
                clanMemberList.remove(member);
                break;
            }
        }
    }

    public int getOnlineMaxUser() {
        return maxUser;
    }

    public void setOnlineMaxUser(int i) {
        maxUser = i;
    }

    public void updataClanMember(String name, int rank) {
        for (L1ClanMember clanMember : clanMemberList) {
            if (clanMember.name.equals(name)) {
                clanMember.rank = rank;
                break;
            }
        }
    }

    public List<String> getAllMemberNames() {
        List<String> members = new ArrayList<>();

        for (L1ClanMember clanMember : clanMemberList) {
            if (!members.contains(clanMember.name)) {
                members.add(clanMember.name);
            }
        }

        return members;
    }

    public void setClanRank(String name, int data) {
        for (L1ClanMember clanMember : clanMemberList) {
            if (clanMember.name.equals(name)) {
                clanMember.rank = data;
                break;
            }
        }
    }

    public Timestamp getClanBirthDay() {
        return clanBirthDay;
    }

    public void setClanBirthDay(Timestamp ClanBirthDay) {
        clanBirthDay = ClanBirthDay;
    }

    public int getClanId() {
        return clanId;
    }

    public void setClanId(int clan_id) {
        clanId = clan_id;
    }

    public String getClanName() {
        return clanName;
    }

    public void setClanName(String clan_name) {
        clanName = clan_name;
    }

    public int getLeaderId() {
        return leaderId;
    }

    public void setLeaderId(int leader_id) {
        leaderId = leader_id;
    }

    public String getLeaderName() {
        return leaderName;
    }

    public void setLeaderName(String leader_name) {
        leaderName = leader_name;
    }

    public int getCastleId() {
        return castleId;
    }

    public void setCastleId(int hasCastle) {
        castleId = hasCastle;
    }

    public int getHouseId() {
        return houseId;
    }

    public void setHouseId(int hasHideout) {
        houseId = hasHideout;
    }

    public int getAlliance() {
        return alliance;
    }

    public void setAlliance(int alliance) {
        this.alliance = alliance;
    }

    public int getOnlineMemberCount() {
        int count = 0;

        for (L1ClanMember clanMember : clanMemberList) {
            if (L1World.getInstance().getPlayer(clanMember.name) != null) {
                count++;
            }
        }

        return count;
    }

    public List<L1PcInstance> getOnlineClanMember() {
        List<L1PcInstance> onlineMembers = new ArrayList<>();

        for (L1ClanMember clanMember : clanMemberList) {
            L1PcInstance pc = L1World.getInstance().getPlayer(clanMember.name);

            if (pc != null && !onlineMembers.contains(pc)) {
                onlineMembers.add(pc);
            }
        }

        return onlineMembers;
    }

    public String getOnlineClanMemberString() {
        StringBuilder result = new StringBuilder();
        for (L1ClanMember l1ClanMember : clanMemberList) {
            if (L1World.getInstance().getPlayer(l1ClanMember.name) != null) {
                result.append(l1ClanMember.name).append(" ");
            }
        }
        return result.toString();
    }

    public String getAllMembersString() {
        StringBuilder result = new StringBuilder();
        for (L1ClanMember l1ClanMember : clanMemberList) {
            result.append(l1ClanMember.name).append(" ");
        }
        return result.toString();
    }

    public int getBotStyle() {
        return botStyle;
    }

    public void setBotStyle(int botStyle) {
        this.botStyle = botStyle;
    }

    public int getBotLevel() {
        return botLevel;
    }

    public void setBotLevel(int botLevel) {
        this.botLevel = botLevel;
    }

    public void addExp(double addExp) {
        this.exp += addExp;
    }

    public void clanBuffAll() {
    }

    public int getClanLevel() {
        if (exp > 10000000) {
            return 5;
        } else if (exp > 7000000) {
            return 4;
        } else if (exp > 4000000) {
            return 3;
        } else if (exp > 2000000) {
            return 2;
        } else if (exp > 1000000) {
            return 1;
        } else {
            return 0;
        }
    }

    public List<String> getGazeList() {
        return gazeList;
    }

    public void removeGazelist(String clanName) {
        gazeList.remove(clanName);
    }

    public int getGazeSize() {
        return gazeList.size();
    }

    public void addGazelist(String clanName) {
        gazeList.add(clanName);
    }

}