package ks.packets.serverpackets;

import ks.core.network.opcode.L1Opcodes;
import ks.model.bookMark.L1BookMark;
import ks.model.pc.L1PcInstance;

import java.util.List;

public class S_BookMarkLoad extends ServerBasePacket {
    public S_BookMarkLoad(L1PcInstance pc) {

        if (L1Opcodes.SERVER_VERSION == 3.1) {
            build31(pc);
        } else if (L1Opcodes.SERVER_VERSION == 3.8) {
            build38(pc);
        }
    }

    private void build38(L1PcInstance pc) {
        try {
            List<L1BookMark> bookMarkList = pc.getBookMark().getBookMarkList();
            List<L1BookMark> speedBookmarkList = pc.getBookMark().getSpeedBookmarkList();

            int size = bookMarkList.size();
            int fastSize = speedBookmarkList.size();

            int bookSize = pc.getMarkCount() + 6;
            int tempSize = bookSize - 1 - size - fastSize;

            writeC(L1Opcodes.S_OPCODE_RETURNEDSTAT);
            writeC(42);
            writeC(bookSize);
            writeC(0x00);
            writeC(0x02);

            if (size > 0) {
                for (int i = 0; i < size; i++) {
                    writeC(i);
                }
            }

            for (L1BookMark s : speedBookmarkList) {
                writeC(s.getNumId());
            }

            if (tempSize > 0) {
                for (int i = 0; i < tempSize; i++) {
                    writeC(0xff);
                }
            }

            writeH(pc.getMarkCount());
            writeH(size);

            for (L1BookMark s : bookMarkList) {
                writeD(s.getNumId());
                writeS(s.getName());
                writeH(s.getMapId());
                writeH(s.getLocX());
                writeH(s.getLocY());
            }
        } catch (Exception e) {
            logger.error(e);
        }
    }

    private void build31(L1PcInstance pc) {
        try {
            List<L1BookMark> bookMarkList = pc.getBookMark().getBookMarkList();

            for (L1BookMark bookMark : bookMarkList) {
                pc.sendPackets(new S_Bookmarks(bookMark.getName(), bookMark.getMapId(), bookMark.getId()));
            }
        } catch (Exception e) {
            logger.error(e);
        }
    }
}
