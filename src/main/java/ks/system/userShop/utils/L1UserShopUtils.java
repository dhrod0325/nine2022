package ks.system.userShop.utils;

import ks.model.instance.L1ItemInstance;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_ServerMessage;
import ks.util.L1CommonUtils;

public class L1UserShopUtils {
    public static boolean isValidBase(L1PcInstance pc, L1ItemInstance item, int count) {
        if (count > 100) {
            return false;
        }

        if (count <= 0) {
            pc.disconnect();
            return false;
        }

        if (item == null)
            return false;

        if (item.getItemDelay().isDelay()) {
            return false;
        }

        if (item.isEquipped()) {
            pc.sendPackets(new S_ServerMessage(905, "")); // 장비 하고 있는
            return false;
        }

        int itemType = item.getItem().getType2();

        if (!item.isStackable() && count != 1) {
            pc.disconnect();
            return false;
        }

        if (item.getCount() < count) {
            pc.disconnect();
            return false;
        }

        if ((itemType == 1 && item.getCount() != 1) || (itemType == 2 && item.getCount() != 1)) {
            pc.disconnect();
            return false;
        }

        if (item.getCount() <= 0) {
            pc.disconnect();
            return false;
        }

        return item.getCount() <= 2000000000;
    }

    public static boolean isValidItem(L1PcInstance pc, L1ItemInstance item, int count, int typePrice) {
        if (!isValidBase(pc, item, count)) {
            return false;
        }

        if (L1CommonUtils.isNotAvailablePcWeight(pc, item, count))
            return false;

        if (L1CommonUtils.isOverMaxAdena(pc, typePrice, count))
            return false;

        int price = count * typePrice;

        if (price <= 0 || price > 2000000000)
            return false;

        return item.getCount() <= 9999;
    }

}
