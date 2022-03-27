package ks.packets.serverpackets;

import ks.core.network.opcode.L1Opcodes;

public class S_CloseList extends ServerBasePacket {
    public S_CloseList(int objid) {
        writeC(L1Opcodes.S_OPCODE_SHOWHTML);
        writeD(objid);
        writeS("");
    }
}
