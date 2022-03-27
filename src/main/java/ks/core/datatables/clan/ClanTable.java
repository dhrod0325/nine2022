package ks.core.datatables.clan;

import ks.constants.L1ClanRankId;
import ks.core.ObjectIdFactory;
import ks.model.L1Clan;
import ks.model.L1World;
import ks.model.pc.L1PcInstance;
import ks.model.warehouse.ClanWarehouse;
import ks.model.warehouse.WarehouseManager;
import ks.util.common.SqlUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClanTable {
    private static final Logger logger = LogManager.getLogger(ClanTable.class.getName());

    private static final ClanTable instance = new ClanTable();

    private final HashMap<Integer, L1Clan> clans = new HashMap<>();

    private final HashMap<Integer, L1Clan> clanCastle = new HashMap<>();

    public static ClanTable getInstance() {
        return instance;
    }

    public HashMap<Integer, L1Clan> getClans() {
        return clans;
    }

    public List<L1Clan> selectClanList() {
        return SqlUtils.query("SELECT * FROM clan_data ORDER BY clan_id", (rs, i) -> {
            L1Clan clan = new L1Clan();

            int clanId = rs.getInt("clan_id");
            int castleId = rs.getInt("hascastle");

            clan.setClanId(clanId);
            clan.setClanName(rs.getString("clan_name"));
            clan.setLeaderId(rs.getInt("leader_id"));
            clan.setLeaderName(rs.getString("leader_name"));
            clan.setCastleId(castleId);
            clan.setHouseId(rs.getInt("hashouse"));
            clan.setAlliance(rs.getInt("alliance"));
            clan.setClanBirthDay(rs.getTimestamp("clan_birthday"));
            clan.setBotStyle(rs.getInt("bot_style"));
            clan.setBotLevel(rs.getInt("bot_level"));
            clan.setOnlineMaxUser(rs.getInt("max_online_user"));
            clan.setAnnouncement(rs.getString("announcement"));
            clan.setEmblemId(rs.getInt("emblem_id"));
            clan.setEmblemStatus(rs.getInt("emblem_status"));
            clan.setExp(rs.getDouble("exp"));

            return clan;
        });
    }

    public void load() {
        clans.clear();
        clanCastle.clear();

        List<L1Clan> clanList = selectClanList();

        for (L1Clan clan : clanList) {
            ClanWarehouse clanWarehouse = WarehouseManager.getInstance().getClanWarehouse(clan.getClanName());
            clanWarehouse.loadItems();

            List<Map<String, Object>> memberList = selectMemberList(clan.getClanId());

            for (Map<String, Object> member : memberList) {
                clan.addClanMember(member.get("char_name") + "", Integer.parseInt(member.get("ClanRank").toString()));
            }

            clans.put(clan.getClanId(), clan);

            if (clan.getCastleId() > 0) {
                clanCastle.put(clan.getCastleId(), clan);
            }

            L1World.getInstance().storeClan(clan);
        }
    }

    public List<Integer> selectAllienceList(int allianceId) {
        return SqlUtils.queryForList("SELECT alliance FROM clan_data where alliance=?", Integer.class, allianceId);
    }

    public List<Integer> selectClanIdListByAllience(int allianceId) {
        return SqlUtils.queryForList("SELECT clan_id FROM clan_data where alliance=?", Integer.class, allianceId);
    }

    public List<Map<String, Object>> selectMemberList(int clanId) {
        return SqlUtils.queryForList("SELECT char_name, ClanRank FROM characters WHERE ClanID = ?", clanId);
    }

    public L1Clan createClan(L1PcInstance pc, String clanName) {
        for (L1Clan oldClans : L1World.getInstance().getAllClans()) {
            if (oldClans.getClanName().equalsIgnoreCase(clanName)) {
                return null;
            }
        }

        L1Clan clan = new L1Clan();
        clan.setClanId(ObjectIdFactory.getInstance().nextId());
        clan.setClanName(clanName);
        clan.setLeaderId(pc.getId());
        clan.setLeaderName(pc.getName());
        clan.setCastleId(0);
        clan.setHouseId(0);
        clan.setAlliance(0);

        Timestamp time = new Timestamp(System.currentTimeMillis()); //추가
        clan.setClanBirthDay(time);  //추가

        clan.setAnnouncement("");
        clan.setEmblemId(0);
        clan.setEmblemStatus(0);

        SqlUtils.update("INSERT INTO clan_data SET clan_id=?, clan_name=?, leader_id=?, leader_name=?, hascastle=?, hashouse=?, alliance=?, clan_birthday=?,max_online_user=?, announcement=?, emblem_id=?, emblem_status=?,exp=?",
                clan.getClanId(),
                clan.getClanName(),
                clan.getLeaderId(),
                clan.getLeaderName(),
                clan.getCastleId(),
                clan.getHouseId(),
                clan.getAlliance(),
                clan.getClanBirthDay(),
                clan.getOnlineMaxUser(),
                "",
                0,
                0,
                clan.getExp()
        );

        L1World.getInstance().storeClan(clan);
        clans.put(clan.getClanId(), clan);

        pc.setClanId(clan.getClanId());
        pc.setClanName(clan.getClanName());
        pc.setClanRank(L1ClanRankId.CLAN_RANK_PRINCE);
        clan.addClanMember(pc.getName(), pc.getClanRank());

        try {
            pc.save();
        } catch (Exception e) {
            logger.error(e);
        }

        return clan;
    }

    public void updateClan(L1Clan clan) {
        SqlUtils.update("UPDATE clan_data SET clan_id=?, leader_id=?, leader_name=?, hascastle=?, hashouse=?, alliance=?, clan_birthday=?, bot_style=?, bot_level=?, max_online_user=?, announcement=?, emblem_id=?, emblem_status=?,exp=? WHERE clan_name=?",
                clan.getClanId(),
                clan.getLeaderId(),
                clan.getLeaderName(),
                clan.getCastleId(),
                clan.getHouseId(),
                clan.getAlliance(),
                clan.getClanBirthDay(),
                clan.getBotStyle(),
                clan.getBotLevel(),
                clan.getOnlineMaxUser(),
                clan.getAnnouncement(),
                clan.getEmblemId(),
                clan.getEmblemStatus(),
                clan.getExp(),
                clan.getClanName()
        );
    }

    public void deleteClan(String clanName) {
        L1Clan clan = L1World.getInstance().getClan(clanName);

        if (clan == null) {
            return;
        }

        SqlUtils.update("DELETE FROM clan_data WHERE clan_name=?", clanName);

        ClanWarehouse clanWarehouse = WarehouseManager.getInstance().getClanWarehouse(clan.getClanName());
        clanWarehouse.clearItems();
        clanWarehouse.deleteAllItems();

        L1World.getInstance().removeClan(clan);
        clans.remove(clan.getClanId());
    }

    public L1Clan getTemplate(int clan_id) {
        return clans.get(clan_id);
    }


    public L1Clan find(String clan_name) {
        for (L1Clan clan : clans.values()) {
            if (clan.getClanName().equalsIgnoreCase(clan_name))
                return clan;
        }
        return null;
    }

    public L1Clan getCastleLeaderClan(int castleId) {
        L1Clan result = clanCastle.get(castleId);

        if (result == null) {
            result = L1World.getInstance().getClan(1);
            result.setCastleId(castleId);
        }

        return result;
    }

    public Integer selectClanIdByLeaderId(int leaderId) {
        return SqlUtils.selectInteger("select clan_id from clan_data where leader_id=?", leaderId);
    }
}
