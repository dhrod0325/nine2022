package ks.model;

import ks.model.instance.L1ItemInstance;

public class L1PrivateShopBuy {
    private L1ItemInstance item;

    private int itemObjectId;
    private int buyTotalCount;
    private int buyPrice;
    private int buyCount;

    public L1PrivateShopBuy() {
    }

    public int getItemObjectId() {
        return itemObjectId;
    }

    public void setItemObjectId(int i) {
        itemObjectId = i;
    }

    public int getBuyTotalCount() {
        return buyTotalCount;
    }

    public void setBuyTotalCount(int i) {
        buyTotalCount = i;
    }

    public int getBuyPrice() {
        return buyPrice;
    }

    public void setBuyPrice(int i) {
        buyPrice = i;
    }

    public int getBuyCount() {
        return buyCount;
    }

    public void setBuyCount(int i) {
        buyCount = i;
    }

    public L1ItemInstance getItem() {
        return item;
    }

    public void setItem(L1ItemInstance item) {
        this.item = item;
    }
}
