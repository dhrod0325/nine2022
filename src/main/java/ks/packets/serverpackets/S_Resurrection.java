package ks.packets.serverpackets;

import ks.core.network.opcode.L1Opcodes;
import ks.model.pc.L1PcInstance;


public class S_Resurrection extends ServerBasePacket {
    public S_Resurrection(L1PcInstance target, L1PcInstance use, int type) {
        writeC(L1Opcodes.S_OPCODE_RESURRECTION);
        writeD(target.getId());
        writeC(type);
        writeD(use.getId());
        writeH(target.getClassId());
    }
}
