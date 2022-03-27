package ks.packets.serverpackets;

import ks.core.network.opcode.L1Opcodes;
import ks.model.instance.L1ItemInstance;


public class S_TradeAddItem extends ServerBasePacket {
    public S_TradeAddItem(L1ItemInstance item, int count, int type) {
        writeC(L1Opcodes.S_OPCODE_TRADEADDITEM);
        writeC(type); // 0:교환창 상단 1:교환창 하단
        writeH(item.getItem().getGfxId());
        writeS(item.getNumberedViewName(count));

        // 0:축복 1:통상 2:저주 3:미감정
        if (!item.isIdentified()) { // 미확인
            writeC(3);
        } else { // 교환이 끝난 상태
            byte[] status = null;
            int bless = item.getBless();
            writeC(bless);
            status = item.getStatusBytes();
            writeC(status.length);
            for (byte b : status) {
                writeC(b);
            }
        }
        writeC(28);
    }
}
