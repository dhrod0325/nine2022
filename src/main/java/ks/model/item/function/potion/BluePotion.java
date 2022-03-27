package ks.model.item.function.potion;

import ks.constants.L1SkillIcon;
import ks.model.Broadcaster;
import ks.model.L1Character;
import ks.model.L1Item;
import ks.model.instance.L1ItemInstance;
import ks.model.pc.L1PcInstance;
import ks.packets.clientpackets.ClientBasePacket;
import ks.packets.serverpackets.S_ServerMessage;
import ks.packets.serverpackets.S_SkillIconGFX;
import ks.packets.serverpackets.S_SkillSound;

import static ks.constants.L1SkillId.*;

public class BluePotion extends L1ItemInstance {

    public BluePotion(L1Item item) {
        super(item);
    }

    @Override
    public void clickItem(L1Character cha, ClientBasePacket packet) {
        if (cha instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) cha;
            useBluePotion(pc);
        }
    }

    private void useBluePotion(L1PcInstance pc) {
        int itemId = getItemId();

        L1ItemInstance useItem = pc.getInventory().getItem(this.getId());

        if (pc.getSkillEffectTimerSet().hasSkillEffect(DECAY_POTION)) {
            pc.sendPackets(new S_ServerMessage(698));
            return;
        }

        pc.cancelAbsoluteBarrier();

        int time;

        if (itemId == 40015 || itemId == 40736) { // 블루 일부, 지혜의 코인
            time = 600;
        } else if (itemId == 140015) { // 축복된 블루 일부
            time = 700;
        } else if (itemId == 404082) { // 픽시의 마나 포션
            time = 700;
        } else if (itemId == 50017) { // 복지마나물약
            time = 2400;
        } else if (itemId == 41142) { // 블루 일부, 지혜의 코인
            time = 300;
        } else {
            return;
        }

        pc.sendPackets(new S_SkillIconGFX(L1SkillIcon.파란물약, time));
        pc.sendPackets(new S_SkillSound(pc.getId(), 190));

        Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 190));

        if (itemId == 41142) {
            pc.getSkillEffectTimerSet().setSkillEffect(STATUS_BLUE_POTION2, time * 1000);
        } else {
            pc.getSkillEffectTimerSet().setSkillEffect(STATUS_BLUE_POTION, time * 1000);
        }

        pc.sendPackets(new S_ServerMessage(1007));
        pc.getInventory().removeItem(useItem, 1);
    }
}
