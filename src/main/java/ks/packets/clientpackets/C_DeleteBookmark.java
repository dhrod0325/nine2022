package ks.packets.clientpackets;

import ks.core.network.L1Client;
import ks.model.bookMark.L1BookMarkTable;
import ks.model.pc.L1PcInstance;

public class C_DeleteBookmark extends ClientBasePacket {
    public C_DeleteBookmark(byte[] data, L1Client client) {
        super(data);
        try {
            L1PcInstance pc = client.getActiveChar();

            if (pc == null)
                return;

            String bookmarkName = readS();

            if (!bookmarkName.isEmpty()) {
                L1BookMarkTable.delete(pc, bookmarkName);
            }
        } catch (Exception e) {
            logger.error(e);
        }
    }
}
