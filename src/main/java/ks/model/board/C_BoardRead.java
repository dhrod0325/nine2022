package ks.model.board;

import ks.core.network.L1Client;
import ks.model.L1Object;
import ks.model.L1World;
import ks.model.instance.L1BoardInstance;
import ks.packets.clientpackets.ClientBasePacket;

public class C_BoardRead extends ClientBasePacket {
    public C_BoardRead(byte[] decrypt, L1Client client) {
        super(decrypt);
        int objId = readD();
        int topicNumber = readD();

        L1Object obj = L1World.getInstance().findObject(objId);

        if (obj instanceof L1BoardInstance) {
            L1BoardInstance board = (L1BoardInstance) obj;
            board.onViewClick(client.getActiveChar(), topicNumber);
        }
    }
}
