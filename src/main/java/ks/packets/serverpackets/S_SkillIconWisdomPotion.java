package ks.packets.serverpackets;

import ks.core.network.opcode.L1Opcodes;


public class S_SkillIconWisdomPotion extends ServerBasePacket {

    public S_SkillIconWisdomPotion(int time) {
        writeC(L1Opcodes.S_OPCODE_PACKETBOX);
        writeC(0x39);
        writeC(0x2c);
        writeH(time);
    }
}
