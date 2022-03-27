package ks.packets.serverpackets;

import ks.core.network.opcode.L1Opcodes;

public class S_SkillSound extends ServerBasePacket {
    public S_SkillSound(int objid, int gfxid) {
        buildPacket(objid, gfxid);
    }

    private void buildPacket(int objId, int gfxId) {
        writeC(L1Opcodes.S_OPCODE_SKILLSOUNDGFX);
        writeD(objId);
        writeH(gfxId);
        writeC(0);
    }
}
