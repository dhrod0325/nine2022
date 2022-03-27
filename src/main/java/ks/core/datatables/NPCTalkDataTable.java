package ks.core.datatables;

import ks.model.L1NpcTalkData;
import ks.util.common.SqlUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NPCTalkDataTable {
    private static final NPCTalkDataTable instance = new NPCTalkDataTable();

    private final Map<Integer, L1NpcTalkData> datatable = new HashMap<>();

    public static NPCTalkDataTable getInstance() {
        return instance;
    }

    public void load() {
        datatable.clear();

        List<L1NpcTalkData> list = selectList();

        for (L1NpcTalkData data : list) {
            datatable.put(data.getNpcID(), data);
        }
    }

    public List<L1NpcTalkData> selectList() {
        return SqlUtils.query("SELECT * FROM npcaction", (rs, i) -> {
            L1NpcTalkData data = new L1NpcTalkData();
            data.setNpcID(rs.getInt(1));
            data.setNormalAction(rs.getString(2));
            data.setCaoticAction(rs.getString(3));
            data.setTeleportURL(rs.getString(4));
            data.setTeleportURLA(rs.getString(5));

            return data;
        });
    }

    public L1NpcTalkData getTemplate(int i) {
        return datatable.get(i);
    }

}
