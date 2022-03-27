package ks.packets.serverpackets;//드래곤진주

import ks.core.network.opcode.L1Opcodes;

public class S_DragonPerl extends ServerBasePacket {
    public S_DragonPerl(int objecId, int type) {
        writeC(L1Opcodes.S_OPCODE_DRAGONPERL);
        writeD(objecId);
        writeC(type);
    }
}
