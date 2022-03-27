package ks.packets.serverpackets;

import ks.core.network.opcode.L1Opcodes;

public class S_Invis extends ServerBasePacket {
    public S_Invis(int objid, int type) {
        buildPacket(objid, type);
    }

    private void buildPacket(int objid, int type) {
        writeC(L1Opcodes.S_OPCODE_INVIS);
        writeD(objid);
        writeC(type);
    }
}
