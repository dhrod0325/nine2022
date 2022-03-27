package ks.packets.serverpackets;

import ks.core.network.opcode.L1Opcodes;

public class S_Trade extends ServerBasePacket {
    public S_Trade(String name) {
        writeC(L1Opcodes.S_OPCODE_TRADE);
        writeS(name);
    }
}
