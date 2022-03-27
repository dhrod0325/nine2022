package ks.system.bossTraning;

import ks.constants.L1ItemId;
import ks.model.instance.L1ItemInstance;
import ks.util.common.SqlUtils;

public class BossTrainingTable {
    private final static BossTrainingTable instance = new BossTrainingTable();

    public static BossTrainingTable getInstance() {
        return instance;
    }

    public void insert(L1ItemInstance item) {
        SqlUtils.update("INSERT INTO character_boss_key SET item_obj_id=?, key_id=?", item.getId(), item.getKeyId());
    }

    public void deleteByKeyId(int keyId) {
        SqlUtils.update("DELETE FROM character_boss_key WHERE key_id=?", keyId);
    }

    public void clear() {
        int itemId = L1ItemId.BOSS_TRANING_KEY;

        SqlUtils.update("DELETE FROM character_boss_key");
        SqlUtils.update("DELETE FROM character_items WHERE item_id = ?", itemId);
        SqlUtils.update("DELETE FROM character_elf_warehouse WHERE item_id = ?", itemId);
        SqlUtils.update("DELETE FROM clan_warehouse WHERE item_id = ?", itemId);
        SqlUtils.update("DELETE FROM character_warehouse WHERE item_id = ?", itemId);
    }
}
