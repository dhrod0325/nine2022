package ks.packets.serverpackets;

import ks.core.network.opcode.L1Opcodes;

public class S_HowManyMake extends ServerBasePacket {
    public S_HowManyMake(int objId, int max, String htmlId) {
        writeC(L1Opcodes.S_OPCODE_INPUTAMOUNT);
        writeD(objId);
        writeD(0);
        writeD(0);
        writeD(0);
        writeD(max);
        writeH(0);
        writeS("request");
        writeS(htmlId);
    }
}
