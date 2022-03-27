package ks.packets.serverpackets;

import ks.core.network.opcode.L1Opcodes;


public class S_Liquor extends ServerBasePacket {
    public S_Liquor(int objecId) {
        writeC(L1Opcodes.S_OPCODE_LIQUOR);
        writeD(objecId);
        writeC(1);
    }
}
