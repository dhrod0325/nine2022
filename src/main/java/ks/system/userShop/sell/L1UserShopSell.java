package ks.system.userShop.sell;

import ks.app.config.prop.CodeConfig;
import ks.constants.L1ItemId;
import ks.core.network.opcode.L1Opcodes;
import ks.model.L1Object;
import ks.model.L1PrivateShopSell;
import ks.model.L1Teleport;
import ks.model.instance.L1ItemInstance;
import ks.model.pc.L1PcInstance;
import ks.packets.clientpackets.C_ShopAndWarehouse;
import ks.packets.serverpackets.S_ChatPacket;
import ks.packets.serverpackets.S_ServerMessage;
import ks.packets.serverpackets.S_SystemMessage;
import ks.system.userShop.L1UserShopHandleMessenger;
import ks.system.userShop.L1UserShopNpcInstance;
import ks.system.userShop.sell.packet.S_PrivateShopSellPrice;
import ks.system.userShop.sell.packet.S_PrivateShopSellStep1;
import ks.system.userShop.table.L1UserShop;
import ks.system.userShop.table.L1UserShopTable;
import ks.system.userShop.utils.L1UserShopUtils;
import ks.util.L1CommonUtils;
import ks.util.log.L1LogUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class L1UserShopSell {
    private final Logger logger = LogManager.getLogger();

    private final List<L1PrivateShopSell> sellList = new ArrayList<>();

    private final L1UserShopNpcInstance npc;
    private final L1UserShopTable shopTable = L1UserShopTable.getInstance();

    public L1UserShopSell(L1UserShopNpcInstance npc) {
        this.npc = npc;
    }

    public void step1() {
        L1PcInstance pc = npc.getMaster();

        L1UserShopHandleMessenger messenger = npc.prepareStep1();

        if (messenger != null) {
            messenger.setType(0);

            pc.sendPackets(new S_PrivateShopSellStep1(pc, messenger.getHandleId(), pc.getInventory().getItems()));
            pc.sendPackets("상점 : 판매할 아이템을 선택하세요");
        }
    }

    public void step2(L1UserShopHandleMessenger messenger, int size, C_ShopAndWarehouse warehousePacket) {
        messenger.setStep(1);

        L1UserShopNpcInstance shopNpcInstance = messenger.getShopInstance();
        L1PcInstance pc = shopNpcInstance.getMaster();

        if (size + sellList.size() > CodeConfig.AUTO_SHOP_MAX_ITEM_COUNT) {
            pc.sendPackets("상점 : 물건을 " + CodeConfig.AUTO_SHOP_MAX_ITEM_COUNT + "가지 이상 등록할수없습니다");
            return;
        }

        pc.sendPackets("상점 : 개당 판매 금액을 설정하세요");

        List<L1PrivateShopSell> sellList = messenger.getSellList();

        for (int i = 0; i < size; i++) {
            int objectId = warehousePacket.readD();
            int count = warehousePacket.readD();

            L1ItemInstance item = pc.getInventory().getItem(objectId);

            if (!L1CommonUtils.isValidSellItem(pc, objectId, item, count)) {
                return;
            }

            L1PrivateShopSell privateShopSell = new L1PrivateShopSell();
            privateShopSell.setItemObjectId(item.getId());
            privateShopSell.setSellPrice(0);
            privateShopSell.setItem(item);
            privateShopSell.setSellTotalCount(count);

            sellList.add(privateShopSell);
        }

        pc.sendPackets(new S_PrivateShopSellPrice(messenger.getHandleId(), sellList));
    }

    public void step3(L1UserShopHandleMessenger messenger, int size, C_ShopAndWarehouse warehousePacket) {
        L1UserShopNpcInstance shopNpcInstance = messenger.getShopInstance();
        L1PcInstance pc = shopNpcInstance.getMaster();

        for (int i = 0; i < size; i++) {
            int objectId = warehousePacket.readD();
            int price = warehousePacket.readD();

            if (price <= 0 || price >= 2000000000) {
                continue;
            }

            L1PrivateShopSell sell = messenger.findSell(objectId);
            sell.setSellPrice(price);
        }

        if (totalRegisterPrice(messenger) > CodeConfig.MAX_TRADE_PRICE) {
            pc.sendPackets("상점 : 물품 등록 금액의 합계는 " + NumberFormat.getInstance().format(CodeConfig.MAX_TRADE_PRICE) + " 넘을수없습니다");
            return;
        }

        for (L1PrivateShopSell sell : messenger.getSellList()) {
            if (sell.getSellPrice() <= 0) {
                pc.sendPackets("상점 : 판매금액을 0원으로 설정할 수 없습니다");
                return;
            }

            L1ItemInstance check = pc.getInventory().findItemId(sell.getItem().getItemId());

            if (check != null && check.isStackable() && shopNpcInstance.getInventory().findItemId(sell.getItem().getItemId()) != null) {
                pc.sendPackets("상점 : 겹칠수 있는 아이템은 같은 종류를 두개 이상 등록할수없습니다");
                return;
            }
        }

        for (L1PrivateShopSell sell : messenger.getSellList()) {
            L1ItemInstance storeItem = npc.getMaster().getInventory().findItemObjId(sell.getItemObjectId());
            L1ItemInstance newItem = npc.getMaster().getInventory().tradeItem(storeItem, sell.getSellTotalCount(), npc.getInventory());

            if (newItem != null) {
                sell.setItemObjectId(newItem.getId());
            }
        }

        for (L1PrivateShopSell vo : messenger.getSellList()) {
            L1UserShop shop = createUserShop(vo);
            shop.setType("sell");
            shop.setItemObjectId(vo.getItemObjectId());
            shop.setItemName(vo.getItem().getName());

            shopTable.saveShopItem(shop);

            String name = L1LogUtils.logItemName(vo.getItem(), vo.getSellTotalCount());

            L1LogUtils.userShopLog("[상점 등록] : {} - {}을 개당 {}에 등록", pc.getName(), name, NumberFormat.getInstance().format(vo.getSellPrice()));
        }

        npc.openShop(pc);

        L1Teleport.teleport(pc, npc.getX(), npc.getY() + 1, npc.getMapId(), npc.getHeading(), false);

        sellList.addAll(messenger.getSellList());
    }

    private int totalRegisterPrice(L1UserShopHandleMessenger messenger) {
        int sum = 0;

        for (L1PrivateShopSell a : sellList) {
            sum += a.getSellPrice() * a.getSellTotalCount();
        }

        for (L1PrivateShopSell a : messenger.getSellList()) {
            sum += a.getSellPrice() * a.getSellTotalCount();
        }

        return sum;
    }

    public List<L1PrivateShopSell> getSellList() {
        return sellList;
    }

    public void process(L1PcInstance pc, L1Object findObject, int size, C_ShopAndWarehouse packet) {
        if (!npc.equals(findObject)) {
            return;
        }

        boolean[] isRemoveFromList = new boolean[CodeConfig.AUTO_SHOP_MAX_ITEM_COUNT];

        List<Integer> orderList = new ArrayList<>();
        List<Integer> countList = new ArrayList<>();

        for (int i = 0; i < size; i++) { // 구입 예정의 상품
            orderList.add(packet.readD());
            countList.add(packet.readD());
        }

        List<L1PrivateShopSell> sellList = npc.getSellList();

        if (pc.getPartnersPrivateShopItemCount() != sellList.size())
            return;

        if (pc.getPartnersPrivateShopItemCount() < sellList.size())
            return;

        for (int i = 0; i < orderList.size(); i++) { // 구입 예정의 상품
            int order = orderList.get(i);
            int count = countList.get(i);

            L1PrivateShopSell vo = sellList.get(order);
            int itemObjectId = vo.getItemObjectId();
            int sellPrice = vo.getSellPrice();
            int sellTotalCount = vo.getSellTotalCount(); // 팔 예정의 개수
            int sellCount = vo.getSellCount(); // 판 누계

            L1ItemInstance item = npc.getInventory().getItem(itemObjectId);

            if (item == null)
                return;

            if (!L1UserShopUtils.isValidItem(pc, item, count, sellPrice)) {
                return;
            }

            if (L1CommonUtils.isNotAvailableTrade(pc, npc, itemObjectId, item, count))
                return;

            int price = count * sellPrice;

            if (price <= 0 || price > 2000000000)
                return;

            if (count > sellTotalCount - sellCount)
                count = sellTotalCount - sellCount;

            if (count == 0)
                return;

            if (count >= item.getCount()) {
                count = item.getCount();
            }

            if (npc.getMasterObjId() == pc.getId()) {
                L1LogUtils.userShopLog("[상점 회수] - {} : {}을 회수", pc.getName(), item.getLogName(count));

                npc.getInventory().tradeItem(item, count, pc.getInventory());

                String message = item.getNumberedViewName(count);
                pc.sendPackets(message + "를 회수하였습니다");
            } else {
                if (!pc.getInventory().checkItem(L1ItemId.ADENA, price)) {
                    pc.sendPackets(new S_ChatPacket(pc, "아데나가 충분하지 않습니다.", L1Opcodes.S_OPCODE_MSG, 20));
                    break;
                }

                L1ItemInstance adena = pc.getInventory().findItemId(L1ItemId.ADENA);

                if (adena == null)
                    break;

                if (npc.getInventory().tradeItem(item, count, pc.getInventory()) == null)
                    break;

                pc.getInventory().tradeItem(adena, price, npc.getInventory());

                String message = item.getNumberedViewName(count);
                npc.sendPackets(new S_ServerMessage(877, pc.getName(), message));

                String itemName = L1LogUtils.logItemName(item, count);

                L1LogUtils.userShopLog("[상점 구매] : {} - {}을 {}에 구매", pc.getName(), itemName, price);
                L1LogUtils.userShopLog("[상점 판매] : {} - {}을 {}에 판매", npc.getName(), itemName, price);

                pc.sendPackets("상점 : " + itemName + "을 구매 하였습니다.");

                npc.sendPacketToMaster(new S_SystemMessage("상점 : " + itemName + "을 판매하였습니다"));
            }

            try {
                vo.setSellCount(count + sellCount);
                sellList.set(order, vo);

                L1UserShop shop = createUserShop(vo);
                shop.setType("sell");
                shop.setItemName(vo.getItem().getName());

                shopTable.saveShopItem(shop);
                shopTable.updateShopLoc(npc);

                pc.saveInventory();

                if (vo.getSellCount() == vo.getSellTotalCount()) {
                    isRemoveFromList[order] = true;
                }
            } catch (Exception e) {
                logger.error("오류", e);
            }
        }

        for (int i = CodeConfig.AUTO_SHOP_MAX_ITEM_COUNT - 1; i >= 0; i--) {
            if (isRemoveFromList[i]) {
                L1PrivateShopSell sell = sellList.get(i);

                L1UserShop l1UserShop = createUserShop(sell);
                l1UserShop.setType("sell");

                shopTable.deleteShopItem(l1UserShop);

                sellList.remove(i);
            }
        }

        pc.setPartnersPrivateShopItemCount(0);
    }

    private L1UserShop createUserShop(L1PrivateShopSell sell) {
        L1UserShop l1UserShop = new L1UserShop();
        l1UserShop.setCharId(npc.getMasterObjId());
        l1UserShop.setTotalCount(sell.getSellTotalCount());
        l1UserShop.setPrice(sell.getSellPrice());
        l1UserShop.setCount(sell.getSellCount());
        l1UserShop.setItemId(sell.getItem().getItemId());
        l1UserShop.setEnchantLvl(sell.getItem().getEnchantLevel());
        l1UserShop.setAttrLvl(sell.getItem().getAttrEnchantLevel());
        l1UserShop.setDurability(sell.getItem().getDurability());
        l1UserShop.setBless(sell.getItem().getBless());
        l1UserShop.setItemObjectId(sell.getItemObjectId());

        return l1UserShop;
    }

}
