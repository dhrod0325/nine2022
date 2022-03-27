package ks.packets.serverpackets;

import ks.core.network.opcode.L1Opcodes;

public class S_BoardNotice extends ServerBasePacket {
    public S_BoardNotice(String msg) {
        writeC(L1Opcodes.S_OPCODE_BOARDREAD);
        writeD(0);
        writeS("공지사항");
        writeS("공지사항");
        writeS("");

        writeS("\r\n\r\n" + msg.replace("\\r\\n", System.lineSeparator()));
    }
}
