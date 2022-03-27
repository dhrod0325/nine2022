package ks.model.action.custom.impl.npc;

import ks.constants.L1ItemId;
import ks.model.L1Object;
import ks.model.action.custom.L1AbstractNpcAction;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_Lawful;
import ks.packets.serverpackets.S_NPCTalkReturn;

public class ActionYuris extends L1AbstractNpcAction {
    public ActionYuris(String action, L1PcInstance pc, L1Object obj) {
        super(action, pc, obj);
    }

    @Override
    public void execute() {
        if (action.equalsIgnoreCase("0")) { // 1번
            yuris(pc, objId, 1, 3000);
        } else if (action.equalsIgnoreCase("1")) { // 3번
            yuris(pc, objId, 3, 9000);
        } else if (action.equalsIgnoreCase("2")) { // 5번
            yuris(pc, objId, 5, 15000);
        } else if (action.equalsIgnoreCase("3")) { // 10번
            yuris(pc, objId, 10, 30000);
        }
    }

    public void yuris(L1PcInstance pc, int objid, int count, int lawful) {
        if (pc.getInventory().checkItem(L1ItemId.REDEMPTION_BIBLE, count)) {
            pc.getInventory().consumeItem(L1ItemId.REDEMPTION_BIBLE, count);
            pc.addLawful(lawful);
            pc.sendPackets(new S_Lawful(pc.getId(), pc.getLawful()));
            pc.sendPackets(new S_NPCTalkReturn(objid, "yuris2"));
        } else {
            pc.sendPackets(new S_NPCTalkReturn(objid, "yuris3"));
        }
    }
}
