package ks.packets.serverpackets;

import ks.core.network.opcode.L1Opcodes;

public class S_MPUpdate extends ServerBasePacket {
    public S_MPUpdate(int currentmp, int maxmp) {
        writeC(L1Opcodes.S_OPCODE_MPUPDATE);

        if (currentmp < 0) {
            writeH(0);
        } else writeH(Math.min(currentmp, 32767));

        if (maxmp < 1) {
            writeH(1);
        } else writeH(Math.min(maxmp, 32767));
    }
}
