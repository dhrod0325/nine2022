package ks.packets.serverpackets;

import ks.core.network.opcode.L1Opcodes;
import ks.model.pc.L1PcInstance;

public class S_ShortOfMaterial extends ServerBasePacket {
    public S_ShortOfMaterial(int type, L1PcInstance pc) {
        writeC(L1Opcodes.S_OPCODE_SHORTOFMATERIAL);
        writeC(type);
        writeC(0);
        writeC(0);
        writeC(0);
    }
}
