package ks.packets.serverpackets;

import ks.core.network.opcode.L1Opcodes;

public class S_ServerVersion extends ServerBasePacket {
    private static final int UPTIME = (int) (System.currentTimeMillis() / 1000L);

    public S_ServerVersion() {
        writeC(L1Opcodes.S_OPCODE_SERVERVERSION);

        writeC(0);
        writeC(0);
        writeD(0x734fd33);
        writeD(0x734fd30);
        writeD(0x77cf6eba);
        writeD(0x734fd31);
        writeD(0);
        writeC(0);
        writeC(0);
        writeC(0);
        writeD(0x77d82);
        writeD(UPTIME);
        writeH(0x01);
    }
}
