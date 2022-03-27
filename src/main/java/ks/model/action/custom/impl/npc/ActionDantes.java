package ks.model.action.custom.impl.npc;

import ks.model.L1Object;
import ks.model.L1Teleport;
import ks.model.action.custom.L1AbstractNpcAction;
import ks.model.pc.L1PcInstance;

public class ActionDantes extends L1AbstractNpcAction {
    public ActionDantes(String action, L1PcInstance pc, L1Object obj) {
        super(action, pc, obj);
    }

    @Override
    public void execute() {
        if (action.equalsIgnoreCase("a")) {
            if (pc.getInventory().checkItem(41028)) {
                L1Teleport.teleport(pc, 32648, 32921, (short) 535, 5, true);
                pc.getInventory().consumeItem(41028, 1);
            }
        }
    }
}
