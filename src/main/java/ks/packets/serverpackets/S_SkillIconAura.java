package ks.packets.serverpackets;

import ks.core.network.opcode.L1Opcodes;

public class S_SkillIconAura extends ServerBasePacket {
    public S_SkillIconAura(int i, int j) {
        writeC(L1Opcodes.S_OPCODE_PACKETBOX);
        writeC(0x16);
        writeC(i);
        writeH(j);
    }
}
