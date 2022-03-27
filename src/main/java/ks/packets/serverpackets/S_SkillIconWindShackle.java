package ks.packets.serverpackets;

import ks.core.network.opcode.L1Opcodes;

public class S_SkillIconWindShackle extends ServerBasePacket {
    public S_SkillIconWindShackle(int objectId, int time) {
        int buffTime = (time / 4);

        writeC(L1Opcodes.S_OPCODE_PACKETBOX);
        writeC(0x2c);
        writeD(objectId);
        writeH(buffTime);
    }
}
