package ks.model.item.function.potion;

import ks.model.Broadcaster;
import ks.model.L1Character;
import ks.model.L1Item;
import ks.model.instance.L1ItemInstance;
import ks.model.pc.L1PcInstance;
import ks.packets.clientpackets.ClientBasePacket;
import ks.packets.serverpackets.S_ServerMessage;
import ks.packets.serverpackets.S_SkillIconBlessOfEva;
import ks.packets.serverpackets.S_SkillSound;

import static ks.constants.L1SkillId.DECAY_POTION;
import static ks.constants.L1SkillId.STATUS_UNDERWATER_BREATH;

@SuppressWarnings("serial")
public class BlessOfEvaPotion extends L1ItemInstance {

    public BlessOfEvaPotion(L1Item item) {
        super(item);
    }

    @Override
    public void clickItem(L1Character cha, ClientBasePacket packet) {
        if (cha instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) cha;

            useBlessOfEva(pc);
        }
    }

    private void useBlessOfEva(L1PcInstance pc) {
        L1ItemInstance useItem = pc.getInventory().getItem(this.getId());
        int itemId = this.getItemId();

        if (pc.getSkillEffectTimerSet().hasSkillEffect(DECAY_POTION)) {
            pc.sendPackets(new S_ServerMessage(698));
            return;
        }

        pc.cancelAbsoluteBarrier();

        int time;

        if (itemId == 40032) { // 에바의 축복
            time = 1800;
        } else if (itemId == 50019) { // 복지호흡물약
            time = 7200;
        } else if (itemId == 40041) { // mermaid의 비늘
            time = 300;
        } else if (itemId == 41344) { // 물의 정수
            time = 2100;
        } else {
            return;
        }

        if (pc.getSkillEffectTimerSet().hasSkillEffect(STATUS_UNDERWATER_BREATH)) {
            int timeSec = pc.getSkillEffectTimerSet().getSkillEffectTimeSec(STATUS_UNDERWATER_BREATH);
            time += timeSec;
            if (time > 3600) {
                time = 3600;
            }
        }

        pc.sendPackets(new S_SkillIconBlessOfEva(pc.getId(), time));
        pc.sendPackets(new S_SkillSound(pc.getId(), 190));
        Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 190));
        pc.getSkillEffectTimerSet().setSkillEffect(STATUS_UNDERWATER_BREATH, time * 1000);
        pc.getInventory().removeItem(useItem, 1);
    }
}
