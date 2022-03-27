package ks.packets.serverpackets;

import ks.core.network.opcode.L1Opcodes;
import ks.model.pc.L1PcInstance;

public class S_OwnCharStatus2 extends ServerBasePacket {
    public S_OwnCharStatus2(L1PcInstance pc) {
        if (pc == null) {
            return;
        }

        writeC(L1Opcodes.S_OPCODE_OWNCHARSTATUS2);
        writeC(pc.getAbility().getTotalStr());
        writeC(pc.getAbility().getTotalInt());
        writeC(pc.getAbility().getTotalWis());
        writeC(pc.getAbility().getTotalDex());
        writeC(pc.getAbility().getTotalCon());
        writeC(pc.getAbility().getTotalCha());
        writeC(pc.getInventory().getWeight240());
    }
}
