package ks.packets.serverpackets;

import ks.core.network.opcode.L1Opcodes;


public class S_Sound extends ServerBasePacket {
    public S_Sound(int sound) {
        buildPacket(sound);
    }

    private void buildPacket(int sound) {
        writeC(L1Opcodes.S_OPCODE_SOUND);
        writeC(0); // repeat
        writeH(sound);
    }
}
