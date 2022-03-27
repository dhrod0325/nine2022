package ks.packets.clientpackets;

import ks.core.network.L1Client;
import ks.model.L1Object;
import ks.model.L1World;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_PrivateShop;
import ks.system.userShop.L1UserShopNpcInstance;

public class C_ShopList extends ClientBasePacket {
    public C_ShopList(byte[] data, L1Client lineageClient) {
        super(data);

        int type = readC();
        int objectId = readD();

        L1PcInstance pc = lineageClient.getActiveChar();

        if (pc == null) {
            return;
        }

        L1Object obj = L1World.getInstance().findObject(objectId);

        if (obj instanceof L1UserShopNpcInstance) {
            pc.sendPackets(new S_PrivateShop(pc, objectId, type));
        }
    }
}
