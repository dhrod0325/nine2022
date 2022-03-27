package ks.packets.serverpackets;

import ks.model.L1World;
import ks.model.pc.L1PcInstance;
import ks.system.userShop.L1UserShopNpcInstance;

public class S_PrivateShop extends ServerBasePacket {
    public S_PrivateShop(L1PcInstance pc, int objectId, int type) {
        try {
            L1UserShopNpcInstance shopNpcInstance = (L1UserShopNpcInstance) L1World.getInstance().findObject(objectId);

            if (shopNpcInstance == null) {
                return;
            }

            shopNpcInstance.showList(pc, type);
        } catch (Exception e) {
            logger.error("S_PrivateShop 오류", e);
        }
    }
}
