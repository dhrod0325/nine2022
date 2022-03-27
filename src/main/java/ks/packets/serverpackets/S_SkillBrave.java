package ks.packets.serverpackets;

import ks.core.network.opcode.L1Opcodes;


public class S_SkillBrave extends ServerBasePacket {
    public S_SkillBrave(int objectId, int type, int time) {
        writeC(L1Opcodes.S_OPCODE_SKILLBRAVE);
        writeD(objectId);
        writeC(type);
        writeH(time);
    }
}
