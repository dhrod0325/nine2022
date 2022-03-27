package ks.packets.serverpackets;

import ks.core.network.opcode.L1Opcodes;

public class S_DoActionShop extends ServerBasePacket {

    public S_DoActionShop(int object, int gfxid, byte[] message) {
        writeC(L1Opcodes.S_OPCODE_DOACTIONGFX);
        writeD(object);
        writeC(gfxid);
        writeByte(message);
    }

    public S_DoActionShop(int object, int gfxid, String message) {
        writeC(L1Opcodes.S_OPCODE_DOACTIONGFX);
        writeD(object);
        writeC(gfxid);
        writeS(message);
    }
}
