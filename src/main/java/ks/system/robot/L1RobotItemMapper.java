package ks.system.robot;

import ks.core.datatables.item.ItemTable;
import ks.model.L1Item;
import ks.model.instance.L1ItemInstance;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class L1RobotItemMapper implements RowMapper<L1ItemInstance> {
    @Override
    public L1ItemInstance mapRow(ResultSet rs, int i) throws SQLException {
        int itemId = rs.getInt("item_id");

        L1Item itemTemplate = ItemTable.getInstance().getTemplate(itemId);

        if (itemTemplate == null) {
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
        item.getLastStatus().updateAll();

        return item;
    }
}
