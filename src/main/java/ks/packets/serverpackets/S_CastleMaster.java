package ks.packets.serverpackets;

import ks.core.network.opcode.L1Opcodes;

public class S_CastleMaster extends ServerBasePacket {
    public S_CastleMaster(int type, int objecId) {
        buildPacket(type, objecId);
    }

    private void buildPacket(int type, int objecId) {
        writeC(L1Opcodes.S_OPCODE_CASTLEMASTER);
        writeC(type);
        writeD(objecId);
    }
}
