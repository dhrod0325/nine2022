package ks.packets.serverpackets;

import ks.core.network.opcode.L1Opcodes;
import ks.model.pc.L1PcInstance;

public class S_Unknown1 extends ServerBasePacket {
    public S_Unknown1(L1PcInstance pc) {
        writeC(L1Opcodes.S_OPCODE_UNKNOWN1);
        writeC(0x03);
        if (pc.getClanId() > 0) {
            writeD(pc.getClanId());
        } else {
            writeC(0x53);
            writeC(0x01);
            writeC(0x00);
            writeC(0x8b);
        }
        writeC(0x9c);
        writeC(0x1f);
    }
}