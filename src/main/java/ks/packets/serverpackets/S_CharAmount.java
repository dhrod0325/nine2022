package ks.packets.serverpackets;

import ks.core.network.opcode.L1Opcodes;

public class S_CharAmount extends ServerBasePacket {
    public S_CharAmount(int value, int i) {
        buildPacket(value, i);
    }

    private void buildPacket(int value, int slot) {
        writeC(L1Opcodes.S_OPCODE_CHARAMOUNT);
        writeC(value);
        writeC(slot);
    }
}
