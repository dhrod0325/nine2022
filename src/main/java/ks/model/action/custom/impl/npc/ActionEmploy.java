package ks.model.action.custom.impl.npc;

import ks.model.L1Object;
import ks.model.action.custom.L1AbstractNpcAction;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_SoldierBuyList;

public class ActionEmploy extends L1AbstractNpcAction {
    public ActionEmploy(String action, L1PcInstance pc, L1Object obj) {
        super(action, pc, obj);
    }

    @Override
    public void execute() {
        int castleId = pc.getClan().getCastleId();
        pc.sendPackets(new S_SoldierBuyList(objId, castleId));
    }
}
