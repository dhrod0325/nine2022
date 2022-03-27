package ks.model.board;

import ks.core.network.L1Client;
import ks.model.L1Object;
import ks.model.L1World;
import ks.model.instance.L1BoardInstance;
import ks.model.pc.L1PcInstance;
import ks.packets.clientpackets.ClientBasePacket;
import ks.util.common.DateUtils;

public class C_BoardWrite extends ClientBasePacket {
    public C_BoardWrite(byte[] data, L1Client client) {
        super(data);

        int objectId = readD();

        String date = DateUtils.currentTime();
        String title = readS();
        String content = readS();

        L1PcInstance pc = client.getActiveChar();
        L1Object tg = L1World.getInstance().findObject(objectId);

        if (tg instanceof L1BoardInstance) {
            L1BoardInstance board = (L1BoardInstance) tg;
            board.onWrite(pc, title, content, date);
        }
    }
}
