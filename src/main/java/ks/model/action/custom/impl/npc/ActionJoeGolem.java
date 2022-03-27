package ks.model.action.custom.impl.npc;

import ks.constants.L1ItemId;
import ks.model.L1Object;
import ks.model.L1Teleport;
import ks.model.action.custom.L1AbstractNpcAction;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_NPCTalkReturn;

public class ActionJoeGolem extends L1AbstractNpcAction {
    public ActionJoeGolem(String action, L1PcInstance pc, L1Object obj) {
        super(action, pc, obj);
    }

    @Override
    public void execute() {
        if (action.equalsIgnoreCase("B")) {
            if (pc.getInventory().checkItem(L1ItemId.TIMECRACK_BROKENPIECE)) {
                pc.getInventory().consumeItem(L1ItemId.TIMECRACK_BROKENPIECE, 1);
                L1Teleport.teleport(pc, 33970, 33246, (short) 4, 4, true);
            } else {
                pc.sendPackets(new S_NPCTalkReturn(objId, "joegolem20"));
            }
        }
    }
}
