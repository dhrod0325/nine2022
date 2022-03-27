package ks.model.cooking;

import ks.constants.L1ItemId;
import ks.model.instance.L1ItemInstance;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_EffectLocation;
import ks.packets.serverpackets.S_ServerMessage;
import ks.packets.serverpackets.S_SystemMessage;

public class L1Cooking {
    public static void useCookingItem(L1PcInstance pc, L1ItemInstance item) {
        int itemId = item.getItemId();

        boolean eatItemCheck = L1CookingUtils.isEatItem(itemId);

        if (eatItemCheck) {
            if (pc.getFood() != 225) {
                pc.sendPackets(new S_SystemMessage("해당 요리는 포만감 100%에서만 먹을수 있습니다."));
                return;
            }

            int dessertId = pc.getDessertId();

            if (dessertId != 0) {
                pc.getSkillEffectTimerSet().removeSkillEffect(dessertId);
            }
        }

        int cookingId = L1CookingUtils.getCookingIdByItemId(itemId);
        int time = 900;

        if (itemId >= 41277 && itemId <= 41283
                || itemId >= 49049 && itemId <= 49056
                || itemId >= L1ItemId.NORMAL_COOKFOOD_3RD_START && itemId <= L1ItemId.COOKFOOD_DEEP_SEA_FISH_STEW
                || itemId >= 41285 && itemId <= 41291
                || itemId >= 49057 && itemId <= 49064
                || itemId >= 9800 && itemId <= 9802
                || itemId >= L1ItemId.SPECIAL_COOKFOOD_3RD_START && itemId <= L1ItemId.SCOOKFOOD_DEEP_SEA_FISH_STEW) {
            int cid = pc.getCookingId();

            if (cid != 0) {
                pc.getSkillEffectTimerSet().removeSkillEffect(cid);
            }
        }

        L1CookingUtils.startCookingBuff(pc, cookingId, time);

        pc.sendPackets(new S_EffectLocation(pc.getX(), pc.getY(), 6392));
        pc.sendPackets(new S_ServerMessage(76, item.getNumberedName(1)));
        pc.getInventory().removeItem(item, 1);
    }
}