package ks.packets.serverpackets;

import ks.core.network.opcode.L1Opcodes;


public class S_Poison extends ServerBasePacket {

    /**
     * 캐릭터의 외관을 독상태에 변경할 때에 송신하는 패킷을 구축한다
     *
     * @param objId 외관을 바꾸는 캐릭터의 ID
     * @param type  외관의 타입 0 = 통상색, 1 = 녹색, 2 = 회색
     */
    public S_Poison(int objId, int type) {
        writeC(L1Opcodes.S_OPCODE_POISON);
        writeD(objId);

        if (type == 0) { // 통상
            writeC(0);
            writeC(0);
        } else if (type == 1) { // 녹색
            writeC(1);
            writeC(0);
        } else if (type == 2) { // 회색
            writeC(0);
            writeC(1);
        } else {
            throw new IllegalArgumentException("부정한 인수입니다. type = " + type);
        }
    }
}
