package ks.packets.serverpackets;

import ks.core.network.opcode.L1Opcodes;

public class S_PinkName extends ServerBasePacket {
    public S_PinkName(int objectId, int time) {
        writeC(L1Opcodes.S_OPCODE_PINKNAME);
        writeD(objectId);
        writeD(time);
    }
}
