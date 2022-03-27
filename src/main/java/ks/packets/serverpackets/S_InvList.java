package ks.packets.serverpackets;

import ks.core.network.opcode.L1Opcodes;
import ks.model.instance.L1ItemInstance;
import ks.model.pc.L1PcInstance;

import java.util.List;

public class S_InvList extends ServerBasePacket {
    public S_InvList(L1PcInstance pc) {
        writeC(L1Opcodes.S_OPCODE_INVLIST);

        List<L1ItemInstance> items = pc.getInventory().getItems();

        writeC(items.size());

        for (L1ItemInstance item : items) {
            S_AddItem packet = new S_AddItem();
            packet.build(item);
            writeByte(packet.toBytes());
        }
    }
}
