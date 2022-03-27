package ks.packets.serverpackets;

import ks.core.network.opcode.L1Opcodes;

public class S_CharCreateStatus extends ServerBasePacket {
    public static final int REASON_OK = 0x02;
    public static final int REASON_ALREADY_EXSISTS = 0x06;
    public static final int REASON_INVALID_NAME = 0x09;
    public static final int REASON_WRONG_AMOUNT = 0x15;

    public S_CharCreateStatus(int reason) {
        writeC(L1Opcodes.S_OPCODE_NEWCHARWRONG);
        writeC(reason);
        writeD(0x00000000);
        writeD(0x0000);
    }
}
