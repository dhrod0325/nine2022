package ks.system.userShop;

import ks.app.LineageAppContext;
import ks.app.aop.LogTime;
import ks.core.datatables.item.ItemTable;
import ks.core.datatables.pc.CharacterTable;
import ks.model.L1PrivateShopBuy;
import ks.model.L1PrivateShopSell;
import ks.model.instance.L1ItemInstance;
import ks.model.pc.L1PcInstance;
import ks.system.userShop.table.L1UserShop;
import ks.system.userShop.table.L1UserShopTable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class L1UserShopManager {
    private final Logger logger = LogManager.getLogger();

    private final List<L1UserShopNpcInstance> autoShopNpcInstanceList = new ArrayList<>();

    public static L1UserShopManager getInstance() {
        return LineageAppContext.getBean(L1UserShopManager.class);
    }

    public void register(L1UserShopNpcInstance autoShopNpcInstance) {
        autoShopNpcInstanceList.add(autoShopNpcInstance);
    }

    public void unRegister(L1UserShopNpcInstance autoShopNpcInstance) {
        autoShopNpcInstanceList.remove(autoShopNpcInstance);
    }

    public L1UserShopNpcInstance find(int masterId) {
        for (L1UserShopNpcInstance o : autoShopNpcInstanceList) {
            if (o.getMasterObjId() == masterId) {
                return o;
            }
        }

        return null;
    }

    public L1UserShopNpcInstance find(L1PcInstance pc) {
        return find(pc.getId());
    }

    @LogTime
    public void load() {
        L1UserShopTable dao = L1UserShopTable.getInstance();
        List<Map<String, Object>> locList = dao.selectUserShopLocList();

        for (Map<String, Object> loc : locList) {
            try {
                if (loc.get("charName") == null) {
                    continue;
                }

                int charId = Integer.parseInt(loc.get("charId").toString());

                L1PcInstance pc = CharacterTable.getInstance().restoreCharacter(loc.get("charName").toString());

                L1UserShopNpcInstance autoShopNpcInstance = new L1UserShopNpcInstance(pc);
                autoShopNpcInstance.setX((Integer) loc.get("locX"));
                autoShopNpcInstance.setY((Integer) loc.get("locY"));
                autoShopNpcInstance.setMap(Short.parseShort(loc.get("locMap").toString()));

                if (loc.get("chat") != null) {
                    String chat = String.valueOf(loc.get("chat"));
                    autoShopNpcInstance.setChat(chat.getBytes());
                }

                Integer adena = (Integer) loc.get("adena");

                if (adena != null) {
                    L1ItemInstance item = ItemTable.getInstance().createItem(40308);
                    item.setCount(adena);
                    autoShopNpcInstance.getInventory().storeItem(item);
                }

                List<L1UserShop> userSellList = dao.selectUserShopList(charId, "sell");

                for (L1UserShop shop : userSellList) {
                    try {
                        L1ItemInstance item = createItem(shop);
                        item.setCount(shop.getTotalCount() - shop.getCount());

                        L1PrivateShopSell sell = new L1PrivateShopSell();
                        sell.setItem(item);
                        sell.setItemObjectId(item.getId());
                        sell.setSellCount(shop.getCount());
                        sell.setSellPrice(shop.getPrice());
                        sell.setSellTotalCount(shop.getTotalCount());

                        shop.setItemObjectId(item.getId());
                        shop.setItemName(item.getName());

                        autoShopNpcInstance.getSellList().add(sell);
                        autoShopNpcInstance.getInventory().storeItem(item);

                        dao.saveShopItem(shop);
                    } catch (Exception e) {
                        logger.error("오류", e);
                    }
                }

                List<L1UserShop> userBuyList = dao.selectUserShopList(charId, "buy");

                for (L1UserShop shop : userBuyList) {
                    try {
                        L1ItemInstance item = createItem(shop);

                        L1PrivateShopBuy buy = new L1PrivateShopBuy();
                        buy.setItem(item);
                        buy.setItemObjectId(item.getId());
                        buy.setBuyCount(shop.getCount());
                        buy.setBuyPrice(shop.getPrice());
                        buy.setBuyTotalCount(shop.getTotalCount());

                        shop.setItemObjectId(item.getId());
                        shop.setItemName(item.getName());

                        autoShopNpcInstance.getBuyList().add(buy);

                        dao.saveShopItem(shop);
                    } catch (Exception e) {
                        logger.error("오류", e);
                    }
                }

                List<L1UserShop> userBuyItemList = dao.selectUserShopBuyList(charId);

                for (L1UserShop shop : userBuyItemList) {
                    try {
                        L1ItemInstance item = createItem(shop);

                        L1PrivateShopBuy buy = new L1PrivateShopBuy();
                        buy.setItem(item);
                        buy.setItemObjectId(item.getId());
                        buy.setBuyCount(shop.getCount());
                        buy.setBuyPrice(shop.getPrice());
                        buy.setBuyTotalCount(shop.getTotalCount());

                        shop.setItemObjectId(item.getId());

                        autoShopNpcInstance.getInventory().storeTradeItem(item);
                    } catch (Exception e) {
                        logger.error("오류", e);
                    }
                }

                autoShopNpcInstance.visibleWorld();
            } catch (Exception e) {
                logger.error("오류", e);
            }
        }
    }

    private L1ItemInstance createItem(L1UserShop shop) {
        L1ItemInstance item = ItemTable.getInstance().createItem(shop.getItemId());
        item.setId(shop.getItemObjectId());
        item.setEnchantLevel(shop.getEnchantLvl());
        item.setBless(shop.getBless());
        item.setDurability(shop.getDurability());
        item.setAttrEnchantLevel(shop.getAttrLvl());
        item.setIdentified(true);

        return item;
    }
}
