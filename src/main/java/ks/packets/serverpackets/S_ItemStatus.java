package ks.packets.serverpackets;

import ks.core.network.opcode.L1Opcodes;
import ks.model.instance.L1ItemInstance;

public class S_ItemStatus extends ServerBasePacket {
    public S_ItemStatus(L1ItemInstance item) {
        writeC(L1Opcodes.S_OPCODE_ITEMSTATUS);
        writeD(item.getId());
        writeS(item.getViewName());
        writeD(item.getCount());

        if (!item.isIdentified()) {
            writeC(0);
        } else {
            byte[] status = item.getStatusBytes();
            writeC(status.length);

            for (byte b : status) {
                writeC(b);
            }
        }
    }
}
