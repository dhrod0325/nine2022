package ks.system.userShop.buy.packet;

import ks.core.network.opcode.L1Opcodes;
import ks.model.L1PrivateShopBuy;
import ks.model.instance.L1ItemInstance;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.ServerPacket;
import ks.system.userShop.L1UserShopNpcInstance;
import ks.system.userShop.table.L1UserShopTable;

import java.util.ArrayList;
import java.util.List;

public class S_PrivateShopShowBuyList extends ServerPacket {
    private final L1UserShopTable dao = L1UserShopTable.getInstance();

    private final List<L1PrivateShopBuy> tempList = new ArrayList<>();

    public S_PrivateShopShowBuyList(L1PcInstance pc, L1UserShopNpcInstance npc, List<L1PrivateShopBuy> list) {
        List<L1PrivateShopBuy> tempList = makeList(pc, npc, list);

        if (tempList.isEmpty()) {
            pc.sendPackets("당신에게 구매할 품목이 없습니다");
            return;
        }

        pc.sendGreenMessageAndSystemMessage("상품 판매는 한번에 100개까지 가능합니다");

        writeC(L1Opcodes.S_OPCODE_SHOWSHOPSELLLIST);
        writeD(npc.getId());
        writeH(tempList.size());

        for (L1PrivateShopBuy vo : tempList) {
            writeD(vo.getItemObjectId());
            writeD(vo.getBuyPrice());
        }

        writeH(0x07);
    }

    private List<L1PrivateShopBuy> makeList(L1PcInstance pc, L1UserShopNpcInstance npc, List<L1PrivateShopBuy> list) {
        List<L1ItemInstance> invList = pc.getInventory().getItems();

        for (L1ItemInstance is : invList) {
            if (is.isEquipped()) {
                continue;
            }

            for (L1PrivateShopBuy vo : list) {
                if (vo.getItem().getItemId() == is.getItemId()
                        && vo.getItem().getEnchantLevel() == is.getEnchantLevel()
                        && vo.getItem().getBless() == is.getBless()
                        && vo.getItem().getAttrEnchantLevel() == is.getAttrEnchantLevel()
                ) {
                    int currentCnt = dao.selectCurrentBuyCountByItem(npc.getMasterObjId(), vo.getItem());

                    if (currentCnt >= vo.getBuyTotalCount()) {
                        continue;
                    }

                    L1PrivateShopBuy tempBuy = new L1PrivateShopBuy();
                    tempBuy.setBuyPrice(vo.getBuyPrice());
                    tempBuy.setBuyCount(vo.getBuyCount());
                    tempBuy.setBuyTotalCount(vo.getBuyTotalCount());
                    tempBuy.setItemObjectId(is.getId());
                    tempBuy.setItem(is);

                    tempList.add(tempBuy);
                }
            }
        }

        return tempList;
    }

    public L1PrivateShopBuy findByObjectId(int objectId) {
        for (L1PrivateShopBuy o : tempList) {
            if (objectId == o.getItemObjectId())
                return o;
        }

        return null;
    }
}