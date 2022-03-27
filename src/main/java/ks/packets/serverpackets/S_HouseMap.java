package ks.packets.serverpackets;

import ks.core.network.opcode.L1Opcodes;

public class S_HouseMap extends ServerBasePacket {
    public S_HouseMap(int objectId, String number) {
        buildPacket(objectId, number);
    }

    private void buildPacket(int objectId, String houseNumber) {
        int number = Integer.parseInt(houseNumber);

        writeC(L1Opcodes.S_OPCODE_HOUSEMAP);
        writeD(objectId);
        writeD(number);
    }
}
