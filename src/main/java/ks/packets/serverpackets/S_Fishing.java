package ks.packets.serverpackets;

import ks.core.network.opcode.L1Opcodes;

public class S_Fishing extends ServerBasePacket {
    public S_Fishing(int objectId, int motionNum, int x, int y) {
        buildPacket(objectId, motionNum, x, y);
    }

    private void buildPacket(int objectId, int motionNum, int x, int y) {
        writeC(L1Opcodes.S_OPCODE_DOACTIONGFX);
        writeD(objectId);
        writeC(motionNum);
        writeH(x);
        writeH(y);
        writeD(0);
        writeH(0);
    }
}
