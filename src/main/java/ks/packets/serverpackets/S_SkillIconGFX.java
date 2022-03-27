package ks.packets.serverpackets;

import ks.core.network.opcode.L1Opcodes;

public class S_SkillIconGFX extends ServerBasePacket {
    public S_SkillIconGFX(int type, int time) {
        writeC(L1Opcodes.S_OPCODE_PACKETBOX);
        writeC(type);
        writeH(time);
    }

    public S_SkillIconGFX(int i) {
        writeC(L1Opcodes.S_OPCODE_PACKETBOX);
        writeC(0xa0);
        writeC(1);
        writeH(0);
        writeC(2);
        writeH(i);
    }

    public S_SkillIconGFX(int i, boolean changeWeapon) {
        writeC(L1Opcodes.S_OPCODE_PACKETBOX);
        writeC(0xa0);
        writeC(changeWeapon ? 1 : 0);
        writeH(0);
        writeC(2);
        writeH(i);
    }
}
