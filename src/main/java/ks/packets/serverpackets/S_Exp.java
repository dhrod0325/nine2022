package ks.packets.serverpackets;

import ks.core.network.opcode.L1Opcodes;
import ks.model.pc.L1PcInstance;

public class S_Exp extends ServerBasePacket {
    public S_Exp(L1PcInstance pc) {
        writeC(L1Opcodes.S_OPCODE_EXP);
        writeC(pc.getLevel());
        writeD(pc.getExp());
    }
}
