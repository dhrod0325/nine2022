package ks.model.item.function;

import ks.constants.L1ItemId;
import ks.constants.L1PacketBoxType;
import ks.model.L1Character;
import ks.model.L1Item;
import ks.model.cooking.L1Cooking;
import ks.model.instance.L1ItemInstance;
import ks.model.pc.L1PcInstance;
import ks.packets.clientpackets.ClientBasePacket;
import ks.packets.serverpackets.S_CurseBlind;
import ks.packets.serverpackets.S_PacketBox;
import ks.packets.serverpackets.S_ServerMessage;

import static ks.constants.L1SkillId.*;

public class Food extends L1ItemInstance {

    public Food(L1Item item) {
        super(item);
    }

    @Override
    public void clickItem(L1Character cha, ClientBasePacket packet) {
        if (cha instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) cha;
            L1ItemInstance useItem = pc.getInventory().getItem(this.getId());
            int itemId = useItem.getItemId();
            switch (itemId) {
                case 40056:
                case 40057:
                case 40059:
                case 40060:
                case 40061:
                case 40062:
                case 40063:
                case 40064:
                case 40065:
                case 40069:
                case 40072:
                case 40073:
                case 41266:
                case 41267:
                case 41274:
                case 41275:
                case 41276:
                case 41296:
                case 41297:
                case 41252:
                case 49040:
                case 49041:
                case 49042:
                case 49043:
                case 49044:
                case 49045:
                case 49046:
                case 49047:
                case 140061:
                case 140062:
                case 140065:
                case 140069:
                case 140072:
                case 436000:
                    pc.getInventory().removeItem(useItem, 1);
                    if (pc.getFood() < 225) {
                        pc.setFood(pc.getFood() + useItem.getItem().getFoodVolume() / 10);
                        if (pc.getFood() > 225) {
                            pc.setFood(225);
                        }

                        pc.sendPackets(new S_PacketBox(L1PacketBoxType.FOOD, pc.getFood()));
                    } else if (pc.getFood() > 225) {
                        pc.setFood(225);
                    }

                    if (itemId == 40057) {
                        pc.getSkillEffectTimerSet().setSkillEffect(STATUS_FLOATING_EYE, 0);
                        if (pc.getSkillEffectTimerSet().hasSkillEffect(CURSE_BLIND) || pc.getSkillEffectTimerSet().hasSkillEffect(DARKNESS)) {
                            pc.sendPackets(new S_CurseBlind(2));
                        }

                        pc.sendPackets(new S_ServerMessage(152));
                    }

                    pc.sendPackets(new S_ServerMessage(76, useItem.getItem().getNameId()));

                    break;
            }

            if ((itemId >= 41277 && itemId <= 41292)
                    || (itemId >= 49049 && itemId <= 49064)
                    || (itemId >= L1ItemId.NORMAL_COOKFOOD_3RD_START && itemId <= L1ItemId.SPECIAL_COOKFOOD_3RD_END)
                    || (itemId >= 9800 && itemId <= 9803)
            ) {
                L1Cooking.useCookingItem(pc, useItem);
            }
        }
    }
}