package ks.packets.serverpackets;

import ks.core.network.opcode.L1Opcodes;

public class S_RenewLetter extends ServerBasePacket {
    public S_RenewLetter(int type, int id) {
        buildPacket(type, id);
    }

    private void buildPacket(int type, int id) {
        writeC(L1Opcodes.S_OPCODE_LETTER);
        writeC(type);
        writeD(id);
        writeC(1);
    }
}