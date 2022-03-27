package ks.packets.clientpackets;

import ks.app.config.prop.CodeConfig;
import ks.constants.L1ItemId;
import ks.constants.L1SkillId;
import ks.core.datatables.ClanWarehouseList;
import ks.core.datatables.ShopTable;
import ks.core.network.L1Client;
import ks.core.network.opcode.L1Opcodes;
import ks.model.*;
import ks.model.instance.L1ItemInstance;
import ks.model.instance.L1NpcInstance;
import ks.model.inventory.InventoryInfoHandler;
import ks.model.pc.L1PcInstance;
import ks.model.shop.L1Shop;
import ks.model.shop.L1ShopBuyOrderList;
import ks.model.shop.L1ShopSellOrderList;
import ks.model.warehouse.*;
import ks.packets.serverpackets.S_ChatPacket;
import ks.packets.serverpackets.S_ServerMessage;
import ks.packets.serverpackets.S_SystemMessage;
import ks.system.userShop.L1UserShopCreateHandler;
import ks.util.L1CommonUtils;
import ks.util.common.DateUtils;
import ks.util.common.SqlUtils;
import ks.util.log.L1LogUtils;

import java.sql.Timestamp;
import java.util.List;

public class C_ShopAndWarehouse extends ClientBasePacket {
    private static final int TYPE_BUY_SHP = 0; // 상점 or 개인 상점 사기
    private static final int TYPE_SEL_SHP = 1; // 상점 or 개인 상점 팔기

    private static final int TYPE_PUT_PWH = 2; // 개인 창고 맡기기
    private static final int TYPE_GET_PWH = 3; // 개인 창고 찾기

    private static final int TYPE_PUT_CWH = 4; // 혈맹 창고 맡기기
    private static final int TYPE_GET_CWH = 5; // 혈맹 창고 찾기

    private static final int TYPE_GET_EXTRA = 20; //부가서비스 아이템 찾기

    public C_ShopAndWarehouse(byte[] data, L1Client client) {
        super(data);

        int npcObjectId = readD();
        int type = readC();
        int size = readC();

        readC();

        if (npcObjectId == 0) {
            return;
        }

        if (size < 0)
            return;

        L1PcInstance pc = client.getActiveChar();

        if (pc == null) {
            return;
        }

        pc.saveInventory();

        if (pc.getOnlineStatus() == 0 || L1CommonUtils.isTwoLogin(pc)) {
            client.disconnect();
            return;
        }

        if (L1UserShopCreateHandler.getInstance().handle(npcObjectId, size, this)) {
            return;
        }

        if (InventoryInfoHandler.getInstance().handle(npcObjectId, size, this)) {
            return;
        }

        if (size > 150) {
            client.disconnect();
            return;
        }

        int level = pc.getLevel();

        int npcId = 0;
        String npcImpl = "";
        boolean isPrivateShop = false;

        L1Object findObject = L1World.getInstance().findObject(npcObjectId);

        if (findObject == null) {
            client.disconnect();
            return;
        }

        if (L1CommonUtils.isNear(pc.getX(), pc.getY(), findObject.getX(), findObject.getY(), 3)) {
            return;
        }

        if (L1UserShopCreateHandler.getInstance().processTalk(pc, findObject, size, type, this)) {
            return;
        }

        if (findObject instanceof L1NpcInstance) {
            L1NpcInstance targetNpc = (L1NpcInstance) findObject;
            npcId = targetNpc.getTemplate().getNpcId();
            npcImpl = targetNpc.getTemplate().getImpl();
        } else if (findObject instanceof L1PcInstance) {
            isPrivateShop = true;
        }

        L1LogUtils.gmLog(pc, "상점 - type:{},npcId:{},npcImpl:{}", type, npcId, npcImpl);

        if (npcId != 9000001) {//후원상점만 이용가능
            if (L1CommonUtils.isStandByServer(pc)) {
                L1CommonUtils.sendStandByMsg(pc);
                return;
            }
        }


        switch (type) {
            case TYPE_BUY_SHP: // 상점 or 개인 상점 사기
                if (size != 0 && npcImpl.equalsIgnoreCase("L1Merchant")) {
                    buyItemFromShop(pc, npcId, size);
                    break;
                }

                if (size != 0 && isPrivateShop) {
                    buyItemFromPrivateShop(pc, findObject, size);
                    break;
                }
            case TYPE_SEL_SHP:
                if (size != 0 && npcImpl.equalsIgnoreCase("L1Merchant")) {
                    sellItemToShop(pc, npcId, size);
                }

                if (size != 0 && isPrivateShop) {
                    sellItemToPrivateShop(pc, findObject, size);
                }

                break;
            case TYPE_PUT_PWH: // 개인 창고 맡기기
                if (size != 0 && npcImpl.equalsIgnoreCase("L1Dwarf") && level >= 5) {
                    putItemToPrivateWarehouse(pc, size);
                    break;
                }
            case TYPE_GET_PWH: // 개인 창고 찾기
                if (size != 0 && npcImpl.equalsIgnoreCase("L1Dwarf") && level >= 5) {
                    getItemToPrivateWarehouse(pc, size);
                    break;
                }
            case TYPE_GET_EXTRA:
                if (size != 0) {
                    getItemToExtraWarehouse(pc, size);
                    break;
                }

                break;
            case TYPE_PUT_CWH: // 혈맹 창고 맡기기
                if (npcImpl.equalsIgnoreCase("L1Dwarf") && level >= 5) {
                    putItemToClanWarehouse(pc, size);
                    break;
                }
            case TYPE_GET_CWH: // 혈맹 창고 찾기
                if (npcImpl.equalsIgnoreCase("L1Dwarf") && level >= 5) {
                    getItemToClanWarehouse(pc, size);
                    break;
                }
            default:
        }
    }

