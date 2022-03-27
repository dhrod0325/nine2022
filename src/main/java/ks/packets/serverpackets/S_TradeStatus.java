package ks.packets.serverpackets;

import ks.core.network.opcode.L1Opcodes;


public class S_TradeStatus extends ServerBasePacket {
    public S_TradeStatus(int type) {
        writeC(L1Opcodes.S_OPCODE_TRADESTATUS);
        writeC(type); // 0:거래 완료 1:거래 캔슬
    }
}
