package ks.packets.serverpackets;

import ks.core.network.opcode.L1Opcodes;


public class S_TaxRate extends ServerBasePacket {
    public S_TaxRate(int objecId) {
        writeC(L1Opcodes.S_OPCODE_TAXRATE);
        writeD(objecId);
        writeC(10); // 10%~50%
        writeC(50);
    }

}
