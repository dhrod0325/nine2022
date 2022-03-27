package ks.model;


import ks.model.pc.L1PcInstance;
import ks.util.common.SqlUtils;
import org.springframework.jdbc.core.SingleColumnRowMapper;

import java.util.ArrayList;
import java.util.List;

public class L1ClanMatching {
    private static final L1ClanMatching instance = new L1ClanMatching();
    private final List<ClanMatchingList> list = new ArrayList<>();

    public static L1ClanMatching getInstance() {
        return instance;
    }

    public void writeClanMatching(String clanname, String text, int htype) {
        SqlUtils.update("INSERT INTO clan_matching_list SET clanname = ?, text = ?, type = ?", clanname, text, htype);
        ClanMatchingList CML = new ClanMatchingList(clanname, text, htype);
        addMatching(CML);
    }

    public void updateClanMatching(String clanname, String text, int htype) {
        SqlUtils.update("UPDATE clan_matching_list SET text = ?, type = ? WHERE clanname = ?", text, htype, clanname);

        ClanMatchingList CML = getClanMatchingList(clanname);
        CML.text = text;
        CML.type = htype;
    }

    public void deleteClanMatching(L1PcInstance pc) {
        SqlUtils.update("DELETE FROM clan_matching_list WHERE clanname=?", pc.getClanName());

        removeMatching(pc.getClanName());
        pc.getCMAList().clear();

        for (L1PcInstance clanuser : pc.getClan().getOnlineClanMember()) {
            switch (clanuser.getClanRank()) {
                case 3:
                case 9:
                case 10:
                    clanuser.getCMAList().clear();
                    break;
            }
        }
        for (L1PcInstance player : L1World.getInstance().getAllPlayers()) {
            if (player.getClanId() == 0 &&
                    player.getCMAList().contains(pc.getClanName())) {
                player.removeCMAList(pc.getClanName());
            }
        }
    }

    public void load() {
        SqlUtils.query("SELECT * FROM clan_matching_list", (rs, i) -> {
            String clanname = rs.getString("clanname");
            String text = rs.getString("text");
            int type = rs.getInt("type");

            ClanMatchingList CML = new ClanMatchingList(clanname, text, type);
            addMatching(CML);

            return CML;
        });
    }

    // 유저 전용
    public void writeClanMatchingApcList_User(L1PcInstance pc, L1Clan clan) {
        SqlUtils.update("INSERT INTO clan_matching_apclist SET pc_name=?, pc_objid=?, clan_name=?", pc.getName(), pc.getId(), clan.getClanName());

        pc.addCMAList(clan.getClanName());

        for (L1PcInstance clanuser : clan.getOnlineClanMember()) {
            switch (clanuser.getClanRank()) {
                case 3:
                case 4:
                case 6:
                    clanuser.addCMAList(pc.getName());
                    break;
            }
        }
    }

    public void loadClanMatchingApcList_User(L1PcInstance pc) {
        List<String> nameList = SqlUtils.query("SELECT clan_name FROM clan_matching_apclist WHERE pc_name = ?", new SingleColumnRowMapper<>(String.class), pc.getName());

        if (nameList == null) {
            return;
        }

        for (String name : nameList) {
            pc.addCMAList(name);
        }
    }

    public void loadClanMatchingApcList_Crown(L1PcInstance pc) {
        List<String> nameList = SqlUtils.query("SELECT pc_name FROM clan_matching_apclist WHERE clan_name = ?", new SingleColumnRowMapper<>(String.class), pc.getClanName());

        if (nameList == null) {
            return;
        }

        for (String name : nameList) {
            pc.addCMAList(name);
        }
    }

    public void deleteClanMatchingApcList(L1PcInstance pc) {
        SqlUtils.update("DELETE FROM clan_matching_apclist WHERE pc_name=?", pc.getName());

        pc.getCMAList().clear();
        for (L1PcInstance clanuser : pc.getClan().getOnlineClanMember()) {
            switch (clanuser.getClanRank()) {
                case 3:
                case 4:
                case 6:
                    clanuser.removeCMAList(pc.getName());
                    break;
            }
        }
    }

    // 거절 눌렀을때
    public void deleteClanMatchingApcList(L1PcInstance pc, int objid, L1Clan clan) {
        String pcname;

        if (pc == null) {
            pcname = SqlUtils.select("SELECT pc_name FROM clan_matching_apclist WHERE pc_objid=? AND clan_name=?", String.class, objid, clan.getClanName());
        } else {
            pcname = pc.getName();
            pc.removeCMAList(clan.getClanName());
        }

        SqlUtils.update("DELETE FROM clan_matching_apclist WHERE pc_objid=? AND clan_name=?", objid, clan.getClanName());

        for (L1PcInstance clanuser : clan.getOnlineClanMember()) {
            switch (clanuser.getClanRank()) {
                case 3:
                case 9:
                case 10:
                    clanuser.removeCMAList(pcname);
                    break;
            }
        }
    }

    // 삭제 눌렀을때
    public void deleteClanMatchingApcList(L1PcInstance pc, L1Clan clan) {
        SqlUtils.update("DELETE FROM clan_matching_apclist WHERE pc_name=? AND clan_name=?", pc.getName(), clan.getClanName());

        pc.removeCMAList(clan.getClanName());
        for (L1PcInstance clanuser : clan.getOnlineClanMember()) {
            switch (clanuser.getClanRank()) {
                case 3:
                case 9:
                case 10:
                    clanuser.removeCMAList(pc.getName());
                    break;
            }
        }
    }

    public void addMatching(ClanMatchingList list) {
        if (this.list.contains(list)) {
            return;
        }
        this.list.add(list);
    }

    public void removeMatching(String clanname) {
        if (!isClanMatchingList(clanname)) {
            return;
        }
        list.remove(getClanMatchingList(clanname));
    }

    public List<ClanMatchingList> getMatchingList() {
        return list;
    }

    public boolean isClanMatchingList(String clanname) {
        for (ClanMatchingList clanMatchingList : list) {
            if (clanMatchingList.clanName.equalsIgnoreCase(clanname))
                return true;
        }
        return false;
    }

    public ClanMatchingList getClanMatchingList(String clanname) {
        for (ClanMatchingList clanMatchingList : list) {
            if (clanMatchingList.clanName.equalsIgnoreCase(clanname)) {
                return clanMatchingList;
            }
        }

        return null;
    }

    public static class ClanMatchingList {
        public String clanName;
        public String text;
        public int type;

        public ClanMatchingList(String clanname, String text, int type) {
            this.clanName = clanname;
            this.text = text;
            this.type = type;
        }
    }
}