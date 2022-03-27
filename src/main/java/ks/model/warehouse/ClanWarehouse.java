package ks.model.warehouse;

import ks.app.config.prop.CodeConfig;
import ks.model.instance.L1ItemInstance;
import ks.util.common.SqlUtils;

import java.util.List;

public class ClanWarehouse extends Warehouse {
    private boolean key = false;

    private int pcIdUsingClanWarehouse = -1;

    public ClanWarehouse(String clan) {
        super(clan);
    }

    public synchronized boolean lock(int id) {
        if (!key || pcIdUsingClanWarehouse == id) {
            key = true;
            pcIdUsingClanWarehouse = id;
            return true;
        } else {
            return false;
        }
    }

    public synchronized void unlock(int id) {
        if (id == pcIdUsingClanWarehouse) {
            key = false;
        }
    }

    @Override
    protected int getMax() {
        return CodeConfig.MAX_CLAN_WAREHOUSE_ITEM;
    }

    @Override
    public synchronized void loadItems() {
        items.clear();
        items.addAll(selectList());
    }

    public List<L1ItemInstance> selectList() {
        return SqlUtils.query("SELECT * FROM clan_warehouse WHERE clan_name = ?", new WareHouseMapper(), getName());
    }

    @Override
    public synchronized void insertItem(L1ItemInstance item) {
        SqlUtils.update("INSERT INTO clan_warehouse SET id = ?, clan_name = ?, item_id = ?, item_name = ?, count = ?, is_equipped=0, enchantlvl = ?, is_id= ?, durability = ?, charge_count = ?, remaining_time = ?, last_used = ?, attr_enchantlvl = ?, package = ?, bless=?",
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
                item.isPackage() ? 1 : 0,
                item.getBless()
        );
    }

    @Override
    public synchronized void updateItem(L1ItemInstance item) {
        SqlUtils.update("UPDATE clan_warehouse SET count = ? WHERE id = ?", item.getCount(), item.getId());
    }

    @Override
    public synchronized void deleteItem(L1ItemInstance item) {
        SqlUtils.update("DELETE FROM clan_warehouse WHERE id = ?", item.getId());

        items.remove(item);
    }

    public synchronized void deleteAllItems() {
        SqlUtils.update("DELETE FROM clan_warehouse WHERE clan_name = ?", getName());
    }
}
