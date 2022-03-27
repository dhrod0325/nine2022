package ks.packets.serverpackets;

import ks.core.network.opcode.L1Opcodes;
import ks.model.L1Character;
import ks.model.pc.L1PcInstance;

public class S_StrUp extends ServerBasePacket {
    public S_StrUp(L1Character character, int type, int time) {
        if (character instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) character;
            writeC(L1Opcodes.S_OPCODE_STRUP);
            writeH(time);
            writeC(pc.getAbility().getTotalStr());
            writeC(pc.getInventory().getWeight240());
            writeC(type);
            writeD(0);
        }
    }
}
