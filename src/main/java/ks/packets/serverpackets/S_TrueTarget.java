package ks.packets.serverpackets;

import ks.core.network.opcode.L1Opcodes;

public class S_TrueTarget extends ServerBasePacket {
    public S_TrueTarget(int targetId, int x, int y) {
        buildPacket(targetId, x, y);
    }

    private void buildPacket(int targetId, int x, int y) {
        writeC(L1Opcodes.S_OPCODE_TRUETARGET);
        writeD(targetId);
        writeH(x);
        writeH(y);
        writeS("");
    }
}
