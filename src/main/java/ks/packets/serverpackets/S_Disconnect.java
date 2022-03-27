package ks.packets.serverpackets;

import ks.core.network.opcode.L1Opcodes;

public class S_Disconnect extends ServerBasePacket {
    public S_Disconnect() {
        int content = 500;

        writeC(L1Opcodes.S_OPCODE_DISCONNECT);
        writeH(content);
        writeD(0x00000000);
    }
}
