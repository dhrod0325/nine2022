package ks.model.warehouse;

import ks.app.config.prop.CodeConfig;
import ks.model.L1World;
import ks.model.instance.L1ItemInstance;
import ks.util.common.SqlUtils;

import java.util.List;

public class ElfWarehouse extends Warehouse {
    public ElfWarehouse(String name) {
        super(name);
    }

    @Override
    protected int getMax() {
        return CodeConfig.MAX_PERSONAL_WAREHOUSE_ITEM;
    }

    @Override
    public void loadItems() {
        items.clear();
        items.addAll(selectList(getName()));
        L1World.getInstance().storeObject(items);
    }

    public List<L1ItemInstance> selectList(String name) {
        return SqlUtils.query("SELECT * FROM character_elf_warehouse WHERE account_name = ?", new WareHouseMapper(), name);
    }

    @Override
    public void insertItem(L1ItemInstance item) {
        String sql = "INSERT INTO character_elf_warehouse SET id = ?, account_name = ?, item_id = ?, item_name = ?, count = ?, is_equipped=0, enchantlvl = ?, is_id = ?, durability = ?, charge_count = ?, remaining_time = ?, last_used = ?, attr_enchantlvl = ?, bless = ?, second_id=?, round_id=?, ticket_id=?, package=?";

        SqlUtils.update(sql,
                item.getId(),
                getName(),
                item.getItemId(),
                item.getName(),
                item.getCount(),
                item.getEnchantLevel(),
                item.isIdentified() ? 1 : 0,
                item.getDurability(),
                item.getChargeCount(),
                item.getRemainingTime(),
                item.getLastUsed(),
                item.getAttrEnchantLevel(),
                item.getBless(),
                item.getSecondId(),
                item.getRoundId(),
                item.getTicketId(),
                item.isPackage() ? 1 : 0
        );
    }

    @Override
    public void updateItem(L1ItemInstance item) {
        SqlUtils.update("UPDATE character_elf_warehouse SET count = ? WHERE id = ?", item.getCount(), item.getId());
    }

    @Override
    public void deleteItem(L1ItemInstance item) {
        SqlUtils.update("DELETE FROM character_elf_warehouse WHERE id = ?", item.getId());
        items.remove(item);
    }
}
