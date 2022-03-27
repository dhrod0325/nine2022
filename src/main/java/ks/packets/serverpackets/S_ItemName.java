package ks.packets.serverpackets;

import ks.core.network.opcode.L1Opcodes;
import ks.model.instance.L1ItemInstance;

public class S_ItemName extends ServerBasePacket {
    public S_ItemName(L1ItemInstance item) {
        if (item == null) {
            return;
        }

        writeC(L1Opcodes.S_OPCODE_ITEMNAME);
        writeD(item.getId());
        writeS(item.getViewName());
    }
}