package ks.model.shop;

public class L1ShopSellOrder {
    private final L1AccessedItem item;
    private final int count;
    private final float dividend;

    public L1ShopSellOrder(L1AccessedItem item, int count, float dividend) {
        this.item = item;
        this.count = count;
        this.dividend = dividend;
    }

    public L1AccessedItem getItem() {
        return item;
    }

    public int getCount() {
        return count;
    }

    public float getDividend() {
        return dividend;
    }
}
