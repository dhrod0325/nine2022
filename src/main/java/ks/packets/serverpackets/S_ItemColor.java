package ks.packets.serverpackets;

import ks.core.network.opcode.L1Opcodes;
import ks.model.instance.L1ItemInstance;

public class S_ItemColor extends ServerBasePacket {
    public S_ItemColor(L1ItemInstance item) {
        if (item == null) {
            return;
        }

        buildPacket(item);
    }

    public S_ItemColor(L1ItemInstance item, int color) {
        if (item == null) {
            return;
        }
        buildPacket(item, color);
    }

    private void buildPacket(L1ItemInstance item) {
        writeC(L1Opcodes.S_OPCODE_ITEMCOLOR);
        writeD(item.getId());
        writeC(item.getBless()); // 0:b 1:n 2:c -의 값:아이템이 봉인되어?
    }

    private void buildPacket(L1ItemInstance item, int color) {
        writeC(L1Opcodes.S_OPCODE_ITEMCOLOR);
        writeD(item.getId());
        // 0 : 축복 1: 보통 2: 저주 3: 미확인 128: 축봉인 129: 봉인 130: 저주봉인 131: 미확인봉인
        writeC(color);
    }
}
