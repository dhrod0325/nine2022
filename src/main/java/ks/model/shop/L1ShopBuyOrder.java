package ks.model.shop;

import ks.model.L1ShopItem;

public class L1ShopBuyOrder {
    private final L1ShopItem item;

    private final int count;

    public L1ShopBuyOrder(L1ShopItem item, int count) {
        this.item = item;
        this.count = count;
    }

    public L1ShopItem getItem() {
        return item;
    }

    public int getCount() {
        return count;
    }
}
