package ks.packets.serverpackets;

import ks.core.datatables.ShopTable;
import ks.core.datatables.item.ItemTable;
import ks.core.network.opcode.L1Opcodes;
import ks.model.L1Item;
import ks.model.L1Object;
import ks.model.L1ShopItem;
import ks.model.L1World;
import ks.model.instance.L1ItemInstance;
import ks.model.instance.L1NpcInstance;
import ks.model.shop.L1Shop;
import ks.util.L1CommonUtils;

import java.util.List;

public class S_PremiumShopSellList extends ServerBasePacket {
    public S_PremiumShopSellList(int objId) {
        writeC(L1Opcodes.S_OPCODE_SHOWSHOPBUYLIST);
        writeD(objId);

        L1Object npcObj = L1World.getInstance().findObject(objId);
        if (!(npcObj instanceof L1NpcInstance)) {
            writeH(0);
            return;
        }
        int npcId = ((L1NpcInstance) npcObj).getTemplate().getNpcId();

        L1Shop shop = ShopTable.getInstance().findShop(npcId);
        List<L1ShopItem> shopItems = shop.getSellingItems();

        writeH(shopItems.size());

        for (int i = 0; i < shopItems.size(); i++) {
            L1ShopItem shopItem = shopItems.get(i);
            L1Item item = shopItem.getItem();
            int price = shopItem.getPrice();
            writeD(i);
            writeH(shopItem.getItem().getGfxId());
            writeD(price);
            if (shopItem.getPackCount() > 1) {
                writeS(item.getName() + " (" + shopItem.getPackCount() + ")");
            } else if (shopItem.getEnchant() > 0) {
                writeS("+" + shopItem.getEnchant() + " " + item.getName());
            } else if (shopItem.getItem().getMaxUseTime() > 0) {
                writeS(item.getName() + " \\fU[" + TimeFormat(item.getMaxUseTime()) + "]");
            } else {
                writeS(item.getNameId());
            }

            L1Item template = ItemTable.getInstance().getTemplate(item.getItemId());

            if (template == null) {
                writeC(0);
            } else {
                L1ItemInstance dummy = L1CommonUtils.createDummyItemInstance(template);
                byte[] status = dummy.getStatusBytes();
                writeC(status.length);
                for (byte b : status) {
                    writeC(b);
                }
            }
        }
    }

    public static String TimeFormat(int time) {
        int hour = time / 60 / 60;
        int min = (time - hour * 60 * 60) / 60;
        int sec = time - (hour * 60 * 60) - (min * 60);
        return String.format("%02d", hour) + ":" + String.format("%02d", min) + ":" + String.format("%02d", sec);
    }
}
