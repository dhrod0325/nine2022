package ks.packets.serverpackets;

import ks.core.network.opcode.L1Opcodes;

public class S_Weather extends ServerBasePacket {
    public S_Weather(int weather) {
        buildPacket(weather);
    }

    private void buildPacket(int weather) {
        writeC(L1Opcodes.S_OPCODE_WEATHER);
        writeC(weather);
    }
}
