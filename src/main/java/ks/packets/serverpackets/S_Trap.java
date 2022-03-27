package ks.packets.serverpackets;

import ks.core.network.opcode.L1Opcodes;
import ks.model.instance.L1TrapInstance;

public class S_Trap extends ServerBasePacket {
    public S_Trap(L1TrapInstance trap, String name) {
        writeC(L1Opcodes.S_OPCODE_SHOWOBJ);
        writeH(trap.getX());
        writeH(trap.getY());
        writeD(trap.getId());
        writeH(7); // adena
        writeC(0);
        writeC(0);
        writeC(0);
        writeC(0);
        writeD(0);
        writeC(0);
        writeC(0);
        writeS(name);
        writeC(0);
        writeD(0);
        writeD(0);
        writeC(255);
        writeC(0);
        writeC(0);
        writeC(0);
        writeH(65535);
        // writeD(0x401799a);
        writeD(0);
        writeC(8);
        writeC(0);
    }
}
