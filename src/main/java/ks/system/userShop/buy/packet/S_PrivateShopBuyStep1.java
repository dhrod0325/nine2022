package ks.system.userShop.buy.packet;

import ks.core.network.opcode.L1Opcodes;
import ks.model.instance.L1ItemInstance;
import ks.packets.serverpackets.ServerPacket;

import java.util.List;

public class S_PrivateShopBuyStep1 extends ServerPacket {
    public S_PrivateShopBuyStep1(int handleId, List<L1ItemInstance> list) {
        writeC(L1Opcodes.S_OPCODE_SHOWRETRIEVELIST);
        writeD(handleId);

        writeH(list.size());
        writeC(3); // 개인 창고

        for (L1ItemInstance item : list) {
            writeD(item.getId());
            writeC(item.getItem().getType2()); //탬타입..돌려주기 응헉
            writeH(item.getGfxId());
            writeC(item.getBless());

            if (!item.getItem().isEtc()) {
                writeD(5);
            } else {
                writeD(500);
            }

            writeC(item.isIdentified() ? 1 : 0);
            writeS(item.getViewName());
        }

        writeD(30);
        writeD(0x00000000);
        writeH(0x00);
    }
}