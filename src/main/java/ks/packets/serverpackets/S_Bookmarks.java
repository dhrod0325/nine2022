package ks.packets.serverpackets;

import ks.core.network.opcode.L1Opcodes;

public class S_Bookmarks extends ServerBasePacket {
    public S_Bookmarks(String name, int map, int bookId) {
        buildPacket(name, map, bookId);
    }

    private void buildPacket(String name, int map, int bookId) {
        writeC(L1Opcodes.S_OPCODE_BOOKMARKS);
        writeS(name);
        writeH(map);
        writeD(bookId);
    }
}