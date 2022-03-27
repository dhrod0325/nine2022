package ks.packets.serverpackets;

import ks.core.datatables.ShopTable;
import ks.core.network.opcode.L1Opcodes;
import ks.model.L1Object;
import ks.model.L1World;
import ks.model.instance.L1NpcInstance;
import ks.model.pc.L1PcInstance;
import ks.model.shop.L1AccessedItem;
import ks.model.shop.L1Shop;
import ks.util.log.L1LogUtils;

import java.util.List;

public class S_ShopBuyList extends ServerBasePacket {
    public S_ShopBuyList(int objId, L1PcInstance pc) {
        L1Object object = L1World.getInstance().findObject(objId);

        if (!(object instanceof L1NpcInstance)) {
            return;
        }

        L1NpcInstance npc = (L1NpcInstance) object;
        int npcId = npc.getTemplate().getNpcId();
        L1Shop shop = ShopTable.getInstance().findShop(npcId);

        logger.debug("shop : {} npcId:{}", shop, npcId);

        if (shop == null) {
            pc.sendPackets(new S_NoSell(npc));
            return;
        }

        L1LogUtils.gmLog(pc, "상품목록 뿌리기 시작 NPC : {}", npc.getName());

        List<L1AccessedItem> accessedItems = shop.assessItems(pc.getInventory());

        if (accessedItems.isEmpty()) {
            pc.sendPackets(new S_NoSell(npc));
            return;
        }

        writeC(L1Opcodes.S_OPCODE_SHOWSHOPSELLLIST);
        writeD(objId);
        writeH(accessedItems.size());

        for (L1AccessedItem item : accessedItems) {
            writeD(item.getTargetId());
            writeD(item.getAssessedPrice());
        }

        writeH(0x07);
    }
}
