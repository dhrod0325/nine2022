package ks.packets.serverpackets;

import ks.core.network.opcode.L1Opcodes;
import ks.model.L1Character;

public class S_AttackPacket extends ServerBasePacket {
    public S_AttackPacket(L1Character character, int objId, int actId) {
        buildPacket(character, objId, actId, 0);
    }

    private void buildPacket(L1Character target, int objId, int actId, int attackType) {
        writeC(L1Opcodes.S_OPCODE_ATTACKPACKET);
        writeC(actId);
        writeD(target.getId());
        writeD(objId);
        writeH(0x02);

        writeC(target.getHeading());
        writeH(0x0000);
        writeH(0x0000);
        writeC(attackType);
    }

    public S_AttackPacket(L1Character character, int objId, int type, int attackType) {
        buildPacket(character, objId, type, attackType);
    }
}
