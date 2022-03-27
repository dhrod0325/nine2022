package ks.packets.serverpackets;

import ks.core.network.opcode.L1Opcodes;


public class S_SellHouse extends ServerBasePacket {
    public S_SellHouse(int objectId, String houseNumber) {
        buildPacket(objectId, houseNumber);
    }

    private void buildPacket(int objectId, String houseNumber) {
        writeC(L1Opcodes.S_OPCODE_INPUTAMOUNT);
        writeD(objectId);
        writeD(0); // ?
        writeD(100000); // 스핀 컨트롤의 초기 가격
        writeD(100000); // 가격의 하한
        writeD(2000000000); // 가격의 상한
        writeH(0); // ?
        writeS("agsell");
        writeS("agsell " + houseNumber);
    }
}
