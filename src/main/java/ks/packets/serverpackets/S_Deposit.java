package ks.packets.serverpackets;

import ks.core.network.opcode.L1Opcodes;

public class S_Deposit extends ServerBasePacket {
    public S_Deposit(int objecId) {
        writeC(L1Opcodes.S_OPCODE_DEPOSIT);
        writeD(objecId);
    }

}
