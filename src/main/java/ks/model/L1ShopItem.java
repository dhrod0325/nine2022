package ks.model;

import ks.core.datatables.item.ItemTable;
import ks.model.instance.L1NpcInstance;
import ks.system.dogFight.L1DogFight;
import ks.system.dogFight.L1DogFightInstance;
import ks.system.race.L1RaceManager;
import org.apache.commons.lang3.SerializationUtils;

public class L1ShopItem {
    private final int itemId;

    private L1Item item;
    private final int packCount;
    private final int enchant;
    private int price;
    private int count;

    public L1ShopItem(int itemId, int price, int packCount, int enchant) {
        this.itemId = itemId;
        this.item = ItemTable.getInstance().getTemplate(itemId);

        this.price = price;
        this.packCount = packCount;
        this.enchant = enchant;

        count = 1;
    }

    public int getItemId() {
        return itemId;
    }

    public L1Item getItem() {
        return item;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getPackCount() {
        return packCount;
    }

    public int getEnchant() {
        return enchant;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int i) {
        count = i;
    }

    public void setName(int num) {
        item = SerializationUtils.clone(item);

        String temp = null;

        if (itemId == L1RaceManager.SHOP_ITEM_ID) {
            L1NpcInstance runner = L1RaceManager.getInstance().getRunner(num).getNpc();
            int round = L1RaceManager.getInstance().getRound();
            int trueNum = runner.getNpcId() - L1RaceManager.FIRST_NPC_ID + 1;
            temp = runner.getNameId() + " " + round + "-" + trueNum;
        } else if (itemId == L1DogFight.SHOP_ITEM_ID) {
            L1DogFightInstance dog = L1DogFight.getInstance().getDog(num);
            int round = L1DogFight.getInstance().getRound();
            int trueNum = L1DogFight.getInstance().getDog(num).getNpcId() - L1DogFight.FIRST_NPC_ID + 1;
            temp = dog.getNameId() + " " + round + "-" + trueNum;
        }

        if (temp != null) {
            item.setName(temp);
            item.setNameId(temp);
        }
    }
}
