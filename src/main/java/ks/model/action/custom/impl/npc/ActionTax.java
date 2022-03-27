package ks.model.action.custom.impl.npc;

import ks.model.L1Object;
import ks.model.action.custom.L1AbstractNpcAction;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_TaxRate;

public class ActionTax extends L1AbstractNpcAction {
    public ActionTax(String action, L1PcInstance pc, L1Object obj) {
        super(action, pc, obj);
    }

    @Override
    public void execute() {
        if (pc.getId() != pc.getClan().getLeaderId() || pc.getClanRank() != 10 || !pc.isCrown()) {
            return;
        }

        pc.sendPackets(new S_TaxRate(pc.getId()));
    }
}
