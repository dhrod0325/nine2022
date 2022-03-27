package ks.packets.serverpackets;

import ks.core.network.opcode.L1Opcodes;
import ks.model.L1Character;


public class S_Dexup extends ServerBasePacket {
    public S_Dexup(L1Character cha, int type, int time) {
        writeC(L1Opcodes.S_OPCODE_DEXUP);
        writeH(time);
        writeC(cha.getAbility().getTotalDex());
        writeC(type);
        writeD(0);
    }
}
