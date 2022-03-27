package ks.model.inventory;

import ks.model.instance.L1ItemInstance;

public class SelectedItem {
    private int count;
    private int price;
    private L1ItemInstance item;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public L1ItemInstance getItem() {
        return item;
    }

    public void setItem(L1ItemInstance item) {
        this.item = item;
    }
}
