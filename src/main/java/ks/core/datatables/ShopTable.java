package ks.core.datatables;

import ks.app.LineageAppContext;
import ks.app.aop.LogTime;
import ks.model.L1ShopItem;
import ks.model.shop.L1Shop;
import ks.util.common.SqlUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ShopTable {
    private final Map<Integer, L1Shop> allShops = new HashMap<>();

    public static ShopTable getInstance() {
        return LineageAppContext.getBean(ShopTable.class);
    }

    @LogTime
    public void load() {
        loadShops();
    }

    private List<Integer> npcIdList() {
        return SqlUtils.query("SELECT DISTINCT npc_id FROM shop", (rs, i) -> rs.getInt("npc_id"));
    }

    private void loadShops() {
        allShops.clear();

        List<Integer> npcIdList = npcIdList();

        for (int npcId : npcIdList) {
            List<L1ShopItem> sellingList = new ArrayList<>();
            List<L1ShopItem> purchasingList = new ArrayList<>();

            SqlUtils.query("SELECT * FROM shop WHERE npc_id=? ORDER BY order_id", (rs, i) -> {
                int itemId = rs.getInt("item_id");
                int sellingPrice = rs.getInt("selling_price");
                int purchasingPrice = rs.getInt("purchasing_price");
                int packCount = rs.getInt("pack_count");
                int enchant = rs.getInt("enchant");

                packCount = packCount == 0 ? 1 : packCount;

                if (sellingPrice >= 0) {
                    L1ShopItem item = new L1ShopItem(itemId, sellingPrice, packCount, enchant);
                    sellingList.add(item);
                }

                if (purchasingPrice >= 0) {
                    L1ShopItem item = new L1ShopItem(itemId, purchasingPrice, packCount, enchant);
                    purchasingList.add(item);
                }

                return null;
            }, npcId);

            L1Shop shop = new L1Shop(npcId, sellingList, purchasingList);
            allShops.put(npcId, shop);
        }
    }

    public L1Shop findShop(int npcId) {
        return allShops.get(npcId);
    }
}
