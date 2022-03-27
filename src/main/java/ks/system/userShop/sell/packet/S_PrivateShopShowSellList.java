package ks.system.userShop.sell.packet;

import ks.core.network.opcode.L1Opcodes;
import ks.model.L1PrivateShopSell;
import ks.model.instance.L1ItemInstance;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.ServerPacket;
import ks.system.userShop.L1UserShopNpcInstance;

import java.util.List;

public class S_PrivateShopShowSellList extends ServerPacket {
    public S_PrivateShopShowSellList(L1PcInstance pc, L1UserShopNpcInstance npc, List<L1PrivateShopSell> sellList) {
        if (sellList.isEmpty()) {
            pc.sendPackets("판매중인 품목이 없습니다");
            return;
        }

        writeC(L1Opcodes.S_OPCODE_PRIVATESHOPLIST);
        writeC(0);
        writeD(npc.getId());
        writeH(sellList.size());

        int i = 0;

        for (L1PrivateShopSell sell : sellList) {
            int itemObjectId = sell.getItemObjectId();
            int count = sell.getSellTotalCount() - sell.getSellCount();
            int price = sell.getSellPrice();

            L1ItemInstance item = npc.getInventory().getItem(itemObjectId);

            writeC(i);
            writeC(item.getBless());
            writeH(item.getItem().getGfxId());
            writeD(count);
            writeD(price);
            writeS(item.getNumberedViewName(count));
            writeC(0);

            i++;
        }
    }
}
