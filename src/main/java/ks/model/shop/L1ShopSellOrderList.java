package ks.model.shop;

import ks.model.instance.L1ItemInstance;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_SystemMessage;

import java.util.ArrayList;
import java.util.List;

public class L1ShopSellOrderList {
    private final L1Shop shop;

    private final L1PcInstance pc;

    private final List<L1ShopSellOrder> list = new ArrayList<>();

    private int hasBug = 0;

    public L1ShopSellOrderList(L1Shop shop, L1PcInstance pc) {
        this.shop = shop;
        this.pc = pc;
    }

    public void add(int itemObjectId, int count, L1PcInstance pc) {
        L1ItemInstance item = pc.getInventory().getItem(itemObjectId);

        if (item == null) {
            hasBug = 1;
            return;
        }

        if (itemObjectId != item.getId()) {
            hasBug = 1;
            return;
        }

        int itemType = item.getItem().getType2();

        if ((itemType == 1 && count != 1) || (itemType == 2 && count != 1)) {
            hasBug = 1;
            return;
        }

        if (item.getCount() < 0 || item.getCount() < count) {
            hasBug = 1;
            return;
        }

        if (count <= 0) {
            pc.kick();
            hasBug = 1;
            return;
        }

        if (count > 10000000) {
            pc.sendPackets(new S_SystemMessage("천만개 이상은 판매하지 못합니다."));
            return;
        }

        if (!item.isStackable() && count != 1) {
            hasBug = 1;
            return;
        }

        if (item.getCount() <= 0) {
            hasBug = 1;
            return;
        }

        if (item.getBless() >= 128) {
            return;
        }

        L1AccessedItem assessedItem = shop.assessItem(this.pc.getInventory().getItem(itemObjectId));

        if (assessedItem == null) {
            throw new IllegalArgumentException();
        }

        float dividend = 1;

        list.add(new L1ShopSellOrder(assessedItem, count, dividend));
    }

    public int bugOk() {
        return hasBug;
    }

    L1PcInstance getPc() {
        return pc;
    }

    List<L1ShopSellOrder> getList() {
        return list;
    }
}
