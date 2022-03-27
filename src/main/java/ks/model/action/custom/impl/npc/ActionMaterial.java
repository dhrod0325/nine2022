package ks.model.action.custom.impl.npc;

import ks.model.L1Object;
import ks.model.action.custom.L1AbstractNpcAction;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_Message_YN;
import ks.packets.serverpackets.S_NpcChatPacket;
import ks.packets.serverpackets.S_ServerMessage;

public class ActionMaterial extends L1AbstractNpcAction {
    public ActionMaterial(String action, L1PcInstance pc, L1Object obj) {
        super(action, pc, obj);
    }

    @Override
    public void execute() {
        if (pc.getInventory().checkItem(701235, 1)) {
            if (pc.getExpRes() == 1) {
                pc.sendPackets(new S_Message_YN(2551, ""));
            } else {
                pc.sendPackets(new S_ServerMessage(739));
            }
        } else {
            pc.sendPackets(new S_NpcChatPacket(npc, "구호 증서가 부족합니다.", 0));
        }
    }
}
