package ks.model.item.function;

import ks.constants.L1ItemId;
import ks.constants.L1PacketBoxType;
import ks.constants.L1SkillId;
import ks.model.L1Character;
import ks.model.L1Item;
import ks.model.instance.L1ItemInstance;
import ks.model.pc.L1PcInstance;
import ks.packets.clientpackets.ClientBasePacket;
import ks.packets.serverpackets.S_PacketBox;
import ks.packets.serverpackets.S_ServerMessage;
import ks.packets.serverpackets.S_SystemMessage;

public class DragonGem extends L1ItemInstance {
    public DragonGem(L1Item item) {
        super(item);
    }

    @Override
    public void clickItem(L1Character cha, ClientBasePacket packet) {
        if (cha instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) cha;

            L1ItemInstance useItem = pc.getInventory().findItemId(getItemId());
            int itemId = useItem.getItemId();

            if (pc.getLevel() < 49) {
                pc.sendPackets(new S_ServerMessage(318, "49"));
                return;
            }

            if (itemId == L1ItemId.DRAGON_DIAMOND) {
                if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.DRAGON_EMERALD_NO)) {
                    if (!pc.getAutoPotion().isAutoPotion()) {
                        pc.sendPackets(new S_SystemMessage("드래곤의 에메랄드 기운을받아 다른 보석을 사용할 수 없습니다."));
                    }
                    return;
                }

                if (pc.getAinHasad() < 1000000) {
                    pc.calAinHasad(1000000);
                    pc.sendPackets(new S_PacketBox(L1PacketBoxType.AINHASAD, pc.getAinHasad()));
                    pc.sendPackets(new S_SystemMessage("아인하사드의 축복이 100% 추가되었습니다."));
                    pc.getInventory().removeItem(useItem, 1);
                } else {
                    if (!pc.getAutoPotion().isAutoPotion()) {
                        pc.sendPackets(new S_SystemMessage("축복지수 100미만에서만 사용하실수 있습니다."));
                    }
                }
            } else if (itemId == L1ItemId.DRAGON_SAPHIRE) {
                if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.DRAGON_EMERALD_NO)) {
                    if (!pc.getAutoPotion().isAutoPotion()) {
                        pc.sendPackets(new S_SystemMessage("드래곤의 에메랄드 기운을받아 다른 보석을 사용할 수 없습니다."));
                    }

                    return;
                }
                if (pc.getAinHasad() < 1500000) {
                    pc.calAinHasad(500000);
                    pc.sendPackets(new S_PacketBox(L1PacketBoxType.AINHASAD, pc.getAinHasad()));
                    pc.sendPackets(new S_SystemMessage("아인하사드의 축복이 50% 추가되었습니다."));
                    pc.getInventory().removeItem(useItem, 1);
                } else {
                    if (!pc.getAutoPotion().isAutoPotion()) {
                        pc.sendPackets(new S_SystemMessage("축복지수 150미만에서만 사용하실수 있습니다."));
                    }
                }
            } else if (itemId == L1ItemId.DRAGON_RUBY) {
                if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.DRAGON_EMERALD_NO)) {
                    if (!pc.getAutoPotion().isAutoPotion())
                        pc.sendPackets(new S_SystemMessage("드래곤의 에메랄드 기운을받아 다른 보석을 사용할 수 없습니다."));
                    return;
                }
                if (pc.getAinHasad() < 1700000) {
                    pc.calAinHasad(300000);
                    pc.sendPackets(new S_PacketBox(L1PacketBoxType.AINHASAD, pc.getAinHasad()));
                    pc.sendPackets(new S_SystemMessage("아인하사드의 축복이 30% 추가되었습니다."));
                    pc.getInventory().removeItem(useItem, 1);
                } else {
                    if (!pc.getAutoPotion().isAutoPotion()) {
                        pc.sendPackets(new S_SystemMessage("축복지수 170미만에서만 사용하실수 있습니다."));
                    }
                }
            } else if (itemId == L1ItemId.DRAGON_EMERALD) {
                if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.DRAGON_EMERALD_NO)) {
                    if (!pc.getAutoPotion().isAutoPotion()) {
                        pc.sendPackets(new S_ServerMessage(2145));
                    }

                    return;
                } else if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.STATUS_DRAGON_EMERALD_YES)) {
                    if (!pc.getAutoPotion().isAutoPotion()) {
                        pc.sendPackets(new S_ServerMessage(2147));
                    }
                    return;
                }

                pc.getSkillEffectTimerSet().setSkillEffect(L1SkillId.STATUS_DRAGON_EMERALD_YES, 3 * 60 * 60 * 1000);
                pc.sendPackets(new S_PacketBox(L1PacketBoxType.AINHASAD, pc.getAinHasad()));
                pc.sendPackets(new S_PacketBox(L1PacketBoxType.EMERALD_EVA, 0x02, 10800));
                pc.sendPackets(new S_ServerMessage(2140));
                pc.getInventory().removeItem(useItem, 1);
            }
        }
    }
}
