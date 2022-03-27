package ks.model.action.custom.impl.npc;

import ks.model.L1Object;
import ks.model.L1Teleport;
import ks.model.action.custom.L1AbstractNpcAction;
import ks.model.pc.L1PcInstance;

public class ActionInn extends L1AbstractNpcAction {
    public ActionInn(String action, L1PcInstance pc, L1Object obj) {
        super(action, pc, obj);
    }

    @Override
    public void execute() {
        if (npcId == 70031) {
            L1Teleport.teleport(pc, 32744, 32803, (short) 18432, 0, true);
        }
    }
}
