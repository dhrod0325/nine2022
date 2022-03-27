package ks.model.action.custom.impl.npc;

import ks.model.L1Object;
import ks.model.action.custom.L1AbstractNpcAction;
import ks.model.pc.L1PcInstance;
import ks.system.auction.S_AuctionBoardRead;

public class ActionSelect extends L1AbstractNpcAction {
    public ActionSelect(String action, L1PcInstance pc, L1Object obj, String param) {
        super(action, pc, obj, param);
    }

    @Override
    public void execute() {
        pc.sendPackets(new S_AuctionBoardRead(objId, param));
    }
}
