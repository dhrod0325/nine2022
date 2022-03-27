package ks.packets.serverpackets;

import ks.core.network.opcode.L1Opcodes;

public class S_DoActionGFX extends ServerBasePacket {
    public S_DoActionGFX(int objectId, int actionId) {
        writeC(L1Opcodes.S_OPCODE_DOACTIONGFX);
        writeD(objectId);
        writeC(actionId);
    }
}
