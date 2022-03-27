package ks.system.userShop.sell.packet;

import ks.app.config.prop.CodeConfig;
import ks.core.network.opcode.L1Opcodes;
import ks.model.L1PrivateShopSell;
import ks.model.instance.L1ItemInstance;
import ks.packets.serverpackets.ServerPacket;

import java.util.List;

public class S_PrivateShopSellPrice extends ServerPacket {
    public S_PrivateShopSellPrice(int handleId, List<L1PrivateShopSell> list) {
        writeC(L1Opcodes.S_OPCODE_SHOWRETRIEVELIST);
        writeD(handleId);

        writeH(list.size());
        writeC(10);

        for (L1PrivateShopSell sell : list) {
            L1ItemInstance item = sell.getItem();

            writeD(item.getId());
            writeC(item.getItem().getType2());
            writeH(item.getGfxId());
            writeC(item.getBless());
            writeD(CodeConfig.MAX_TRADE_PRICE);
            writeC(item.isIdentified() ? 1 : 0);
            writeS(item.getNumberedViewName(sell.getSellTotalCount()));
        }

        writeD(30);
        writeD(0x00000000);
        writeH(0x00);
    }
}
