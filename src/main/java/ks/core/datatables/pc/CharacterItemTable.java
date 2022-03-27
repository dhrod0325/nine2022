package ks.core.datatables.pc;

import ks.app.LineageAppContext;
import ks.core.datatables.item.ItemTable;
import ks.model.L1Item;
import ks.model.instance.L1ItemInstance;
import ks.util.common.SqlUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

@Component
public class CharacterItemTable {
    private static final Logger logger = LogManager.getLogger(CharacterItemTable.class.getName());

    public static CharacterItemTable getInstance() {
        return LineageAppContext.getBean(CharacterItemTable.class);
    }

    public List<L1ItemInstance> loadItems(int objId) {
        return SqlUtils.query("SELECT * FROM character_items WHERE char_id = ?", (rs, i) -> {
            int itemId = rs.getInt("item_id");

            L1Item itemTemplate = ItemTable.getInstance().getTemplate(itemId);

            if (itemTemplate == null) {
                logger.warn(String.format("item id:%d not found", itemId));
                return null;
            }

            L1ItemInstance item = ItemTable.getInstance().functionItem(itemTemplate);
            item.setId(rs.getInt("id"));
            item.setItem(itemTemplate);
            item.setCount(rs.getInt("count"));
            item.setEquipped(rs.getInt("Is_equipped") != 0);
            item.setEnchantLevel(rs.getInt("enchantlvl"));
            item.setIdentified(rs.getInt("is_id") != 0);
            item.setDurability(rs.getInt("durability"));
            item.setChargeCount(rs.getInt("charge_count"));
            item.setRemainingTime(rs.getInt("remaining_time"));
            item.setLastUsed(rs.getTimestamp("last_used"));
            item.setBless(rs.getInt("bless"));
            item.setAttrEnchantLevel(rs.getInt("attr_enchantlvl"));
            item.setEndTime(rs.getTimestamp("end_time"));
            item.setSecondId(rs.getInt("second_id"));
            item.setRoundId(rs.getInt("round_id"));
            item.setTicketId(rs.getInt("ticket_id"));
            item.setClock(rs.getInt("clock"));
            item.setProtection(rs.getInt("protection"));
            item.setPackage(rs.getInt("package") != 0);
            item.setOptionGrade(rs.getInt("option_grade"));
            item.setNextReq(rs.getInt("next_req"));
            item.getLastStatus().updateAll();

            return item;
        }, objId);
    }


    public void storeItem(int objId, L1ItemInstance item) throws Exception {
        String sql = "INSERT INTO character_items SET id = ?, item_id = ?, char_id = ?, item_name = ?, count = ?, is_equipped = 0, enchantlvl = ?, is_id = ?, durability = ?, charge_count = ?, remaining_time = ?, last_used = ?, bless = ?, attr_enchantlvl = ?, end_time = ?, second_id=?, round_id=?, ticket_id=?,regist_level=?,clock = ?,protection=?, PandoraT=?, package = ?, option_grade = ?,next_req=?";

        SqlUtils.update(sql,
                item.getId(),
                item.getItemId(),
                objId,
                item.getItem().getName(),
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
                item.isPackage() ? 1 : 0,
                item.getOptionGrade(),
                item.getNextReq()
        );

        item.getLastStatus().updateAll();
    }


    public void deleteItem(L1ItemInstance item) throws Exception {
        SqlUtils.update("DELETE FROM character_items WHERE id = ?", item.getId());
    }


    public void updateItemId(L1ItemInstance item) throws Exception {
        executeUpdate(item.getId(), "UPDATE character_items SET item_id = ? WHERE id = ?", item.getItemId());
        item.getLastStatus().updateItemId();
    }


    public void updateClock(L1ItemInstance item) throws Exception {
        executeUpdate(item.getId(), "UPDATE character_items SET clock = ? WHERE id = ?", item.getClock());
        item.getLastStatus().updateClock();
    }


    public void updateEndTime(L1ItemInstance item) throws Exception {
        executeUpdate(item.getId(), "UPDATE character_items SET end_time = ? WHERE id = ?", item.getEndTime());
        item.getLastStatus().updateEndTime();
    }


