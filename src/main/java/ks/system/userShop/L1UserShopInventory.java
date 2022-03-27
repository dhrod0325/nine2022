package ks.system.userShop;

import ks.model.L1Inventory;
import ks.model.instance.L1ItemInstance;

public class L1UserShopInventory extends L1Inventory {
    public L1UserShopInventory() {
        super();
    }

    @Override
    public synchronized L1ItemInstance storeTradeItem(L1ItemInstance item) {
        return super.storeTradeItem(item);
    }

    @Override
    public void insertItem(L1ItemInstance item) {
    }

    @Override
    public void updateItem(L1ItemInstance item, int colmn) {
    }

    @Override
    public void deleteItem(L1ItemInstance item) {
        super.deleteItem(item);
    }

    @Override
    public void loadItems() {
    }
}
