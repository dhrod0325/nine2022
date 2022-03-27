package ks.packets.serverpackets;

import ks.core.network.opcode.L1Opcodes;

public class S_CurseBlind extends ServerBasePacket {
    public S_CurseBlind(int type) {
        buildPacket(type);
    }

    private void buildPacket(int type) {
        writeC(L1Opcodes.S_OPCODE_CURSEBLIND);
        writeH(type);
    }
}
