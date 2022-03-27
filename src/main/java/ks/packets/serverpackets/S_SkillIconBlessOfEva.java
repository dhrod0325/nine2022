package ks.packets.serverpackets;

import ks.core.network.opcode.L1Opcodes;


public class S_SkillIconBlessOfEva extends ServerBasePacket {
    public S_SkillIconBlessOfEva(int objectId, int time) {
        writeC(L1Opcodes.S_OPCODE_BLESSOFEVA);
        writeD(objectId);
        writeH(time);
    }
}
