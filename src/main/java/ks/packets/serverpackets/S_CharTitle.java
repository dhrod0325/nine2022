package ks.packets.serverpackets;

import ks.core.network.opcode.L1Opcodes;

public class S_CharTitle extends ServerBasePacket {
    public S_CharTitle(int objid, String title) {
        writeC(L1Opcodes.S_OPCODE_CHARTITLE);
        writeD(objid);
        writeS(title);
    }
}
