package ks.system.userShop.buy;

import ks.constants.L1DataMapKey;
import ks.constants.L1ItemId;
import ks.core.datatables.item.ItemTable;
import ks.core.network.opcode.L1Opcodes;
import ks.model.L1Object;
import ks.model.L1PrivateShopBuy;
import ks.model.L1Teleport;
import ks.model.instance.L1ItemInstance;
import ks.model.pc.L1PcInstance;
import ks.packets.clientpackets.C_ShopAndWarehouse;
import ks.packets.serverpackets.S_ChatPacket;
import ks.packets.serverpackets.S_CloseList;
import ks.packets.serverpackets.S_ServerMessage;
import ks.packets.serverpackets.S_SystemMessage;
import ks.system.userShop.L1UserShopHandleMessenger;
import ks.system.userShop.L1UserShopNpcInstance;
import ks.system.userShop.buy.packet.S_PrivateShopBuyPrice;
import ks.system.userShop.buy.packet.S_PrivateShopBuyStep1;
import ks.system.userShop.table.L1UserShop;
import ks.system.userShop.table.L1UserShopTable;
import ks.system.userShop.utils.L1UserShopUtils;
import ks.util.L1CommonUtils;
import ks.util.common.SqlUtils;
import ks.util.log.L1LogUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class L1UserShopBuy {
    private final Logger logger = LogManager.getLogger();

    private final L1UserShopNpcInstance npc;

    private final L1UserShopTable shopTable = L1UserShopTable.getInstance();
    private final List<L1PrivateShopBuy> buyList = new ArrayList<>();
    private final List<L1ItemInstance> tempItemList = new ArrayList<>();

    public L1UserShopBuy(L1UserShopNpcInstance npc) {
        this.npc = npc;
    }

    public void step1(String itemName, int enchant, int bless, int attrLevel) {
        try {
            tempItemList.clear();

            L1PcInstance pc = npc.getMaster();

            if (itemName.length() <= 1) {
                pc.sendPackets("검색어가 너무 짧습니다");
                return;
            }

            L1UserShopHandleMessenger messenger = npc.prepareStep1();

            List<Object> params = new ArrayList<>();
            params.add(itemName);

            if (messenger != null) {
                messenger.setType(1);

                String sql = "  SELECT item_id FROM all_item where name like concat('%',?,'%') and item_id not in(40308) and purchase_able=1   ";

                List<Integer> itemIdList = SqlUtils.queryForList(sql, Integer.class, params.toArray());

                for (Integer itemId : itemIdList) {
                    L1ItemInstance item = ItemTable.getInstance().createItem(itemId);
                    item.setIdentified(true);

                    if (enchant > 0 || bless != 1) {
                        if (item.getItem().isEtc()) {
                            continue;
                        }
                    }

                    if (enchant > 0) {
                        item.setEnchantLevel(enchant);
                    }

                    if (bless != 1) {
                        item.setBless(bless);
                    }

                    if (attrLevel > 0) {
                        item.setAttrEnchantLevel(attrLevel);
                    }

                    if (item.getItem().isPurchaseAble()) {
                        tempItemList.add(item);
                    }
                }

                if (tempItemList.isEmpty()) {
                    pc.sendPackets("아이템 검색에 실패했습니다");
                    pc.sendPackets(new S_CloseList(pc.getId()));
                    return;
                }

                pc.sendPackets("상점 : 매입할 아이템 수량을 선택하세요");

                pc.sendPackets(new S_PrivateShopBuyStep1(messenger.getHandleId(), tempItemList));

                pc.getDataMap().remove(L1DataMapKey.SHOP_BUY_ATTR);
                pc.getDataMap().remove(L1DataMapKey.SHOP_BUY_BLESS);
                pc.getDataMap().remove(L1DataMapKey.SHOP_BUY_ENCHANT);
            }
        } catch (Exception e) {
            logger.error("오류", e);
        }
    }

    public void step2(L1UserShopHandleMessenger messenger, int size, C_ShopAndWarehouse packet) {
        messenger.setStep(1);

        L1UserShopNpcInstance shopNpcInstance = messenger.getShopInstance();
        L1PcInstance pc = shopNpcInstance.getMaster();

        List<L1PrivateShopBuy> buyList = messenger.getBuyList();

        pc.sendPackets("상점 : 개당 매입 금액을 설정하세요");

        for (int i = 0; i < size; i++) {
            int objectId = packet.readD();
            int count = packet.readD();

            if (count > 500) {
                return;
            }

            if (count <= 0) {
                pc.disconnect();
                return;
            }

            L1ItemInstance item = L1CommonUtils.findItemByObjectId(objectId, tempItemList);

            if (item != null) {
                try {
                    int k = shopTable.selectShopCount(npc.getMasterObjId(),
                            "buy",
                            item.getItemId(),
                            item.getEnchantLevel(),
                            item.getBless(),
                            item.getAttrEnchantLevel()
                    );

                    if (k > 0) {
                        pc.sendPackets("이미 매입 등록되어있는 아이템이 포함 되어있습니다");
                        return;
                    }

                    L1PrivateShopBuy shopBuy = new L1PrivateShopBuy();
                    shopBuy.setItemObjectId(item.getId());
                    shopBuy.setBuyPrice(0);
                    shopBuy.setItem(item);
                    shopBuy.setBuyTotalCount(count);

                    buyList.add(shopBuy);
                } catch (Exception e) {
                    logger.error("오류", e);
                }
            }
        }

        if (buyList.isEmpty()) {
            pc.sendPackets("물품 불러오기에 실패했습니다. 다시 시도하세요");
            return;
        }

        pc.sendPackets(new S_PrivateShopBuyPrice(messenger.getHandleId(), buyList));
    }

    public void step3(L1UserShopHandleMessenger messenger, int size, C_ShopAndWarehouse packet) {
        L1UserShopNpcInstance shopNpcInstance = messenger.getShopInstance();
        L1PcInstance pc = shopNpcInstance.getMaster();

        for (int i = 0; i < size; i++) {
            int itemObjectId = packet.readD();
            int price = packet.readD();

            if (price <= 0 || price >= 2000000000) {
                continue;
            }

            L1PrivateShopBuy vo = messenger.findBuy(itemObjectId);
            vo.setBuyPrice(price);
        }

        for (L1PrivateShopBuy vi : messenger.getBuyList()) {
            if (vi.getBuyPrice() <= 0) {
                pc.sendPackets("상점 : 매입금액을 0원으로 설정할 수 없습니다");
                return;
            }
        }

        for (L1PrivateShopBuy vo : messenger.getBuyList()) {
            L1ItemInstance storeItem = npc.getMaster().getInventory().findItemObjId(vo.getItemObjectId());
            L1ItemInstance newItem = npc.getMaster().getInventory().tradeItem(storeItem, vo.getBuyTotalCount(), npc.getInventory());

            if (newItem != null) {
                vo.setItemObjectId(newItem.getId());
            }
        }

        for (L1PrivateShopBuy vo : messenger.getBuyList()) {
            L1UserShop shop = new L1UserShop();
            shop.setCharId(npc.getMasterObjId());
            shop.setTotalCount(vo.getBuyTotalCount());
            shop.setPrice(vo.getBuyPrice());
            shop.setCount(vo.getBuyCount());
            shop.setItemId(vo.getItem().getItemId());
            shop.setEnchantLvl(vo.getItem().getEnchantLevel());
            shop.setAttrLvl(vo.getItem().getAttrEnchantLevel());
            shop.setDurability(vo.getItem().getDurability());
            shop.setBless(vo.getItem().getBless());
            shop.setType("buy");

            shop.setItemName(vo.getItem().getName());

            shopTable.saveShopItem(shop);

            L1LogUtils.userShopLog("[매입 등록] : {} : {}까지 개당 {}에 등록", pc.getName(), L1LogUtils.logItemName(vo.getItem(), vo.getBuyTotalCount()), vo.getBuyPrice());
        }

        npc.openShop(pc);

        L1Teleport.teleport(pc, npc.getX(), npc.getY() + 1, npc.getMapId(), npc.getHeading(), false);

        buyList.addAll(messenger.getBuyList());
    }

    public void process(L1PcInstance pc, L1Object findObject, int size, C_ShopAndWarehouse packet) {
        if (!npc.equals(findObject)) {
            return;
        }

        List<L1PrivateShopBuy> list = npc.getBuyList();

        if (pc.getPartnersPrivateShopItemCount() != list.size()) {
            return;
        }

        if (pc.getPartnersPrivateShopItemCount() < list.size()) {
            return;
        }

        List<Integer> orderList = new ArrayList<>();
        List<Integer> countList = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            orderList.add(packet.readD());
            countList.add(packet.readD());
        }

        for (int i = 0; i < orderList.size(); i++) {
            int order = orderList.get(i);
            int count = countList.get(i);

            L1PrivateShopBuy vo = npc.getPrivateShopShowBuyList().findByObjectId(order);

            int itemObjectId = vo.getItemObjectId();
            int buyPrice = vo.getBuyPrice();

            L1ItemInstance item = pc.getInventory().getItem(itemObjectId);

            if (item == null)
                return;

            if (!L1UserShopUtils.isValidItem(pc, item, count, buyPrice)) {
                return;
            }

            if (L1CommonUtils.isNotAvailablePcWeight(pc, item, count))
                return;

            if (L1CommonUtils.isOverMaxAdena(pc, buyPrice, count))
                return;

            if (L1CommonUtils.isNotAvailableTrade(pc, npc, itemObjectId, item, count))
                return;

            if (count >= item.getCount())
                count = item.getCount();

            int price = count * buyPrice;

            if (price <= 0 || price > 2000000000)
                return;

            int oldCnt = shopTable.selectCurrentBuyCountByItem(npc.getMasterObjId(), item);

            if (oldCnt < 0) {
                oldCnt = 0;
            }

            int updateCount = oldCnt + count;

            int maxCnt = vo.getBuyTotalCount() - oldCnt;

            if (updateCount > vo.getBuyTotalCount()) {
                pc.sendPackets("실패 : 매입상점이 " + maxCnt + "개 까지 구매 가능합니다");
                return;
            }

            if (!npc.getInventory().checkItem(L1ItemId.ADENA, price)) {
                pc.sendPackets(new S_ChatPacket(pc, "실패 : 매입상점에 아데나 부족", L1Opcodes.S_OPCODE_MSG, 20));
                break;
            }

            L1ItemInstance adena = npc.getInventory().findItemId(L1ItemId.ADENA);

            if (adena == null)
                break;

            L1ItemInstance tradeItem = pc.getInventory().tradeItem(item, count, npc.getInventory());

            if (tradeItem == null) {
                break;
            }

            npc.getInventory().tradeItem(adena, price, pc.getInventory());

            String message = item.getItem().getName() + " (" + count + ")";
            npc.sendPackets(new S_ServerMessage(877, pc.getName(), message));

            String name = L1LogUtils.logItemName(vo.getItem(), count);

            L1LogUtils.userShopLog("[상점 매입] : {} : {}을 {}에 매입", pc.getName(), name, price);
            L1LogUtils.userShopLog("[상점 매입] : {} : {}을 {}에 판매", npc.getName(), name, price);

            pc.sendPackets("상점 : " + name + "을 판매하였습니다.");
            npc.sendPacketToMaster(new S_SystemMessage("상점 : " + name + "을 구매 하였습니다"));

            L1UserShop userShop = createUserShop(vo);
            userShop.setItemObjectId(tradeItem.getId());
            userShop.setCount(updateCount);

            shopTable.saveShopBuyItem(userShop);
            shopTable.updateShopLoc(npc.getInventory().getAdenaCount(), pc.getId());
        }

        pc.setPartnersPrivateShopItemCount(0);
    }

    public List<L1PrivateShopBuy> getBuyList() {
        return buyList;
    }

    private L1UserShop createUserShop(L1PrivateShopBuy vo) {
        L1UserShop l1UserShop = new L1UserShop();
        l1UserShop.setCharId(npc.getMasterObjId());
        l1UserShop.setPrice(vo.getBuyPrice());
        l1UserShop.setCount(vo.getBuyCount());
        l1UserShop.setItemId(vo.getItem().getItemId());
        l1UserShop.setEnchantLvl(vo.getItem().getEnchantLevel());
        l1UserShop.setAttrLvl(vo.getItem().getAttrEnchantLevel());
        l1UserShop.setDurability(vo.getItem().getDurability());
        l1UserShop.setBless(vo.getItem().getBless());
        l1UserShop.setItemObjectId(vo.getItemObjectId());

        return l1UserShop;
    }
}
