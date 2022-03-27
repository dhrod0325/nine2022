package ks.packets.serverpackets;

import ks.core.network.opcode.L1Opcodes;

public class S_RedMessage extends ServerBasePacket {
    public S_RedMessage(int type, String msg1) {
        buildPacket(type, msg1);
    }

    private void buildPacket(int type, String msg1) {
        writeC(L1Opcodes.S_OPCODE_REDMESSAGE);
        writeH(type);
        if (msg1.length() <= 0) {
            writeC(0);
        } else {
            writeC(1);
            writeS(msg1);
        }
    }
}

