package ks.model.board;

import ks.core.network.L1Client;
import ks.model.L1Object;
import ks.model.L1World;
import ks.model.instance.L1BoardInstance;
import ks.packets.clientpackets.ClientBasePacket;

public class C_BoardDelete extends ClientBasePacket {
    public C_BoardDelete(byte[] data, L1Client client) {
        super(data);

        int objId = readD();
        int topicNumber = readD();

        L1Object obj = L1World.getInstance().findObject(objId);

        L1BoardInstance board = (L1BoardInstance) obj;

        if (board == null) {
            return;
        }

        board.onDeleteClick(client.getActiveChar(), topicNumber);
    }
}
