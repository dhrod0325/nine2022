package ks.packets.serverpackets;

import ks.core.network.opcode.L1Opcodes;

public class S_GameTime extends ServerBasePacket {
    public S_GameTime(int time) {
        buildPacket(time);
    }

    private void buildPacket(int time) {
        writeC(L1Opcodes.S_OPCODE_GAMETIME);
        writeD(time);
    }
}
