package ks.packets.serverpackets;

import ks.core.network.opcode.L1Opcodes;

public class S_Light extends ServerBasePacket {
    public S_Light(int objid, int type) {
        buildPacket(objid, type);
    }

    private void buildPacket(int objid, int type) {
        writeC(L1Opcodes.S_OPCODE_LIGHT);
        writeD(objid);
        writeC(type);
    }
}
