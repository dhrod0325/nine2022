package ks.packets.serverpackets;

import ks.core.network.opcode.L1Opcodes;
import ks.model.L1Character;
import ks.model.pc.L1PcInstance;


public class S_ChangeName extends ServerBasePacket {
    public S_ChangeName(int objectId, String name) {
        writeC(L1Opcodes.S_OPCODE_CHANGENAME);
        writeD(objectId);
        writeS(name);
    }

    public S_ChangeName(L1Character character) {
        if (character instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) character;
            writeC(L1Opcodes.S_OPCODE_CHANGENAME);
            writeD(pc.getId());
            writeS(pc.getHuntName());
        }

    }
}
