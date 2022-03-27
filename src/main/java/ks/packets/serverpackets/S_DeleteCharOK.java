package ks.packets.serverpackets;

import ks.core.network.opcode.L1Opcodes;

public class S_DeleteCharOK extends ServerBasePacket {
    public static final int DELETE_CHAR_NOW = 0x05;

    public S_DeleteCharOK(int type) {
        writeC(L1Opcodes.S_OPCODE_DETELECHAROK);
        writeC(type);
    }

}
