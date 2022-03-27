package ks.packets.serverpackets;

import ks.core.network.opcode.L1Opcodes;
import ks.model.L1Object;

public class S_RemoveObject extends ServerBasePacket {
    public S_RemoveObject(L1Object obj) {
        writeC(L1Opcodes.S_OPCODE_REMOVE_OBJECT);
        writeD(obj.getId());
    }

    public S_RemoveObject(int objId) {
        writeC(L1Opcodes.S_OPCODE_REMOVE_OBJECT);
        writeD(objId);
    }
}
