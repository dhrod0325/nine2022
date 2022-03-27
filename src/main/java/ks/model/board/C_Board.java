package ks.model.board;

import ks.core.network.L1Client;
import ks.model.L1Object;
import ks.model.L1World;
import ks.model.instance.L1AuctionBoardInstance;
import ks.model.instance.L1BoardInstance;
import ks.model.instance.L1NpcInstance;
import ks.packets.clientpackets.ClientBasePacket;

public class C_Board extends ClientBasePacket {
    public C_Board(byte[] data, L1Client client) {
        super(data);
        int objectId = readD();

        L1Object obj = L1World.getInstance().findObject(objectId);

        if (!isBoardInstance(obj)) {
            if (obj instanceof L1NpcInstance) {
                L1NpcInstance npc = (L1NpcInstance) obj;
                npc.onTalkAction(client.getActiveChar());
            }

            return;
        }

        obj.onAction(client.getActiveChar());
    }

    private boolean isBoardInstance(L1Object obj) {
        return (obj instanceof L1BoardInstance || obj instanceof L1AuctionBoardInstance);
    }
}
