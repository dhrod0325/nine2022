package ks.model.shop;

import ks.model.L1ShopItem;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_SystemMessage;

import java.util.ArrayList;
import java.util.List;

public class L1ShopBuyOrderList {
    private final L1Shop shop;

    private final List<L1ShopBuyOrder> list = new ArrayList<>();

    private int totalWeight = 0;

    private int totalPrice = 0;

    private int bugok = 0;

    private int totalWeight2 = 0;

    public L1ShopBuyOrderList(L1Shop shop) {
        this.shop = shop;
    }

    public void add(int orderNumber, int count, L1PcInstance pc) {
        if (shop.getSellingItems().size() < orderNumber)
            return;

        L1ShopItem shopItem = shop.getSellingItems().get(orderNumber);
        int price = shopItem.getPrice();

        for (int j = 0; j < count; j++) {
            if (price * j < 0) {
                return;
            }

            if (count > 9999) {
                return;
            }

            if (totalPrice < 0)
                return;
        }

        if (price >= 10000 && count > 50) { // 43억 버그방지
            bugok = 1;
            pc.sendPackets(new S_SystemMessage("1만원이상의 물품은 50개이상 구입할수없습니다."));
            return;
        }

        if (price > 10000000 && count > 1) { // ########### 상점 버그방지 추가
            pc.sendPackets(new S_SystemMessage("10000000원이상의 물품은 1개이상 구입할수없습니다."));
            bugok = 1;
            return;
        }

        totalPrice += price * count;
        totalWeight += shopItem.getItem().getWeight() * shopItem.getPackCount() * count;
        totalWeight2 += (shopItem.getItem().getWeight() / 1000) * shopItem.getPackCount() * count;

        if (totalPrice > 1000000000) {
            bugok = 1;
            pc.sendPackets(new S_SystemMessage("1,000,000,000 이상의 물품을 한번에 구매할 수 없습니다."));
            return;
        }

        if (totalPrice < 0 || price < 0) {
            pc.disconnect();
            bugok = 1;
            return;
        }

        if (totalPrice > 50000000 && totalWeight > 19 && count > 500) {
            pc.kick();
            bugok = 1;
            return;
        }

        if (count <= 0) {
            pc.kick();
            bugok = 1;
            return;
        }

        if ((price >= 1000 && price < 10000) && count >= 1000) {
            bugok = 1;
            return;
        }

        if (shopItem.getItem().isStackable()) {
            list.add(new L1ShopBuyOrder(shopItem, count * shopItem.getPackCount()));
            return;
        }

        for (int i = 0; i < (count * shopItem.getPackCount()); i++) {
            list.add(new L1ShopBuyOrder(shopItem, 1));
        }
    }

    List<L1ShopBuyOrder> getList() {
        return list;
    }

    public int bugOk() {
        return bugok;
    }

    public int getTotalWeight() {
        return totalWeight;
    }

    public int getTotalWeight2() {
        return totalWeight2;
    }

    public int getTotalPrice() {
        return totalPrice;
    }

    public boolean containsItemId(int itemId) {
        for (L1ShopBuyOrder item : list) {
            if (item.getItem().getItemId() == itemId) {
                return true;
            }
        }

        return false;
    }
}
