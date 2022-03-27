package ks.system.userShop.sell.packet;

import ks.core.network.opcode.L1Opcodes;
import ks.model.instance.L1ItemInstance;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.ServerPacket;
import ks.util.L1CommonUtils;

import java.util.ArrayList;
import java.util.List;

public class S_PrivateShopSellStep1 extends ServerPacket {
    public S_PrivateShopSellStep1(L1PcInstance pc, int handleId, List<L1ItemInstance> list) {
        writeC(L1Opcodes.S_OPCODE_SHOWRETRIEVELIST);
        writeD(handleId);

        List<L1ItemInstance> tempSellList = new ArrayList<>();

        for (L1ItemInstance item : list) {
            if (item.getItemId() == 40308 || !item.getItem().isTradeAble() || !item.getItem().isDeleteAble() || item.isEquipped()) {
                continue;
            }

            if (L1CommonUtils.isNotAvailableTrade(pc, item.getId(), item, item.getCount())) {
                continue;
            }

            tempSellList.add(item);
        }

        writeH(tempSellList.size());
        writeC(3); // 개인 창고

        for (L1ItemInstance item : tempSellList) {
            writeD(item.getId());
            writeC(item.getItem().getType2()); //탬타입..돌려주기 응헉
            writeH(item.getGfxId());
            writeC(item.getBless());
            writeD(item.getCount());
            writeC(item.isIdentified() ? 1 : 0);
            writeS(item.getViewName());
        }

        writeD(30);
        writeD(0x00000000);
        writeH(0x00);
    }
}
