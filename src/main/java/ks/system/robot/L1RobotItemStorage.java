package ks.system.robot;

import ks.model.instance.L1ItemInstance;
import ks.util.common.SqlUtils;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class L1RobotItemStorage {
    private static final L1RobotItemStorage instance = new L1RobotItemStorage();

    public static L1RobotItemStorage getInstance() {
        return instance;
    }

    public List<L1ItemInstance> loadItems(int objId) {
        List<L1ItemInstance> items = new ArrayList<>();

        List<L1ItemInstance> list = SqlUtils.query("SELECT * FROM robot_items WHERE char_id = ?", new L1RobotItemMapper(), objId);

        if (list != null) {
            for (L1ItemInstance item : list) {
                if (item == null)
                    continue;

                items.add(item);
            }
        }

        return items;
    }

    public void storeItem(int objId, L1ItemInstance item) {
        SqlUtils.update("INSERT INTO robot_items SET id = ?, item_id = ?, char_id = ?, item_name = ?, count = ?, is_equipped = 0, enchantlvl = ?, is_id = ?, durability = ?, charge_count = ?, remaining_time = ?, last_used = ?, bless = ?, attr_enchantlvl = ?, end_time = ?, second_id=?, round_id=?, ticket_id=?,regist_level=?,clock = ?,protection=?, PandoraT=?, package = ?",
                item.getId(),
                item.getItemId(),
                objId,
                item.getName(),
                item.getCount(),
                item.getEnchantLevel(),
                item.isIdentified() ? 1 : 0,
                item.getDurability(),
                item.getChargeCount(),
                item.getRemainingTime(),
                item.getLastUsed(),
                item.getBless(),
                item.getAttrEnchantLevel(),
                item.getEndTime(),
                item.getSecondId(),
                item.getRoundId(),
                item.getTicketId(),
                0,
                item.getClock(),
                item.getProtection(),
                0,
                item.isPackage() ? 1 : 0
        );

        item.getLastStatus().updateAll();
    }


    public void deleteItem(L1ItemInstance item) {
        SqlUtils.update("DELETE FROM robot_items WHERE id = ?", item.getId());
    }


    public void updateItemId(L1ItemInstance item) {
        executeUpdate(item.getId(), "UPDATE robot_items SET item_id = ? WHERE id = ?", item.getItemId());
        item.getLastStatus().updateItemId();
    }


    public void updateClock(L1ItemInstance item) {
        executeUpdate(item.getId(), "UPDATE robot_items SET clock = ? WHERE id = ?", item.getClock());
        item.getLastStatus().updateClock();
    }


    public void updateEndTime(L1ItemInstance item) {
        executeUpdate(item.getId(), "UPDATE robot_items SET end_time = ? WHERE id = ?", item.getEndTime());
        item.getLastStatus().updateEndTime();
    }


    public void updateItemProtection(L1ItemInstance item) {
        executeUpdate(item.getId(), "UPDATE robot_items SET protection = ? WHERE id = ?", item.getProtection());
        item.getLastStatus().updateProtection();
    }


    public void updateItemCount(L1ItemInstance item) {
        executeUpdate(item.getId(), item.getCount(), !item.isPackage() ? 0 : 1);
        item.getLastStatus().updateCount();
    }


    public void updateItemDurability(L1ItemInstance item) {
        executeUpdate(item.getId(), "UPDATE robot_items SET durability = ? WHERE id = ?", item.getDurability());
        item.getLastStatus().updateDuraility();
    }


    public void updateItemChargeCount(L1ItemInstance item) {
        executeUpdate(item.getId(), "UPDATE robot_items SET charge_count = ? WHERE id = ?", item.getChargeCount());
        item.getLastStatus().updateChargeCount();
    }


    public void updateItemRemainingTime(L1ItemInstance item) {
        executeUpdate(item.getId(), "UPDATE robot_items SET remaining_time = ? WHERE id = ?",
                item.getRemainingTime());
        item.getLastStatus().updateRemainingTime();
    }


    public void updateItemEnchantLevel(L1ItemInstance item) {
        executeUpdate(item.getId(), "UPDATE robot_items SET enchantlvl = ? WHERE id = ?", item.getEnchantLevel());
        item.getLastStatus().updateEnchantLevel();
    }


    public void updateItemEquipped(L1ItemInstance item) {
        executeUpdate(item.getId(), "UPDATE robot_items SET is_equipped = ? WHERE id = ?", (item.isEquipped() ? 1 : 0));
        item.getLastStatus().updateEquipped();
    }


    public void updateItemIdentified(L1ItemInstance item) {
        executeUpdate(item.getId(), "UPDATE robot_items SET is_id = ? WHERE id = ?", (item.isIdentified() ? 1 : 0));
        item.getLastStatus().updateIdentified();
    }


    public void updateItemDelayEffect(L1ItemInstance item) {
        executeUpdate(item.getId(), "UPDATE robot_items SET last_used = ? WHERE id = ?", item.getLastUsed());
        item.getLastStatus().updateLastUsed();
    }


    public void updateItemBless(L1ItemInstance item) {
        executeUpdate(item.getId(), "UPDATE robot_items SET bless = ? WHERE id = ?", item.getBless());
        item.getLastStatus().updateBless();
    }


    public void updateItemAttrEnchantLevel(L1ItemInstance item) {
        executeUpdate(item.getId(), "UPDATE robot_items SET attr_enchantlvl = ? WHERE id = ?", item.getAttrEnchantLevel());
        item.getLastStatus().updateAttrEnchantLevel();
    }


    private void executeUpdate(int objId, String sql, int updateNum) {
        SqlUtils.update(sql, updateNum, objId);
    }

    private void executeUpdate(int objId, int updateNum, int updatePackage) {
        SqlUtils.update("UPDATE robot_items SET count = ?, package = ? WHERE id = ?", updateNum, updatePackage, objId);
    }

    private void executeUpdate(int objId, String sql, Timestamp ts) {
        SqlUtils.update(sql, ts, objId);
    }
}
