package ks.packets.serverpackets;

import ks.core.network.opcode.L1Opcodes;

public class S_ElfIcon extends ServerBasePacket {

    public S_ElfIcon(int a, int b, int c, int d) {
        writeC(L1Opcodes.S_OPCODE_PACKETBOX);
        writeC(0x15);
        writeC(a);
        writeC(b);
        writeC(c);
        writeC(d);
        writeC(0);
        writeC(0);

    }
}
