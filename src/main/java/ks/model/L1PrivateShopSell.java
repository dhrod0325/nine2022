package ks.model;

import ks.model.instance.L1ItemInstance;

public class L1PrivateShopSell {
    private L1ItemInstance item;
    private int itemObjectId;
    private int sellTotalCount;
    private int sellPrice;
    private int sellCount;

    public L1PrivateShopSell() {
    }

    public int getItemObjectId() {
        return itemObjectId;
    }

    public void setItemObjectId(int i) {
        itemObjectId = i;
    }

    public int getSellTotalCount() {
        return sellTotalCount;
    }

    public void setSellTotalCount(int i) {
        sellTotalCount = i;
    }

    public int getSellPrice() {
        return sellPrice;
    }

    public void setSellPrice(int i) {
        sellPrice = i;
    }

    public int getSellCount() {
        return sellCount;
    }

    public void setSellCount(int i) {
        sellCount = i;
    }

    public L1ItemInstance getItem() {
        return item;
    }

    public void setItem(L1ItemInstance item) {
        this.item = item;
    }
}
