package ks.core.datatables;

import ks.model.L1Buddy;
import ks.util.common.SqlUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BuddyTable {
    private static final BuddyTable instance = new BuddyTable();
    private final Map<Integer, L1Buddy> buddys = new HashMap<>();

    public static BuddyTable getInstance() {
        return instance;
    }

    public void load() {
        buddys.clear();

        List<Integer> idList = selectCharIdList();

        for (Integer id : idList) {
            buddys.put(id, selectBuddy(id));
        }
    }

    public L1Buddy selectBuddy(int charId) {
        L1Buddy buddy = new L1Buddy(charId);

        SqlUtils.query("SELECT buddy_id, buddy_name FROM character_buddys WHERE char_id = ?", (rs, i2) -> {
            buddy.add(rs.getInt("buddy_id"), rs.getString("buddy_name"));
            return null;
        }, charId);

        return buddy;
    }

    public List<Integer> selectCharIdList() {
        return SqlUtils.queryForList("SELECT distinct(char_id) as char_id FROM character_buddys", Integer.class);
    }

    public L1Buddy getBuddyTable(int charId) {
        L1Buddy buddy = buddys.getOrDefault(charId, new L1Buddy(charId));
        buddys.put(charId, buddy);
        return buddy;
    }

    public void addBuddy(int charId, int objId, String name) {
        SqlUtils.update("INSERT INTO character_buddys SET char_id=?, buddy_id=?, buddy_name=?", charId, objId, name);
    }

    public void removeBuddy(int charId, String buddyName) {
        SqlUtils.update("DELETE FROM character_buddys WHERE char_id=? AND buddy_name=?", charId, buddyName);
    }
}