    private static void updateLog(String name, String clanname, String itemname, int count, int type) {
        Timestamp time = new Timestamp(System.currentTimeMillis());

        SqlUtils.update("INSERT INTO clan_warehouse_log SET name=?, clan_name=?, item_name=?, item_count=?, type=?, time=?",
                name,
                clanname,
                itemname,
                count,
                type,
                time
        );
    }

    private void doNothingClanWarehouse(L1PcInstance pc) {
        if (pc == null)
            return;

        L1Clan clan = L1World.getInstance().getClan(pc.getClanName());
        if (clan == null)
            return;

        ClanWarehouse clanWarehouse = WarehouseManager.getInstance().getClanWarehouse(clan.getClanName());
        if (clanWarehouse == null)
            return;

        clanWarehouse.unlock(pc.getId());
    }

    private void getItemToClanWarehouse(L1PcInstance pc, int size) {
        if (pc.getLevel() < 5)
            return;

        if (size == 0) {
            doNothingClanWarehouse(pc);
            return;
        }

        L1Clan clan = L1World.getInstance().getClan(pc.getClanName());

        if (L1CommonUtils.isNotAvailableClan(pc, clan))
            return;

        ClanWarehouse clanWarehouse = WarehouseManager.getInstance().getClanWarehouse(clan.getClanName());

        if (clanWarehouse == null)
            return;

        for (int i = 0; i < size; i++) {
            int objectId = readD();
            int count = readD();

            L1ItemInstance item = clanWarehouse.getItem(objectId);

            if (!L1CommonUtils.isValidItemInClanWareHouse(pc, objectId, count)) {
                return;
            }

            if (L1CommonUtils.isNotAvailableTrade(pc, objectId, item, count))
                break;

            if (L1CommonUtils.hasNotAdena(pc))
                break;
            if (count >= item.getCount())
                count = item.getCount();
            if (L1CommonUtils.isNotAvailablePcWeight(pc, item, count))
                break;
            if (count <= 0) {
                pc.disconnect();
                return;
            }
            if (objectId != item.getId()) {
                pc.disconnect();
                break;
            }
            if (!item.isStackable() && count != 1) {
                pc.disconnect();
                break;
            }
            if (count > item.getCount()) {
                count = item.getCount();
            }

            if (item.getCount() < count) {
                pc.disconnect();
                break;
            }
            if (count < 1 || item.getCount() <= 0) {
                pc.kick();
                break;
            }
            if (item.getBless() >= 128) {
                pc.sendPackets(new S_SystemMessage("해당 아이템은 창고 이용을 할 수 없습니다."));
                return;
            }

            if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.CHANGGO_TIMER)) {
                pc.sendPackets(new S_SystemMessage("RESTART 후 30초간 창고를 이용 할 수 없습니다."));
                return;
            }

