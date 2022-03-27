package ks.packets.serverpackets;

import ks.core.network.opcode.L1Opcodes;

public class S_IconFromEffectList extends ServerBasePacket {
    public S_IconFromEffectList(int objId, int effectId) {
        writeC(L1Opcodes.S_OPCODE_SKILLSOUNDGFX);
        writeD(objId);
        writeH(effectId + 8689);
        writeC(0);
    }
}
