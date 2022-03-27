package ks.model.action.custom.impl.npc;

import ks.model.L1Object;
import ks.model.action.custom.L1AbstractNpcAction;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_Message_YN;
import ks.packets.serverpackets.S_NPCTalkReturn;
import ks.packets.serverpackets.S_ServerMessage;
import ks.util.L1CommonUtils;

public class ActionExp extends L1AbstractNpcAction {
    public ActionExp(String action, L1PcInstance pc, L1Object obj) {
        super(action, pc, obj);
    }

    @Override
    public void execute() {
        String html = restoreExp(pc);

        pc.sendPackets(new S_NPCTalkReturn(objId, html));
    }

    public String restoreExp(L1PcInstance pc) {
        if (pc.getExpRes() == 1) {
            if (L1CommonUtils.isNotExpRestoreAble(pc)) {
                return null;
            }

            int cost;
            int level = pc.getLevel();
            int lawful = pc.getLawful();

            if (level < 45) {
                cost = level * level * 100;
            } else {
                cost = level * level * 200;
            }

            if (lawful >= 0) {
                cost = (cost / 2);
            }

            cost *= 2;

            pc.sendPackets(new S_Message_YN(738, String.valueOf(cost)));
        } else {
            pc.sendPackets(new S_ServerMessage(739));

            return "";
        }

        return null;
    }
}
