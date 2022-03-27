package ks.packets.serverpackets;

import ks.core.network.opcode.L1Opcodes;
import ks.model.L1Character;


public class S_AttackMissPacket extends ServerBasePacket {
    public S_AttackMissPacket(L1Character attacker, int targetId) {
        writeC(L1Opcodes.S_OPCODE_ATTACKPACKET);
        writeC(1);
        writeD(attacker.getId());
        writeD(targetId);
        writeH(0);
        writeC(attacker.getHeading());
        writeD(0);
        writeC(0);
    }

    public S_AttackMissPacket(L1Character attacker, int targetId, int actId) {
        writeC(L1Opcodes.S_OPCODE_ATTACKPACKET);
        writeC(actId);
        writeD(attacker.getId());
        writeD(targetId);
        writeH(0);
        writeC(attacker.getHeading());
        writeD(0);
        writeC(0);
    }
}
