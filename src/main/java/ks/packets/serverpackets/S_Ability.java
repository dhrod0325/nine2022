package ks.packets.serverpackets;

import ks.core.network.opcode.L1Opcodes;

public class S_Ability extends ServerBasePacket {

    public S_Ability(int type, boolean equipped) {
        buildPacket(type, equipped);
    }

    private void buildPacket(int type, boolean equipped) {
        writeC(L1Opcodes.S_OPCODE_ABILITY);
        writeC(type);

        if (equipped) {
            writeC(0x01);
        } else {
            writeC(0x00);
        }

        writeC(0x02);
        writeH(0x0000);
    }
}
