package ks.packets.serverpackets;

import ks.core.network.opcode.L1Opcodes;
import ks.model.instance.L1ItemInstance;

public class S_DeleteInventoryItem extends ServerBasePacket {
    public S_DeleteInventoryItem(L1ItemInstance item) {
        if (item != null) {
            writeC(L1Opcodes.S_OPCODE_DELETEINVENTORYITEM);
            writeD(item.getId());
        }
    }
}
