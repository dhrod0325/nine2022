package ks.packets.serverpackets;

import ks.core.datatables.CharacterConfigTable;
import ks.core.network.opcode.L1Opcodes;

import java.util.Map;

public class S_CharacterConfig extends ServerBasePacket {
    public S_CharacterConfig(int objectId) {
        buildPacket(objectId);
    }

    private void buildPacket(int objectId) {
        Map<String, Object> o = CharacterConfigTable.getInstance().loadCharacterConfig(objectId);

        if (o == null) {
            o = CharacterConfigTable.getInstance().loadCharacterConfig(0);
        }

        if (o != null) {
            int length = (int) o.get("length");
            byte[] data = (byte[]) o.get("data");

            writeC(L1Opcodes.S_OPCODE_PACKETBOX);
            writeC(41);
            writeD(length);
            writeByte(data);
        }
    }
}