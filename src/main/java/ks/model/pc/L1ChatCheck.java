package ks.model.pc;

import ks.constants.L1SkillIcon;
import ks.constants.L1SkillId;
import ks.packets.serverpackets.S_ServerMessage;
import ks.packets.serverpackets.S_SkillIconGFX;

public class L1ChatCheck {
    private final L1PcInstance pc;

    private int chatCount;
    private long oldChatTimeInMillis;

    public L1ChatCheck(L1PcInstance pc) {
        this.pc = pc;
    }

    public void checkChatInterval() {
        long nowChatTimeInMillis = System.currentTimeMillis();

        if (chatCount == 0) {
            chatCount++;
            oldChatTimeInMillis = nowChatTimeInMillis;
            return;
        }

        long chatInterval = nowChatTimeInMillis - oldChatTimeInMillis;

        if (chatInterval > 2000) {
            chatCount = 0;
            oldChatTimeInMillis = 0;
        } else {
            if (chatCount >= 4) {
                pc.getSkillEffectTimerSet().setSkillEffect(L1SkillId.STATUS_CHAT_PROHIBITED, 120 * 1000);
                pc.sendPackets(new S_SkillIconGFX(L1SkillIcon.채금, 120));
                pc.sendPackets(new S_ServerMessage(153));
                chatCount = 0;
                oldChatTimeInMillis = 0;
            }

            chatCount++;
        }
    }
}
