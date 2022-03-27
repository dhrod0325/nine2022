package ks.packets.serverpackets;

import ks.core.network.opcode.L1Opcodes;

public class KeyPacket extends ServerBasePacket {
//    public static final long SEED = 0x2d449e88L;
//    public static final byte[] KEY_PACKET = {
//            (byte) 0x12, (byte) 0x00,
//            (byte) 0x58,
//            (byte) 0x88, (byte) 0x9e, (byte) 0x44, (byte) 0x2d,
//            (byte) 0xac, (byte) 0x37, (byte) 0x1c, (byte) 0x65,
//            (byte) 0xa5, (byte) 0x13, (byte) 0xdf, (byte) 0xae,
//            (byte) 0xfe, (byte) 0xda, (byte) 0x54,
//    };

    public KeyPacket() {
        for (byte b : L1Opcodes.KEY_PACKET) {
            writeC(b);
        }
    }
}
