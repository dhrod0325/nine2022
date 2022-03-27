package ks.model.poison;

import ks.constants.L1ItemId;
import ks.constants.L1SkillId;
import ks.model.L1Character;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_ServerMessage;

public abstract class L1Poison {
    protected static boolean isNotValidTarget(L1Character cha) {
        if (cha == null) {
            return true;
        }

        if (cha.getPoison() != null) {
            return true;
        }

        if (!(cha instanceof L1PcInstance)) {
            return false;
        }

        L1PcInstance player = (L1PcInstance) cha;

        return player.getInventory().checkEquipped(L1ItemId.제니스의반지)
                || player.getInventory().checkEquipped(L1ItemId.바포메트의갑옷)
                || player.getInventory().checkEquipped(L1ItemId.안타라스의완력)
                || player.getInventory().checkEquipped(L1ItemId.안타라스의예지력)
                || player.getInventory().checkEquipped(L1ItemId.안타라스의인내력)
                || player.getInventory().checkEquipped(L1ItemId.안타라스의마력)
                || player.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.BIRTH_MAAN)
                || player.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.VENOM_RESIST);
    }

    protected static void sendMessageIfPlayer(L1Character cha, int msgId) {
        if (!(cha instanceof L1PcInstance)) {
            return;
        }

        L1PcInstance player = (L1PcInstance) cha;
        player.sendPackets(new S_ServerMessage(msgId));
    }

    public abstract int getEffectId();

    public abstract void cure();
}
