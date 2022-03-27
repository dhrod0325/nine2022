package ks.model.warehouse;

import ks.core.datatables.item.ItemTable;
import ks.model.L1Item;
import ks.model.instance.L1ItemInstance;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class WareHouseMapper implements RowMapper<L1ItemInstance> {
    @Override
    public L1ItemInstance mapRow(ResultSet rs, int i) throws SQLException {
        L1Item itemTemplate = ItemTable.getInstance().getTemplate(rs.getInt("item_id"));
        L1ItemInstance item = ItemTable.getInstance().functionItem(itemTemplate);

        int objectId = rs.getInt("id");
        item.setId(objectId);

        item.setItem(itemTemplate);
        item.setCount(rs.getInt("count"));
        item.setEquipped(false);
        item.setEnchantLevel(rs.getInt("enchantlvl"));
        item.setIdentified(rs.getInt("is_id") != 0);
        item.setDurability(rs.getInt("durability"));
        item.setChargeCount(rs.getInt("charge_count"));
        item.setRemainingTime(rs.getInt("remaining_time"));
        item.setLastUsed(rs.getTimestamp("last_used"));
        item.setBless(rs.getInt("bless"));
        item.setAttrEnchantLevel(rs.getInt("attr_enchantlvl"));

        return item;
    }
}
