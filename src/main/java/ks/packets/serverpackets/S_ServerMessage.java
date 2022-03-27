package ks.packets.serverpackets;

import ks.core.network.opcode.L1Opcodes;

public class S_ServerMessage extends ServerBasePacket {
    public static final int NO_PLEDGE = 208;

    public S_ServerMessage(int type) {
        buildPacket(type, null, null, null, null, null, 0);
    }

    public S_ServerMessage(int type, String msg1) {
        buildPacket(type, msg1, null, null, null, null, 1);
    }

    public S_ServerMessage(int type, String msg1, String msg2) {
        buildPacket(type, msg1, msg2, null, null, null, 2);
    }

    public S_ServerMessage(int type, String msg1, String msg2, String msg3) {
        buildPacket(type, msg1, msg2, msg3, null, null, 3);
    }

    public S_ServerMessage(int type, String msg1, String msg2, String msg3,
                           String msg4) {
        buildPacket(type, msg1, msg2, msg3, msg4, null, 4);
    }

    public S_ServerMessage(int type, String msg1, String msg2, String msg3,
                           String msg4, String msg5) {

        buildPacket(type, msg1, msg2, msg3, msg4, msg5, 5);
    }

    private void buildPacket(int type, String msg1, String msg2, String msg3,
                             String msg4, String msg5, int check) {

        writeC(L1Opcodes.S_OPCODE_SERVERMSG);
        writeH(type);

        if (check == 0) {
            writeC(0);
        } else if (check == 1) {
            writeC(1);
            writeS(msg1);
        } else if (check == 2) {
            writeC(2);
            writeS(msg1);
            writeS(msg2);
        } else if (check == 3) {
            writeC(3);
            writeS(msg1);
            writeS(msg2);
            writeS(msg3);
        } else if (check == 4) {
            writeC(4);
            writeS(msg1);
            writeS(msg2);
            writeS(msg3);
            writeS(msg4);
        } else {
            writeC(5);
            writeS(msg1);
            writeS(msg2);
            writeS(msg3);
            writeS(msg4);
            writeS(msg5);
        }
    }
}
