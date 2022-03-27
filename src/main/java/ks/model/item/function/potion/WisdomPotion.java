package ks.model.item.function.potion;

import ks.constants.L1ItemId;
import ks.core.network.opcode.L1Opcodes;
import ks.model.Broadcaster;
import ks.model.L1Character;
import ks.model.L1Item;
import ks.model.instance.L1ItemInstance;
import ks.model.pc.L1PcInstance;
import ks.packets.clientpackets.ClientBasePacket;
import ks.packets.serverpackets.S_ChatPacket;
import ks.packets.serverpackets.S_SkillIconWisdomPotion;
import ks.packets.serverpackets.S_SkillSound;

import static ks.constants.L1SkillId.DECAY_POTION;
import static ks.constants.L1SkillId.STATUS_WISDOM_POTION;

public class WisdomPotion extends L1ItemInstance {
    public WisdomPotion(L1Item item) {
        super(item);
    }

    @Override
    public void clickItem(L1Character cha, ClientBasePacket packet) {
        if (cha instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) cha;


            if (pc.isWizard()) {
                useWisdomPotion(pc);
            } else {
                pc.sendPackets(new S_ChatPacket(pc, "아무것도 일어나지않았습니다.", L1Opcodes.S_OPCODE_MSG, 20));
            }
        }
    }

    private void useWisdomPotion(L1PcInstance pc) {
        L1ItemInstance useItem = pc.getInventory().getItem(this.getId());
        int itemId = this.getItemId();

        if (pc.getSkillEffectTimerSet().hasSkillEffect(DECAY_POTION)) { // 디케이포션상태
            pc.sendPackets(new S_ChatPacket(pc, "마력에 의해 아무것도 마실 수가없습니다.", L1Opcodes.S_OPCODE_MSG, 20));
            return;
        }

        // 아브소르트바리아의 해제
        pc.cancelAbsoluteBarrier();

        int time = 0; // 시간은 4의 배수로 하는 것
        if (itemId == L1ItemId.POTION_OF_EMOTION_WISDOM) { // 위즈 댐 일부
            time = 300;
        } else if (itemId == L1ItemId.B_POTION_OF_EMOTION_WISDOM) {
            time = 360;
        } else if (itemId == L1ItemId.B_POTION_OF_EMOTION_WISDOM2) {
            time = 600;
        }

        if (!pc.getSkillEffectTimerSet().hasSkillEffect(STATUS_WISDOM_POTION)) {
            pc.getAbility().addSp(2);
        }

        pc.sendPackets(new S_SkillIconWisdomPotion(time));
        pc.sendPackets(new S_SkillSound(pc.getId(), 750));
        Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 750));
        pc.getSkillEffectTimerSet().setSkillEffect(STATUS_WISDOM_POTION, time * 1000);
        pc.getInventory().removeItem(useItem, 1);
    }
}
