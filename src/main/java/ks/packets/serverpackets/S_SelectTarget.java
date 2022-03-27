package ks.packets.serverpackets;

import ks.core.network.opcode.L1Opcodes;

public class S_SelectTarget extends ServerBasePacket {
    public S_SelectTarget(int objectId) {
        writeC(L1Opcodes.S_OPCODE_SELECTTARGET);
        writeD(objectId);
        writeC(0x00);
        writeC(0x00);
        writeC(0x02);
    }

    public S_SelectTarget(int objectId, int type) {
        this(objectId, type, 0);
    }

    public S_SelectTarget(int objectId, int type, int type2) {
        writeC(L1Opcodes.S_OPCODE_SELECTTARGET);
        writeD(objectId);
        writeC(type);
        writeC(0);
        writeC(type2);
    }
}