            if (item.getItemDelay().isDelay()) {
                break;
            }

            if (!item.getItem().isToBeSavedAtOnce()) {
                pc.getInventory().saveItem(item, L1PcInventory.COL_COUNT);
            }

            if (item.getCount() > 2000000000) {
                return;
            }

            if (count > 2000000000) {
                return;
            }

            L1CommonUtils.clearMagicItem(pc, item);

            clanWarehouse.tradeItem(item, count, pc.getInventory());
            ClanWarehouseList.getInstance().addList(pc.getClanId(), pc.getName() + " 이(가) 아이템을 찾았습니다. \n" + "[" + (item.getItem().getType2() == 1 || item.getItem().getType2() == 2 ? "+" + item.getEnchantLevel() + " " + item.getName() : item.getName()) + "] x " + count + "개\n", DateUtils.currentTime());
            updateLog(pc.getName(), pc.getClanName(), item.getName(), count, 1);
        }

        clanWarehouse.unlock(pc.getId());
    }

    private void putItemToClanWarehouse(L1PcInstance pc, int size) {
        if (size == 0) {
            doNothingClanWarehouse(pc);
            return;
        }

        L1Clan clan = L1World.getInstance().getClan(pc.getClanName());

        if (L1CommonUtils.isNotAvailableClan(pc, clan))
            return;

        ClanWarehouse clanWarehouse = WarehouseManager.getInstance().getClanWarehouse(clan.getClanName());

        if (clanWarehouse == null)
            return;

        for (int i = 0; i < size; i++) {
            int objectId = readD();
            int count = readD();

            L1ItemInstance item = pc.getInventory().getItem(objectId);

            if (L1CommonUtils.isInValidItemInInventory(pc, objectId, count)) {
                return;
            }

            if (item == null)
                break;

            if (count > item.getCount()) {
                count = item.getCount();
            }

            if (L1CommonUtils.isNotAvailableTrade(pc, objectId, item, count))
                break;

            if (item.getItem().getItemId() >= 76767 && item.getItem().getItemId() <= 76784) {
                pc.sendPackets(new S_SystemMessage("봉인이 풀린 룬, 유물은 창고 이용이 불가능 합니다."));
                return;
            }

            if (item.getCount() > 2000000000) {
                return;
            }

            if (count > 2000000000) {
                return;
            }

            if (L1CommonUtils.isNotAbleWareHouse(pc, item)) {
                pc.sendPackets(new S_SystemMessage("해당 아이템은 창고 이용을 할 수 없습니다."));
                return;
            }

            if (!item.getItem().isTradeAble()) {
                pc.sendPackets(new S_ServerMessage(210, item.getItem().getName()));
                break;
            }

            if (!L1CommonUtils.checkPetList(pc, item))
                break;

            if (L1CommonUtils.isNotAbleWhCount(clanWarehouse, pc, item, count))
                break;

            if (item.getItemDelay().isDelay()) {
                break;
            }

            if (!item.getItem().isToBeSavedAtOnce()) {
                pc.getInventory().saveItem(item, L1PcInventory.COL_COUNT);
            }

            L1CommonUtils.clearMagicItem(pc, item);

            pc.getInventory().tradeItem(objectId, count, clanWarehouse);
            pc.getLight().turnOnOffLight();
            ClanWarehouseList.getInstance().addList(pc.getClanId(), pc.getName() + " 이(가) 아이템을 맡겼습니다. \n" + "[" + (item.getItem().getType2() == 1 || item.getItem().getType2() == 2 ? "+" + item.getEnchantLevel() + " " + item.getName() : item.getName()) + "] x " + count + "개\n", DateUtils.currentTime());

            updateLog(pc.getName(), pc.getClanName(), item.getName(), count, 0);
        }

        clanWarehouse.unlock(pc.getId());
    }

    private void getItemToWarehouse(L1PcInstance pc, Warehouse warehouse, String tableName, int size) {
        if (warehouse == null)
            return;

        for (int i = 0; i < size; i++) {
            int objectId = readD();
            int count = readD();

            L1ItemInstance item = warehouse.getItem(objectId);

            if (!L1CommonUtils.isValidItemInWareHouse(pc, objectId, tableName, count)) {
                return;
            }

            if (L1CommonUtils.isNotAvailableTrade(pc, objectId, item, count)) {
                break;
            }

            if (count < 0) {
                continue;
            }

            if (objectId != item.getId()) {
                pc.disconnect();
                break;
            }

            if (!item.isStackable() && count != 1) {
                pc.disconnect();
                break;
            }

            if (count > item.getCount()) {
                count = item.getCount();
            }

            /* 버그방지 */
            if (item.getCount() < count) {
                pc.disconnect();
                break;
            }

            if (count < 1 || item.getCount() <= 0) {
                pc.kick();
                break;
            }

            if (!pc.isGm() && item.getItem().getItemId() == 40308) {
                if (count > CodeConfig.MAX_TRADE_PRICE) {
                    pc.sendPackets(new S_SystemMessage("아데나는 " + CodeConfig.MAX_TRADE_PRICE + "억이상 한번에 찾을수 없습니다."));
                    return;
                }
            }

            if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.CHANGGO_TIMER)) {
                pc.sendPackets(new S_SystemMessage("RESTART 후 30초간 창고를 이용 할 수 없습니다."));
                return;
            }

            if (item.getCount() > 2000000000) {
                return;
            }

            if (count > 2000000000) {
                return;
            }

            if (count > item.getCount())
                count = item.getCount();

            if (L1CommonUtils.isNotAvailablePcWeight(pc, item, count)) {
                return;
            }

            if (L1CommonUtils.hasNotAdena(pc))
                break;

            if (item.getItemDelay().isDelay()) {
                break;
            }

            if (!item.getItem().isToBeSavedAtOnce()) {
                pc.getInventory().saveItem(item, L1PcInventory.COL_COUNT);
            }

            L1CommonUtils.clearMagicItem(pc, item);
            warehouse.tradeItem(item, count, pc.getInventory());

        }
    }

    private void getItemToPrivateWarehouse(L1PcInstance pc, int size) {
        PrivateWarehouse warehouse = WarehouseManager.getInstance().getPrivateWarehouse(pc.getAccountName());
        getItemToWarehouse(pc, warehouse, "character_warehouse", size);
    }

    private void getItemToExtraWarehouse(L1PcInstance pc, int size) {
        ExtraWarehouse warehouse = WarehouseManager.getInstance().getExtraWarehouse(pc.getAccountName());
        getItemToWarehouse(pc, warehouse, "character_extra_warehouse", size);
    }

    private void putItemToPrivateWarehouse(L1PcInstance pc, int size) {
        PrivateWarehouse warehouse = WarehouseManager.getInstance().getPrivateWarehouse(pc.getAccountName());

        if (warehouse == null)
            return;

        for (int i = 0; i < size; i++) {
            int objectId = readD();
            int count = readD();

            if (count < 0)
                return;

            L1ItemInstance item = pc.getInventory().getItem(objectId);

            if (L1CommonUtils.isInValidItemInInventory(pc, objectId, count)) {
                return;
            }

            if (item.getItem().getItemId() >= 76767 && item.getItem().getItemId() <= 76784) {
                pc.sendPackets(new S_SystemMessage("봉인이 풀린 룬, 유물은 창고 이용이 불가능 합니다."));
                return;
            }

            if (item.getCount() > 2000000000) {
                return;
            }

            if (count > 2000000000) {
                return;
            }

            if (L1CommonUtils.isNotAvailableTrade(pc, objectId, item, count))
                return;

            if (L1CommonUtils.isNotAbleWareHouse(pc, item)) {
                pc.sendPackets(new S_SystemMessage("해당 아이템은 창고 이용을 할 수 없습니다."));
                return;
            }

            if (!L1CommonUtils.checkPetList(pc, item))
                return;

            if (L1CommonUtils.isNotAbleWhCount(warehouse, pc, item, count))
                return;

            if (!item.getItem().isToBeSavedAtOnce()) {
                pc.getInventory().saveItem(item, L1PcInventory.COL_COUNT);
            }

            if (count > item.getCount())
                count = item.getCount();

            L1CommonUtils.clearMagicItem(pc, item);

            pc.getInventory().tradeItem(objectId, count, warehouse);
            pc.getLight().turnOnOffLight();

        }
    }

    private void sellItemToPrivateShop(L1PcInstance pc, L1Object findObject, int size) {
        L1PcInstance target = null;

        if (findObject instanceof L1PcInstance) {
            target = (L1PcInstance) findObject;

            if (target.isTradingInPrivateShop())
                return;
        }

        if (target == null)
            return;

        target.setTradingInPrivateShop(true);

        boolean[] isRemoveFromList = new boolean[8];
        List<L1PrivateShopBuy> buyList = target.getBuyList();

        synchronized (buyList) {
            int order, itemObjectId, count, buyPrice, buyTotalCount, buyCount;

            for (int i = 0; i < size; i++) {
                itemObjectId = readD();
                count = readCH();
                order = readC();

                L1ItemInstance item = pc.getInventory().getItem(itemObjectId);

                if (L1CommonUtils.isNotAvailableTrade(pc, itemObjectId, item, count))
                    break;
                if (item.getBless() >= 128) {
                    pc.sendPackets(new S_ServerMessage(210, item.getItem().getName()));
                    break;
                }

                L1PrivateShopBuy psbl = buyList.get(order);
                buyPrice = psbl.getBuyPrice();
                buyTotalCount = psbl.getBuyTotalCount(); // 살 예정의 개수
                buyCount = psbl.getBuyCount(); // 산 누계

                if (count > buyTotalCount - buyCount)
                    count = buyTotalCount - buyCount;

                if (item.isEquipped()) {
                    pc.sendPackets(new S_ServerMessage(905));
                    break;
                }

                if (L1CommonUtils.isNotAvailablePcWeight(pc, item, count))
                    break;
                if (L1CommonUtils.isOverMaxAdena(target, buyPrice, count))
                    return;

                int itemType = item.getItem().getType2();

                if (itemObjectId != item.getId()) {
                    pc.disconnect();
                    target.disconnect();
                    return;
                }
                if (!item.isStackable() && count != 1) {
                    pc.disconnect();
                    target.disconnect();
                    return;
                }
                if (count >= item.getCount()) {
                    count = item.getCount();
                }
                /* 버그방지 */
                if ((itemType == 1 && count != 1)
                        || (itemType == 2 && count != 1)) {
                    return;
                }
                if (item.getCount() <= 0 || count <= 0
                        || item.getCount() < count) {
                    pc.disconnect();
                    target.disconnect();
                    return;
                }
                if (buyPrice * count <= 0 || buyPrice * count > 2000000000) {
                    return;
                }

                if (item.getCount() > 2000000000) {
                    return;
                }
                if (count > 2000000000) {
                    return;
                }

                if (count >= item.getCount())
                    count = item.getCount();

                if (!target.getInventory().checkItem(L1ItemId.ADENA, count * buyPrice)) {
                    target.sendPackets(new S_ServerMessage(189));
                    break;
                }

                L1ItemInstance adena = target.getInventory().findItemId(L1ItemId.ADENA);
                if (adena == null)
                    break;

                target.getInventory().tradeItem(adena, count * buyPrice, pc.getInventory());
                pc.getInventory().tradeItem(item, count, target.getInventory());
                psbl.setBuyCount(count + buyCount);
                buyList.set(order, psbl);

                if (psbl.getBuyCount() == psbl.getBuyTotalCount()) {
                    isRemoveFromList[order] = true;
                }

                try {
                    pc.saveInventory();
                    target.saveInventory();
                } catch (Exception e) {
                    logger.error(e);
                }
            }

            for (int i = 7; i >= 0; i--) {
                if (isRemoveFromList[i]) {
                    buyList.remove(i);
                }
            }

            target.setTradingInPrivateShop(false);
        }
    }

    private void sellItemToShop(L1PcInstance pc, int npcId, int size) {
        L1Shop shop = ShopTable.getInstance().findShop(npcId);
        L1ShopSellOrderList orderList = shop.newSellOrderList(pc);

        for (int i = 0; i < size; i++) {
            int itemNumber = readD();
            long itemCount = readD();

            if (itemCount <= 0) {
                return;
            }

            orderList.add(itemNumber, (int) itemCount, pc);

            if (orderList.bugOk() != 0) {
                for (L1PcInstance player : L1World.getInstance().getAllPlayers()) {
                    if (player.isGm() || pc == player) {
                        player.sendPackets(new S_SystemMessage(pc.getName() + "님 상점 최대 구매 수량 초과 (" + itemCount + ")"));
                    }
                }
            }
        }

        int bugOk = orderList.bugOk();

        if (bugOk == 0) {
            if (shop.buyItems(orderList)) {
                pc.saveInventory();
                pc.sendPackets(new S_SystemMessage("아이템을 판매 하였습니다"));
            } else {
                pc.sendPackets(new S_SystemMessage("아이템 판매에 실패하였습니다"));
            }
        } else {
            pc.sendPackets(new S_SystemMessage("아이템 판매에 실패하였습니다"));
        }
    }

    private void buyItemFromPrivateShop(L1PcInstance pc, L1Object object, int size) {
        L1PcInstance targetPc = null;

        if (object instanceof L1PcInstance) {
            targetPc = (L1PcInstance) object;
        }

        if (targetPc == null) {
            return;
        }

        if (targetPc.isTradingInPrivateShop())
            return;

        List<L1PrivateShopSell> sellList = targetPc.getSellList();

        synchronized (sellList) {
            // 품절이 발생해, 열람중의 아이템수와 리스트수가 다르다
            if (pc.getPartnersPrivateShopItemCount() != sellList.size())
                return;
            if (pc.getPartnersPrivateShopItemCount() < sellList.size())
                return;

            targetPc.setTradingInPrivateShop(true);

            L1ItemInstance item;
            L1PrivateShopSell pssl;
            boolean[] isRemoveFromList = new boolean[8];
            int order, count, price, sellCount, sellPrice, itemObjectId, sellTotalCount;
            for (int i = 0; i < size; i++) { // 구입 예정의 상품
                order = readD();
                count = readD();

                pssl = sellList.get(order);
                itemObjectId = pssl.getItemObjectId();
                sellPrice = pssl.getSellPrice();
                sellTotalCount = pssl.getSellTotalCount(); // 팔 예정의 개수
                sellCount = pssl.getSellCount(); // 판 누계
                item = targetPc.getInventory().getItem(itemObjectId);

                if (item == null)
                    break;

                if (item.getItemDelay().isDelay()) {
                    break;
                }

                if (item.isEquipped()) {
                    pc.sendPackets(new S_ServerMessage(905, ""));
                    break;
                }
                if (count > sellTotalCount - sellCount)
                    count = sellTotalCount - sellCount;
                if (count == 0)
                    break;

                int itemType = item.getItem().getType2();

                if (count > 100) {
                    pc.sendPackets(new S_SystemMessage("최대 구매 수량 : 잡템류(100개씩) / 장비류(1개씩)"));
                    break;
                }
                if (count <= 0) {
                    pc.disconnect();
                    return;
                }

                if (!item.isStackable() && count != 1) {
                    pc.disconnect();
                    return;
                }
                if (item.getCount() < count) {
                    pc.disconnect();
                    return;
                }
                if ((itemType == 1 && item.getCount() != 1) || (itemType == 2 && item.getCount() != 1)) {
                    pc.disconnect();
                    return;
                }
                if (item.getCount() <= 0) {
                    pc.disconnect();
                    return;
                }
                if (item.getCount() > 2000000000) {
                    return;
                }
                if (L1CommonUtils.isNotAvailablePcWeight(pc, item, count))
                    break;
                if (L1CommonUtils.isOverMaxAdena(pc, sellPrice, count))
                    break;

                price = count * sellPrice;
                if (price <= 0 || price > 2000000000)
                    break;

                if (L1CommonUtils.isNotAvailableTrade(pc, targetPc, itemObjectId, item, count))
                    break;

                if (count >= item.getCount())
                    count = item.getCount();
                if (item.getCount() > 9999)
                    break;

                if (!pc.getInventory().checkItem(L1ItemId.ADENA, price)) {
                    pc.sendPackets(new S_ChatPacket(pc, "아데나가 충분하지 않습니다.", L1Opcodes.S_OPCODE_MSG, 20));
                    break;
                }

                L1ItemInstance adena = pc.getInventory().findItemId(L1ItemId.ADENA);

                if (adena == null)
                    break;
                if (targetPc.getInventory().tradeItem(item, count, pc.getInventory()) == null)
                    break;

                pc.getInventory().tradeItem(adena, price, targetPc.getInventory());

                // %1%o %0에 판매했습니다.
                String message = item.getItem().getName() + " (" + count + ")";
                targetPc.sendPackets(new S_ServerMessage(877, pc.getName(), message));

                pssl.setSellCount(count + sellCount);
                sellList.set(order, pssl);

                if (pssl.getSellCount() == pssl.getSellTotalCount())
                    isRemoveFromList[order] = true;
                try {
                    pc.saveInventory();
                    targetPc.saveInventory();
                } catch (Exception e) {
                    logger.error("오류", e);
                }
            }

            // 품절된 아이템을 리스트의 말미로부터 삭제
            for (int i = 7; i >= 0; i--) {
                if (isRemoveFromList[i]) {
                    sellList.remove(i);
                }
            }

            targetPc.setTradingInPrivateShop(false);
        }
    }

    private void buyItemFromShop(L1PcInstance pc, int npcId, int size) {
        L1Shop shop = ShopTable.getInstance().findShop(npcId);
        L1ShopBuyOrderList orderList = shop.newBuyOrderList();

        if (shop.getSellingItems().size() < size) {
            pc.disconnect();
            return;
        }

        for (int i = 0; i < size; i++) {
            int itemNumber = readD();
            long itemCount = readD();

            if (itemCount <= 0) {
                return;
            }

            orderList.add(itemNumber, (int) itemCount, pc);

            if (orderList.bugOk() != 0) {
                for (L1PcInstance player : L1World.getInstance().getAllPlayers()) {
                    if (player.isGm() || pc == player) {
                        player.sendPackets(new S_SystemMessage(pc.getName() + "님 상점 최대 구매 수량 초과 (" + itemCount + ")"));
                    }
                }
            }
        }

        int bugOk = orderList.bugOk();

        if (bugOk == 0) {
            shop.sellItems(pc, orderList);
            pc.saveInventory();
        }
    }
}
