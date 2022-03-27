package ks.core.datatables;

import ks.model.L1MobGroup;
import ks.util.common.SqlUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MobGroupTable {
    private static final MobGroupTable instance = new MobGroupTable();

    private final Map<Integer, L1MobGroup> mobGroupIndex = new HashMap<>();

    public static MobGroupTable getInstance() {
        return instance;
    }

    public void load() {
        mobGroupIndex.clear();

        List<L1MobGroup> list = selectList();

        for (L1MobGroup mobGroup : list) {
            mobGroupIndex.put(mobGroup.getId(), mobGroup);
        }
    }

    public List<L1MobGroup> selectList() {
        return SqlUtils.query("SELECT * FROM mobgroup", (rs, i) -> {
            L1MobGroup mobGroup = new L1MobGroup();
            int mobGroupId = rs.getInt("id");
            mobGroup.setId(mobGroupId);
            mobGroup.setRemoveGroupIfLeaderDie(rs.getBoolean("remove_group_if_leader_die"));
            mobGroup.setLeaderId(rs.getInt("leader_id"));
            mobGroup.setMinion1Id(rs.getInt("minion1_id"));
            mobGroup.setMinion1Count(rs.getInt("minion1_count"));
            mobGroup.setMinion2Id(rs.getInt("minion2_id"));
            mobGroup.setMinion2Count(rs.getInt("minion2_count"));
            mobGroup.setMinion3Id(rs.getInt("minion3_id"));
            mobGroup.setMinion3Count(rs.getInt("minion3_count"));
            mobGroup.setMinion4Id(rs.getInt("minion4_id"));
            mobGroup.setMinion4Count(rs.getInt("minion4_count"));
            mobGroup.setMinion5Id(rs.getInt("minion5_id"));
            mobGroup.setMinion5Count(rs.getInt("minion5_count"));
            mobGroup.setMinion6Id(rs.getInt("minion6_id"));
            mobGroup.setMinion6Count(rs.getInt("minion6_count"));
            mobGroup.setMinion7Id(rs.getInt("minion7_id"));
            mobGroup.setMinion7Count(rs.getInt("minion7_count"));

            return mobGroup;
        });
    }

    public L1MobGroup getTemplate(int mobGroupId) {
        return mobGroupIndex.get(mobGroupId);
    }
}
