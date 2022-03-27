package ks.model.action.custom.impl.npc;

import ks.constants.L1ItemId;
import ks.model.L1Object;
import ks.model.action.custom.L1AbstractNpcAction;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_HPUpdate;
import ks.packets.serverpackets.S_MPUpdate;
import ks.packets.serverpackets.S_ServerMessage;
import ks.packets.serverpackets.S_SkillSound;

public class ActionFullHeal extends L1AbstractNpcAction {
    public ActionFullHeal(String action, L1PcInstance pc, L1Object obj) {
        super(action, pc, obj);
    }

    @Override
    public void execute() {
        if (npcId == 70512 || npcId == 71037) {
            if (pc.getLevel() >= 13)
                return;

            healHp();

        } else if (npcId == 71030) {
            if (pc.getInventory().checkItem(L1ItemId.ADENA, 5)) {
                healHp();

                if (pc.isInParty()) {
                    pc.getParty().updateMiniHP(pc);
                }

                pc.getInventory().consumeItem(L1ItemId.ADENA, 5);
            } else {
                pc.sendPackets(new S_ServerMessage(337, "$4"));
            }
        }
    }

    private void healHp() {
        pc.setCurrentHp(pc.getMaxHp());
        pc.setCurrentMp(pc.getMaxMp());
        pc.sendPackets(new S_ServerMessage(77));
        pc.sendPackets(new S_SkillSound(pc.getId(), 830));
        pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
        pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
    }
}
