package ks.packets.serverpackets;

import ks.core.network.opcode.L1Opcodes;

public class S_Drawal extends ServerBasePacket {
    public S_Drawal(int objectId, int count) {
        writeC(L1Opcodes.S_OPCODE_DRAWAL);
        writeD(objectId);
        writeD(count);
    }
}
