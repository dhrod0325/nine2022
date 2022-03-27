package ks.model.action.custom.impl.npc;

import ks.model.L1Object;
import ks.model.action.custom.L1AbstractNpcAction;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_RetrieveList;
import ks.util.L1CommonUtils;

public class ActionRetrieve extends L1AbstractNpcAction {
    public ActionRetrieve(String action, L1PcInstance pc, L1Object obj) {
        super(action, pc, obj);
    }

    @Override
    public void execute() {
        if (pc.getLevel() >= 5) {
            if (L1CommonUtils.isTwoLogin(pc))
                return;

            pc.sendPackets(new S_RetrieveList(objId, pc));
        }
    }
}
