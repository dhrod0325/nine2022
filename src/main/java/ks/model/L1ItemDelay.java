package ks.model;

import ks.model.instance.L1ItemInstance;

public class L1ItemDelay {
    public static void onItemUse(L1Character character, L1ItemInstance item) {
        int delayId = 0;
        int delayTime = 0;

        if (item.getItem().getType2() == 0) {
            delayId = item.getItem().getDelayId();
            delayTime = item.getItem().getDelayTime();
        }

        ItemDelayTimer timer = new ItemDelayTimer(character, delayTime);
        character.addItemDelay(delayId, timer);
    }

    public static boolean hasItemDelay(L1Character character, L1ItemInstance item) {
        if (item.getItem().getType2() == 0) {
            int delayId = item.getItem().getDelayId();

            if (delayId != 0) {
                return character.hasItemDelay(delayId);
            }
        }

        return false;
    }
}
