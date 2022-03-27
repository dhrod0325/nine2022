package ks.packets.serverpackets;

import ks.core.network.opcode.L1Opcodes;

public class S_SkillHaste extends ServerBasePacket {
    public S_SkillHaste(int i, int j, int k) {
        writeC(L1Opcodes.S_OPCODE_SKILLHASTE);
        writeD(i);
        writeC(j);
        writeH(k);
    }
}
