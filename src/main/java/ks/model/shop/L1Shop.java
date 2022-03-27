package ks.model.shop;

import ks.constants.L1ItemId;
import ks.core.datatables.item.ItemTable;
import ks.core.datatables.npc.NpcTable;
import ks.core.datatables.shopInfo.NpcShopInfo;
import ks.core.datatables.shopInfo.NpcShopInfoTable;
import ks.model.L1Item;
import ks.model.L1Npc;
import ks.model.L1PcInventory;
import ks.model.L1ShopItem;
import ks.model.instance.L1ItemInstance;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_ServerMessage;
import ks.packets.serverpackets.S_SystemMessage;
import ks.system.dogFight.L1DogFight;
import ks.system.race.L1RaceManager;
import ks.util.L1CommonUtils;
import ks.util.common.IntRange;
import ks.util.log.L1LogUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class L1Shop {
    private final Logger logger = LogManager.getLogger();

    private final int npcId;

    private final List<L1ShopItem> sellingItems;

    private final List<L1ShopItem> purchasingItems;

    public L1Shop(int npcId, List<L1ShopItem> sellingItems, List<L1ShopItem> purchasingItems) {
        if (sellingItems == null || purchasingItems == null) {
            throw new NullPointerException();
        }

        this.npcId = npcId;
        this.sellingItems = sellingItems;
        this.purchasingItems = purchasingItems;
    }

    public void sellItems(L1PcInstance pc, L1ShopBuyOrderList orderList) {
        try {
            NpcShopInfo info = NpcShopInfoTable.getInstance().selectByNpcId(npcId);

            if (info != null) {
                if (!ensureByInfo(info, pc, orderList)) {
                    return;
                }

                sellByInfo(info, pc.getInventory(), orderList);
            } else {
                if (!ensureSell(pc, orderList)) {
                    return;
                }

                sellItems(pc.getInventory(), orderList);
            }
        } catch (Exception e) {
            pc.sendPackets(e.getMessage());
        }
    }

    private void sellByInfo(NpcShopInfo info, L1PcInventory inv, L1ShopBuyOrderList orderList) {
        if (!inv.consumeItem(info.getTargetItemId(), orderList.getTotalPrice())) {
            throw new IllegalStateException("수량 부족 : " + info.getTargetItemName());
        }

        storeItems(inv, orderList);
    }

    private boolean ensureByInfo(NpcShopInfo info, L1PcInstance pc, L1ShopBuyOrderList orderList) {
        if (!ensure(pc, orderList))
            return false;

        int price = orderList.getTotalPrice();

        if (!pc.getInventory().checkItem(info.getTargetItemId(), price)) {
            pc.sendPackets("수량 부족 : " + info.getTargetItemName());
            return false;
        }

        return true;
    }

    private boolean ensureSell(L1PcInstance pc, L1ShopBuyOrderList orderList) {
        if (!ensure(pc, orderList))
            return false;

        if (!pc.getInventory().checkItem(L1ItemId.ADENA, orderList.getTotalPrice())) {
            throw new IllegalStateException("구입에 필요한 아데나를 소비 할 수 없습니다.");
        }

        return true;
    }

    private boolean ensure(L1PcInstance pc, L1ShopBuyOrderList orderList) {
        if (!L1RaceManager.getInstance().ensure(pc, orderList)) {
            return false;
        }

        if (!L1DogFight.getInstance().ensure(pc, orderList)) {
            return false;
        }

        if (isOverWeight(pc, orderList)) {
            return false;
        }

        if (isOverPrice(pc, orderList)) {
            return false;
        }

        return !isOverCount(pc, orderList);
    }

    private void sellItems(L1PcInventory inv, L1ShopBuyOrderList orderList) {
        int adenaCount = inv.getAdenaCount();

        if (!inv.consumeItem(L1ItemId.ADENA, orderList.getTotalPrice())) {
            throw new IllegalStateException("구입에 필요한 아데나를 소비 할 수 없습니다.");
        }

        L1LogUtils.shopLog("[상점구매] - {} 구매시작 / 인벤아덴 : {}", inv.getOwner().getName(), NumberFormat.getInstance().format(adenaCount));

        storeItems(inv, orderList);
    }

    public boolean buyItems(L1ShopSellOrderList orderList) {
        NpcShopInfo info = NpcShopInfoTable.getInstance().selectByNpcId(npcId);

        L1PcInventory inv = orderList.getPc().getInventory();

        int totalPrice = 0;

        for (L1ShopSellOrder order : orderList.getList()) {
            L1ItemInstance item = inv.getItem(order.getItem().getTargetId());

            if (L1CommonUtils.isUsingDoll(item, inv)) {
                inv.getOwner().sendPackets("소환중인 인형은 판매할 수 없습니다");
                return false;
            }

            if (item.getItem().getBless() < 128) {
                int count = inv.removeItem(order.getItem().getTargetId(), order.getCount());
                totalPrice += order.getItem().getAssessedPrice() * count * order.getDividend();
                String msg = "[아이템 판매]" + orderList.getPc().getName() + ",합계:" + count + ",npcId:" + npcId + ",수량:" + totalPrice + ",아이템:" + item.getName() + ",인벤아덴:" + inv.getAdenaCount();
                L1LogUtils.shopLog(msg);
            }
        }

        totalPrice = IntRange.ensure(totalPrice, 0, 2000000000);

        if (totalPrice > 0) {
            int itemId;

            if (info != null) {
                itemId = info.getTargetItemId();
            } else {
                itemId = L1ItemId.ADENA;
            }

            inv.storeItem(itemId, totalPrice);

            String msg = "[아이템 판매완료]" + orderList.getPc().getName() + ",합계:" + totalPrice + ",npcId:" + npcId + ",인벤아덴:" + inv.getAdenaCount() + ",머니id:" + itemId;

            L1LogUtils.shopLog(msg);
        }

        return true;
    }

    private L1ShopItem findShopItemByItemId(int itemId) {
        for (L1ShopItem shopItem : purchasingItems) {
            if (shopItem.getItemId() == itemId) {
                return shopItem;
            }
        }

        return null;
    }

    private boolean isPurchaseNotAbleItem(L1ItemInstance item) {
        return item == null || item.isEquipped() || item.getBless() >= 128 || item.getEnchantLevel() != 0;
    }

    public L1AccessedItem assessItem(L1ItemInstance item) {
        L1ShopItem shopItem = findShopItemByItemId(item.getItemId());

        if (shopItem == null) {
            return null;
        }

        return new L1AccessedItem(item.getId(), getAssessedPrice(shopItem));
    }

    private int getAssessedPrice(L1ShopItem item) {
        return item.getPrice() / item.getPackCount();
    }

    public List<L1AccessedItem> assessItems(L1PcInventory inv) {
        List<L1AccessedItem> result = new ArrayList<>();

        for (L1ShopItem item : purchasingItems) {
            for (L1ItemInstance targetItem : inv.findItemsId(item.getItemId())) {
                if (isPurchaseNotAbleItem(targetItem)) {
                    continue;
                }

                if (L1CommonUtils.isUsingDoll(targetItem, inv)) {
                    continue;
                }

                if (item.getEnchant() == targetItem.getEnchantLevel()) {
                    result.add(new L1AccessedItem(targetItem.getId(), getAssessedPrice(item)));
                }
            }
        }

        return result;
    }

    private void storeItems(L1PcInventory inv, L1ShopBuyOrderList orderList) {
        String ownerName = inv.getOwner().getName();

        for (L1ShopBuyOrder order : orderList.getList()) {
            int itemId = order.getItem().getItemId();
            int amount = order.getCount();
            int enchant = order.getItem().getEnchant();

            L1ItemInstance item = ItemTable.getInstance().createItem(itemId);

            if (isNotAbleSellItem(item)) {
                inv.getOwner().sendPackets(item.getName() + "은 구매 불가능한 아이템입니다");
                return;
            }

            if (item.getItemId() == L1RaceManager.SHOP_ITEM_ID) {
                L1RaceManager.getInstance().bet(item, order, amount);
            } else if (item.getItemId() == L1DogFight.SHOP_ITEM_ID) {
                L1DogFight.getInstance().bet(item, order, amount);
            }

            item.setCount(amount);
            item.setIdentified(true);
            item.setEnchantLevel(enchant);

            inv.storeItem(item);

            L1Npc tpl = NpcTable.getInstance().getTemplate(npcId);
            String npcName = tpl.getName() + "(" + npcId + ")";

            L1LogUtils.shopLog("[상점구매] - {} / {} / 수량:{} / NPC:{}", ownerName, item.getViewName2(), amount, npcName);
        }

        L1LogUtils.shopLog("[상점구매] - {} 구매종료 / 구매금액 {} / 인벤아덴 : {}", ownerName, NumberFormat.getInstance().format(orderList.getTotalPrice()), NumberFormat.getInstance().format(inv.getAdenaCount()));

        inv.getOwner().sendPackets(new S_SystemMessage("아이템을 구매 하였습니다"));
    }

    private boolean isOverWeight(L1PcInstance pc, L1ShopBuyOrderList orderList) {
        int currentWeight = pc.getInventory().getWeight();
        int totalWeight = orderList.getTotalWeight2();
        double maxWeight = pc.getMaxWeight();

        if (currentWeight + totalWeight > maxWeight) {
            pc.sendPackets(new S_ServerMessage(82));
            return true;
        }

        return false;
    }

    private boolean isOverPrice(L1PcInstance pc, L1ShopBuyOrderList orderList) {
        int price = orderList.getTotalPrice();

        if (!IntRange.includes(price, 0, 2000000000)) {
            pc.sendPackets(new S_ServerMessage(904, "2000000000"));
            return true;
        }

        return false;
    }

    private boolean isOverCount(L1PcInstance pc, L1ShopBuyOrderList orderList) {
        int totalCount = pc.getInventory().getSize();

        for (L1ShopBuyOrder order : orderList.getList()) {
            L1Item temp = order.getItem().getItem();
            if (temp.isStackable()) {
                if (!pc.getInventory().checkItem(temp.getItemId())) {
                    totalCount += 1;
                }
            } else {
                totalCount += 1;
            }
        }

        if (totalCount > 180) {
            pc.sendPackets(new S_ServerMessage(263));
            return true;
        }

        return false;
    }

    private boolean isNotAbleSellItem(L1ItemInstance item) {
        for (L1ShopItem shopItem : getSellingItems()) {
            if (shopItem.getItemId() == item.getItemId()) {
                return false;
            }
        }

        return true;
    }

    public List<L1ShopItem> getSellingItems() {
        return sellingItems;
    }

    public List<L1ShopItem> getBuyingItems() {
        return purchasingItems;
    }

    public L1ShopBuyOrderList newBuyOrderList() {
        return new L1ShopBuyOrderList(this);
    }

    public L1ShopSellOrderList newSellOrderList(L1PcInstance pc) {
        return new L1ShopSellOrderList(this, pc);
    }

    public int getNpcId() {
        return npcId;
    }


}
