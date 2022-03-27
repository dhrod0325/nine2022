package ks.model.action.custom.impl.npc;

import ks.model.L1Object;
import ks.model.action.custom.L1AbstractNpcAction;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_RetrieveElfList;
import ks.util.L1CommonUtils;

public class ActionRetrieveElven extends L1AbstractNpcAction {
    public ActionRetrieveElven(String action, L1PcInstance pc, L1Object obj) {
        super(action, pc, obj);
    }

    @Override
    public void execute() {
        if (pc.getLevel() >= 5 && pc.isElf()) {
            if (L1CommonUtils.isTwoLogin(pc))
                return;

            pc.sendPackets(new S_RetrieveElfList(objId, pc));
        }
    }
}
