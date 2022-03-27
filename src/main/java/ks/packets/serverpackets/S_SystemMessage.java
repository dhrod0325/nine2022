package ks.packets.serverpackets;

import ks.core.network.opcode.L1Opcodes;

public class S_SystemMessage extends ServerBasePacket {
    public S_SystemMessage(String msg) {
        writeC(L1Opcodes.S_OPCODE_MSG);
        writeC(0x09);
        writeS(msg);
    }
}
