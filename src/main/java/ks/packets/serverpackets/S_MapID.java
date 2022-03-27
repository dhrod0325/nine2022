package ks.packets.serverpackets;

import ks.core.network.opcode.L1Opcodes;


public class S_MapID extends ServerBasePacket {
    public S_MapID(int mapid, boolean isUnderwater) {
        writeC(L1Opcodes.S_OPCODE_MAPID);
        writeH(mapid);
        writeC(isUnderwater ? 1 : 0);
        writeC(0x10);
        writeC(0xf8);
        writeC(0x0f);
        writeC(0);
        writeD(0);
        writeD(0);
    }
}
