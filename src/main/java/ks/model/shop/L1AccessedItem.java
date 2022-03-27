package ks.model.shop;

import ks.model.L1World;
import ks.model.instance.L1ItemInstance;
import ks.system.dogFight.L1DogFight;
import ks.system.race.L1RaceManager;

public class L1AccessedItem {
    private final int targetId;

    private final int assessedPrice;

    L1AccessedItem(int targetId, int assessedPrice) {
        this.targetId = targetId;
        this.assessedPrice = buildPrice(assessedPrice);
    }

    public int getTargetId() {
        return targetId;
    }

    public int getAssessedPrice() {
        return assessedPrice;
    }

    private int buildPrice(int assessedPrice) {
        try {
            L1ItemInstance item = (L1ItemInstance) L1World.getInstance().findObject(targetId);

            if (item.getItemId() == L1RaceManager.SHOP_ITEM_ID) {
                return L1RaceManager.getInstance().buildPrice(targetId, assessedPrice);
            } else if (item.getItemId() == L1DogFight.SHOP_ITEM_ID) {
                return L1DogFight.getInstance().buildPrice(targetId, assessedPrice);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return assessedPrice;
    }
}
