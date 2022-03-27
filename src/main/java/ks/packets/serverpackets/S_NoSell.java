package ks.packets.serverpackets;

import ks.core.network.opcode.L1Opcodes;
import ks.model.instance.L1NpcInstance;

public class S_NoSell extends ServerBasePacket {
    public S_NoSell(L1NpcInstance npc) {
        buildPacket(npc);
    }

    private void buildPacket(L1NpcInstance npc) {
        writeC(L1Opcodes.S_OPCODE_SHOWHTML);
        writeD(npc.getId());
        writeS("nosell");
        writeC(1);
    }
}