    public void updateItemProtection(L1ItemInstance item) throws Exception {
        executeUpdate(item.getId(), "UPDATE character_items SET protection = ? WHERE id = ?", item.getProtection());
        item.getLastStatus().updateProtection();
    }


    public void updateItemOptionGrade(L1ItemInstance item) throws Exception {
        executeUpdate(item.getId(), "UPDATE character_items SET option_grade = ? WHERE id = ?", item.getOptionGrade());
        item.getLastStatus().updateOptionGrade();
    }


    public void updateItemCount(L1ItemInstance item) throws Exception {
        executeUpdate(item.getId(), "UPDATE character_items SET count = ?, package = ? WHERE id = ?", item.getCount(), !item.isPackage() ? 0 : 1);
        item.getLastStatus().updateCount();
    }


    public void updateItemDurability(L1ItemInstance item) throws Exception {
        executeUpdate(item.getId(), "UPDATE character_items SET durability = ? WHERE id = ?", item.getDurability());
        item.getLastStatus().updateDuraility();
    }


    public void updateItemChargeCount(L1ItemInstance item) throws Exception {
        executeUpdate(item.getId(), "UPDATE character_items SET charge_count = ? WHERE id = ?", item.getChargeCount());
        item.getLastStatus().updateChargeCount();
    }


    public void updateItemRemainingTime(L1ItemInstance item) throws Exception {
        executeUpdate(item.getId(),
                "UPDATE character_items SET remaining_time = ? WHERE id = ?",
                item.getRemainingTime());
        item.getLastStatus().updateRemainingTime();
    }


    public void updateItemEnchantLevel(L1ItemInstance item) throws Exception {
        executeUpdate(item.getId(),
                "UPDATE character_items SET enchantlvl = ? WHERE id = ?", item
                        .getEnchantLevel());
        item.getLastStatus().updateEnchantLevel();
    }


    public void updateItemEquipped(L1ItemInstance item) throws Exception {
        executeUpdate(item.getId(), "UPDATE character_items SET is_equipped = ? WHERE id = ?", (item.isEquipped() ? 1 : 0));
        item.getLastStatus().updateEquipped();
    }


    public void updateItemIdentified(L1ItemInstance item) throws Exception {
        executeUpdate(item.getId(),
                "UPDATE character_items SET is_id = ? WHERE id = ?", (item
                        .isIdentified() ? 1 : 0));
        item.getLastStatus().updateIdentified();
    }


    public void updateItemDelayEffect(L1ItemInstance item) throws Exception {
        executeUpdate(item.getId(),
                "UPDATE character_items SET last_used = ? WHERE id = ?", item
                        .getLastUsed());
        item.getLastStatus().updateLastUsed();
    }


    public void updateItemBless(L1ItemInstance item) throws Exception {
        executeUpdate(item.getId(),
                "UPDATE character_items SET bless = ? WHERE id = ?", item
                        .getBless());

        executeUpdate(item.getId(), "UPDATE character_items SET next_req = ? WHERE id = ?", item.getNextReq());

        item.getLastStatus().updateBless();
    }


    public void updateItemNextReq(L1ItemInstance item) throws Exception {
        executeUpdate(item.getId(), "UPDATE character_items SET next_req = ? WHERE id = ?", item.getNextReq());

        item.getLastStatus().updateNextReq();
    }


    public void updateItemAttrEnchantLevel(L1ItemInstance item) throws Exception {
        executeUpdate(item.getId(), "UPDATE character_items SET attr_enchantlvl = ? WHERE id = ?",
                item.getAttrEnchantLevel());
        item.getLastStatus().updateAttrEnchantLevel();
    }

    private void executeUpdate(int objId, String sql, int updateNum) throws SQLException {
        SqlUtils.update(sql, updateNum, objId);
    }

    private void executeUpdate(int objId, String sql, int updateNum, int updatePackage) throws SQLException {
        SqlUtils.update(sql, updateNum, updatePackage, objId);
    }

    private void executeUpdate(int objId, String sql, Timestamp ts) throws SQLException {
        SqlUtils.update(sql, ts, objId);
    }
}
