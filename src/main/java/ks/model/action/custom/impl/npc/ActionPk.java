package ks.model.action.custom.impl.npc;

import ks.constants.L1ItemId;
import ks.core.network.opcode.L1Opcodes;
import ks.model.L1Object;
import ks.model.action.custom.L1AbstractNpcAction;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_ChatPacket;
import ks.packets.serverpackets.S_ServerMessage;

public class ActionPk extends L1AbstractNpcAction {
    public ActionPk(String action, L1PcInstance pc, L1Object obj) {
        super(action, pc, obj);
    }

    @Override
    public void execute() {
        if (pc.getLawful() < 30000) {
            pc.sendPackets(new S_ServerMessage(559));
        } else if (pc.getPkCount() < 5) {
            pc.sendPackets(new S_ServerMessage(560));
        } else {
            if (pc.getInventory().consumeItem(L1ItemId.ADENA, 700000)) {
                pc.setPkCount(pc.getPkCount() - 5);
                pc.sendPackets(new S_ServerMessage(561, String.valueOf(pc.getPkCount())));
            } else {
                pc.sendPackets(new S_ChatPacket(pc, "아데나가 충분치않습니다.", L1Opcodes.S_OPCODE_MSG, 20));
            }
        }
    }
}
