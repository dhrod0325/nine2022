package ks.packets.serverpackets;

import ks.core.network.opcode.L1Opcodes;


public class S_BonusStats extends ServerBasePacket {
    public S_BonusStats(int i, int j) {
        buildPacket(i, j);
    }

    private void buildPacket(int i, int j) {
        writeC(L1Opcodes.S_OPCODE_SHOWHTML);
        writeD(i);
        writeS("RaiseAttr");
    }
}
