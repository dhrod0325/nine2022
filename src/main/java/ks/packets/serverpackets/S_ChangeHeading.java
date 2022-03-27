package ks.packets.serverpackets;

import ks.core.network.opcode.L1Opcodes;
import ks.model.L1Character;

public class S_ChangeHeading extends ServerBasePacket {
    public S_ChangeHeading(L1Character cha) {
        buildPacket(cha);
    }

    private void buildPacket(L1Character cha) {
        writeC(L1Opcodes.S_OPCODE_CHANGEHEADING);
        writeD(cha.getId());
        writeC(cha.getHeading());
    }
}
