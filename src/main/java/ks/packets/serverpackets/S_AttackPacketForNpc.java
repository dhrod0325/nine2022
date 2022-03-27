package ks.packets.serverpackets;

import ks.core.network.opcode.L1Opcodes;
import ks.model.L1Character;

public class S_AttackPacketForNpc extends ServerBasePacket {
    public S_AttackPacketForNpc(L1Character cha, int npcObjectId, int type) {
        build(cha, npcObjectId, type);
    }

    private void build(L1Character target, int npcObjectId, int actId) {
        writeC(L1Opcodes.S_OPCODE_ATTACKPACKET);
        writeC(actId);
        writeD(npcObjectId);
        writeD(target.getId());

        writeH(0x01);

        writeC(target.getHeading());
        writeH(0x0000);
        writeH(0x0000);
        writeC(0x00);
    }
}
