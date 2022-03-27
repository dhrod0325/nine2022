package ks.packets.serverpackets;

import ks.core.network.opcode.L1Opcodes;

public class S_LoginResult extends ServerBasePacket {
    public static final int REASON_USER_OR_PASS_WRONG = 0x08;

    public S_LoginResult(int reason) {
        buildPacket(reason);
    }

    private void buildPacket(int reason) {
        writeC(L1Opcodes.S_OPCODE_LOGINRESULT);
        writeC(reason);
        writeD(0x00000000);
        writeD(0x00000000);
        writeD(0x00000000);
    }
}
