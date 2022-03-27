package ks.packets.serverpackets;

import ks.core.network.opcode.L1Opcodes;

public class S_Door extends ServerBasePacket {
    public S_Door(int x, int y, int direction, int passable) {
        buildPacket(x, y, direction, passable);
    }

    private void buildPacket(int x, int y, int direction, int passable) {
        writeC(L1Opcodes.S_OPCODE_ATTRIBUTE);
        writeH(x);
        writeH(y);
        writeC(direction);
        writeC(passable);
    }
}
