package ks.model.item.function.potion;

import ks.core.network.opcode.L1Opcodes;
import ks.model.Broadcaster;
import ks.model.L1Character;
import ks.model.L1Item;
import ks.model.instance.L1ItemInstance;
import ks.model.pc.L1PcInstance;
import ks.packets.clientpackets.ClientBasePacket;
import ks.packets.serverpackets.S_ChatPacket;
import ks.packets.serverpackets.S_SkillSound;

import static ks.constants.L1SkillId.DECAY_POTION;

public class CurePotion extends L1ItemInstance {
    public CurePotion(L1Item item) {
        super(item);
    }

    @Override
    public void clickItem(L1Character cha, ClientBasePacket packet) {
        if (cha instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) cha;
            L1ItemInstance useItem = pc.getInventory().findItemObjId(getId());

            if (pc.getSkillEffectTimerSet().hasSkillEffect(DECAY_POTION)) {
                pc.sendPackets(new S_ChatPacket(pc, "마력에 의해아무것도 마실수가 없습니다.", L1Opcodes.S_OPCODE_MSG, 20));
            } else {
                pc.cancelAbsoluteBarrier();
                pc.sendPackets(new S_SkillSound(pc.getId(), 192));
                Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 192));
                pc.curePoison();
                pc.getInventory().removeItem(useItem, 1);
            }
        }
    }
}

