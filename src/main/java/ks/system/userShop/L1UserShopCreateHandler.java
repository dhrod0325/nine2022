package ks.system.userShop;

import ks.model.L1Object;
import ks.model.pc.L1PcInstance;
import ks.packets.clientpackets.C_ShopAndWarehouse;

import java.util.HashMap;
import java.util.Map;

public class L1UserShopCreateHandler {
    private static final L1UserShopCreateHandler instance = new L1UserShopCreateHandler();
    private final Map<Integer, L1UserShopHandleMessenger> messengerMap = new HashMap<>();

    public static L1UserShopCreateHandler getInstance() {
        return instance;
    }

    public void startSell(L1PcInstance pc) {
        L1UserShopHandleMessenger messenger = find(pc);
        L1UserShopNpcInstance shop;

        if (messenger == null) {
            shop = new L1UserShopNpcInstance(pc);
        } else {
            shop = messenger.getShopInstance();
        }

        shop.getUserShopSell().step1();
    }

    public void startBuy(L1PcInstance pc, String itemName, int enchant, int bless, int attrLevel) {
        L1UserShopHandleMessenger messenger = find(pc);
        L1UserShopNpcInstance shop;

        if (messenger == null) {
            shop = new L1UserShopNpcInstance(pc);
        } else {
            shop = messenger.getShopInstance();
        }

        shop.getUserShopBuy().step1(itemName, enchant, bless, attrLevel);

    }

    public boolean handle(int handleId, int size, C_ShopAndWarehouse packet) {
        L1UserShopHandleMessenger messenger = find(handleId);

        if (messenger != null) {
            int type = messenger.getType();

            L1UserShopNpcInstance shopNpcInstance = messenger.getShopInstance();

            if (type == 0) {
                if (messenger.getStep() == 0) {
                    shopNpcInstance.getUserShopSell().step2(messenger, size, packet);
                } else if (messenger.getStep() == 1) {
                    shopNpcInstance.getUserShopSell().step3(messenger, size, packet);
                }
            } else if (type == 1) {
                if (messenger.getStep() == 0) {
                    shopNpcInstance.getUserShopBuy().step2(messenger, size, packet);
                } else if (messenger.getStep() == 1) {
                    shopNpcInstance.getUserShopBuy().step3(messenger, size, packet);
                }
            }

            return true;
        }

        return false;
    }

    public boolean processTalk(L1PcInstance pc, L1Object findObject, int size, int resultType, C_ShopAndWarehouse packet) {
        if (findObject instanceof L1UserShopNpcInstance) {
            L1UserShopNpcInstance shopNpcInstance = (L1UserShopNpcInstance) findObject;

            if (resultType == 0) {
                shopNpcInstance.getUserShopSell().process(pc, findObject, size, packet);
            } else {
                shopNpcInstance.getUserShopBuy().process(pc, findObject, size, packet);
            }

            return true;
        }

        return false;
    }

    public void unRegister(int objectId) {
        messengerMap.remove(objectId);
    }

    public void unRegister(L1UserShopHandleMessenger messenger) {
        if (messenger != null)
            unRegister(messenger.getHandleId());
    }

    public void unRegister(L1UserShopNpcInstance shop) {
        unRegister(find(shop));
    }

    public L1UserShopHandleMessenger find(L1UserShopNpcInstance shop) {
        for (L1UserShopHandleMessenger messenger : messengerMap.values()) {
            if (shop.equals(messenger.getShopInstance())) {
                return messenger;
            }
        }

        return null;
    }

    public L1UserShopHandleMessenger find(L1PcInstance pc) {
        for (L1UserShopHandleMessenger messenger : messengerMap.values()) {
            if (pc.getId() == messenger.getShopInstance().getMasterObjId()) {
                return messenger;
            }
        }

        return null;
    }

    public L1UserShopHandleMessenger find(int handleId) {
        for (L1UserShopHandleMessenger messenger : messengerMap.values()) {
            if (handleId == messenger.getHandleId()) {
                return messenger;
            }
        }

        return null;
    }

    public void register(L1UserShopHandleMessenger model) {
        messengerMap.put(model.getHandleId(), model);
    }
}
