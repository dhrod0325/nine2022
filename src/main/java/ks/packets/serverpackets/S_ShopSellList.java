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
import ks.system.dogFight.L1DogFight;
import ks.system.race.L1RaceManager;
import ks.util.L1CommonUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class S_ShopSellList extends ServerBasePacket {
    public S_ShopSellList(int objId) {
        writeC(L1Opcodes.S_OPCODE_SHOWSHOPBUYLIST);
        writeD(objId);

        L1Object npcObj = L1World.getInstance().findObject(objId);

        if (!(npcObj instanceof L1NpcInstance)) {
            writeH(0);
            return;
        }

        int npcId = ((L1NpcInstance) npcObj).getTemplate().getNpcId();

        L1Shop shop = ShopTable.getInstance().findShop(npcId);

        if (shop == null) {
            return;
        }

        List<L1ShopItem> shopItems = shop.getSellingItems();
        writeH(shopItems.size());

        for (int i = 0; i < shopItems.size(); i++) {
            L1ShopItem shopItem = shopItems.get(i);

            if (shopItem == null) {
                logger.warn("shopItem is null idx :" + i);
                continue;
            }

            if (shopItem.getItem() == null) {
                logger.warn("shopItem.getItem() is null itemId :" + shopItem.getItemId());
                continue;
            }

            L1Item item = shopItem.getItem();
            int price = shopItem.getPrice();

            writeD(i);
            writeH(shopItem.getItem().getGfxId());
            writeD(price);

            if (shopItem.getPackCount() > 1) {
                String n = item.getNameId();
                writeS(n + " (" + shopItem.getPackCount() + ")");
            } else if (shopItem.getItem().getMaxUseTime() > 0) {
                if (shopItem.getEnchant() > 0) {
                    writeS("+" + shopItem.getEnchant() + " " + item.getName() + " [" + TimeFormat(item.getMaxUseTime()) + "]");
                } else {
                    writeS(item.getName() + " [" + TimeFormat(item.getMaxUseTime()) + "]");
                }
            } else {
                if (item.getItemId() == L1RaceManager.SHOP_ITEM_ID || item.getItemId() == L1DogFight.SHOP_ITEM_ID) {
                    String[] temp = item.getName().split(" ");

                    String buf = temp[temp.length - 1];
                    temp = buf.split("-");

                    writeS(buf + " $" + (1212 + Integer.parseInt(temp[temp.length - 1])));
                } else {
                    String msg = "";

                    if (item.getItemId() != 6000112 && item.getItemId() != 6000113 && item.getItemId() != 6000114) {
                        if (item.getBless() == 0) {
                            msg += "[축] ";
                        }

                        if (item.getBless() == 2) {
                            msg += "[저] ";
                        }
                    }

                    if (shopItem.getEnchant() > 0) {
                        msg += "+" + shopItem.getEnchant() + " " + item.getNameId();
                    } else {
                        msg += item.getNameId();
                    }

                    if (!StringUtils.isEmpty(item.getColor())) {
                        msg = "\\" + item.getColor() + msg;
                    }

                    writeS(msg);
                }
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

        writeC(0x07);
        writeC(0x00);
    }

    public static String TimeFormat(int time) {
        int hour = time / 60 / 60;
        int min = (time - hour * 60 * 60) / 60;
        int sec = time - (hour * 60 * 60) - (min * 60);
        return String.format("%02d", hour) + ":" + String.format("%02d", min) + ":" + String.format("%02d", sec);
    }
}
