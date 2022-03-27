package ks.core.datatables;

import ks.app.config.prop.CodeConfig;
import ks.util.common.SqlUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ResolventTable {
    private static final ResolventTable instance = new ResolventTable();

    private final Map<Integer, ResolventModel> resolvent = new HashMap<>();

    public static ResolventTable getInstance() {
        return instance;
    }

    public void load() {
        resolvent.clear();

        List<ResolventModel> list = selectList();

        for (ResolventModel o : list) {
            resolvent.put(o.getItem_id(), o);
        }
    }

    public Map<Integer, ResolventModel> getResolvent() {
        return resolvent;
    }

    public List<ResolventModel> selectList() {
        return SqlUtils.query("SELECT * FROM resolvent", (rs, i) -> {
            int itemId = rs.getInt("item_id");
            int crystalCount = rs.getInt("crystal_count") * CodeConfig.RATE_CRISTAL;
            String note = rs.getString("note");

            return new ResolventModel(itemId, crystalCount, note);
        });
    }

    public int getCrystalCount(int itemId) {
        int crystalCount = 0;

        if (resolvent.containsKey(itemId)) {
            crystalCount = resolvent.get(itemId).getCrystal_count();
        }

        return crystalCount;
    }

    public static class ResolventModel {
        private int item_id;
        private int crystal_count;
        private String note;

        public ResolventModel(int item_id, int crystal_count, String note) {
            this.item_id = item_id;
            this.note = note;
            this.crystal_count = crystal_count;
        }

        public int getItem_id() {
            return item_id;
        }

        public void setItem_id(int item_id) {
            this.item_id = item_id;
        }

        public String getNote() {
            return note;
        }

        public void setNote(String note) {
            this.note = note;
        }

        public int getCrystal_count() {
            return crystal_count;
        }

        public void setCrystal_count(int crystal_count) {
            this.crystal_count = crystal_count;
        }
    }
}
