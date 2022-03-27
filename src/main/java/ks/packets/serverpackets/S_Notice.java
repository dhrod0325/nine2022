package ks.packets.serverpackets;

import ks.core.datatables.notice.Notice;
import ks.core.datatables.notice.NoticeTable;
import ks.core.network.L1Client;
import ks.core.network.opcode.L1Opcodes;

public class S_Notice extends ServerBasePacket {
    public S_Notice(String msg) {
        writeC(L1Opcodes.S_OPCODE_NOTICE);
        writeS(msg);
    }

    public S_Notice(L1Client client) {
        if (client == null) {
            return;
        }

        String accountName = client.getAccountName();

        int noticeId = NoticeTable.getInstance().findLastNoticeId(accountName);
        Notice notice = NoticeTable.getInstance().selectFirstNoticeById(noticeId);

        if (notice != null) {
            writeC(L1Opcodes.S_OPCODE_NOTICE);
            writeS(notice.getMessageLine());

            int lastNoticeId = notice.getId();
            NoticeTable.getInstance().update(client.getAccountName(), lastNoticeId);
        }
    }
}