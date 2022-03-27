package ks.packets.serverpackets;

import ks.core.network.opcode.L1Opcodes;

public class S_SkillIconShield extends ServerBasePacket {
    public static final int SHIELD = 2;
    public static final int SHADOW_ARMOR = 3;
    public static final int EARTH_SKIN = 6;
    public static final int EARTH_GUARDIAN = 7;
    public static final int IRON_SKIN = 10;

    public S_SkillIconShield(int type, int time) {
        writeC(L1Opcodes.S_OPCODE_SKILLICONSHIELD);
        writeH(time);
        writeC(type);
        writeD(0);
    }
}
