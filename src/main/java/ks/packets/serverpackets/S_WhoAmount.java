package ks.packets.serverpackets;

import ks.core.network.opcode.L1Opcodes;

public class S_WhoAmount extends ServerBasePacket {
    public S_WhoAmount(String amount) {
        writeC(L1Opcodes.S_OPCODE_SERVERMSG);
        writeH(0x0051);
        writeC(0x01);
        writeS(amount);
    }
}
