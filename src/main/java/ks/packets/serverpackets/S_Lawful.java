package ks.packets.serverpackets;

import ks.core.network.opcode.L1Opcodes;

public class S_Lawful extends ServerBasePacket {
    public S_Lawful(int objid, int lawful) {
        buildPacket(objid, lawful);
    }

    private void buildPacket(int objid, int lawful) {
        writeC(L1Opcodes.S_OPCODE_LAWFUL);
        writeD(objid);
        writeH(lawful);
        writeD(0);
    }
}