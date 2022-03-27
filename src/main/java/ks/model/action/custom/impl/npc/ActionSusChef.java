package ks.model.action.custom.impl.npc;

import ks.model.L1Object;
import ks.model.action.custom.L1AbstractNpcAction;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_NPCTalkReturn;
import ks.packets.serverpackets.S_NpcChatPacket;
import ks.packets.serverpackets.S_SystemMessage;
import ks.util.L1CommonUtils;

public class ActionSusChef extends L1AbstractNpcAction {
    public ActionSusChef(String action, L1PcInstance pc, L1Object obj) {
        super(action, pc, obj);
    }

    @Override
    public void execute() {
        String html = null;

        if (action.equalsIgnoreCase("0")) {
            if (pc.getInventory().checkItem(41159, 45)) {
                pc.getInventory().consumeItem(41159, 45);
                pc.getInventory().storeItem(437019, 1);
                pc.sendPackets(new S_SystemMessage("포춘쿠키를 얻었습니다."));
            } else {
                pc.sendPackets(new S_NpcChatPacket(npc, "픽시의 깃털이 부족합니다.", 0));
                html = "suschef5";
            }
        }

        if (action.equalsIgnoreCase("1")) {
            if (pc.getInventory().checkItem(437020, 1)) {
                pc.getInventory().consumeItem(437020, 1);
                L1CommonUtils.statusBuff(pc, npc);
                html = "suschef2";
            } else if (pc.getInventory().checkItem(41159, 45)) {
                pc.getInventory().consumeItem(41159, 45);
                L1CommonUtils.statusBuff(pc, npc);
                html = "suschef2";
            } else {
                html = "suschef4";
                pc.sendPackets(new S_NpcChatPacket(npc, "운세쪽지 또는 깃털(45)개가 필요합니다", 0));
            }
        }

        if (html != null) {
            pc.sendPackets(new S_NPCTalkReturn(objId, html));
        }
    }
}
