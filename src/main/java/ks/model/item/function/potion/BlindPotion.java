package ks.model.item.function.potion;

import ks.model.L1Character;
import ks.model.L1Item;
import ks.model.instance.L1ItemInstance;
import ks.model.pc.L1PcInstance;
import ks.packets.clientpackets.ClientBasePacket;
import ks.packets.serverpackets.S_CurseBlind;
import ks.packets.serverpackets.S_ServerMessage;

import static ks.constants.L1SkillId.*;

public class BlindPotion extends L1ItemInstance {
    public BlindPotion(L1Item item) {
        super(item);
    }

    @Override
    public void clickItem(L1Character cha, ClientBasePacket packet) {
        if (cha instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) cha;

            useBlindPotion(pc);

        }
    }

    private void useBlindPotion(L1PcInstance pc) {
        L1ItemInstance useItem = pc.getInventory().getItem(this.getId());

        if (pc.getSkillEffectTimerSet().hasSkillEffect(DECAY_POTION)) {
            pc.sendPackets(new S_ServerMessage(698));
            return;
        }

        pc.cancelAbsoluteBarrier();

        int time = 450;

        if (pc.getSkillEffectTimerSet().hasSkillEffect(CURSE_BLIND)) {
            pc.getSkillEffectTimerSet().killSkillEffectTimer(CURSE_BLIND);
        } else if (pc.getSkillEffectTimerSet().hasSkillEffect(DARKNESS)) {
            pc.getSkillEffectTimerSet().killSkillEffectTimer(DARKNESS);
        }

        if (pc.getSkillEffectTimerSet().hasSkillEffect(STATUS_FLOATING_EYE)) {
            pc.sendPackets(new S_CurseBlind(2));
        } else {
            pc.sendPackets(new S_CurseBlind(1));
        }

        pc.getSkillEffectTimerSet().setSkillEffect(CURSE_BLIND, time * 1000);
        pc.getInventory().removeItem(useItem, 1);
    }
}
