package ks.packets.serverpackets;

import ks.core.network.opcode.L1Opcodes;

public class S_SendLocation extends ServerBasePacket {
    public S_SendLocation(String senderName, int mapId, int x, int y, int msgId) {
        writeC(L1Opcodes.S_OPCODE_PACKETBOX);
        writeC(0x6f);
        writeS(senderName);
        writeH(mapId);
        writeH(x);
        writeH(y);
        writeC(msgId); // 發信者位在的地圖ID
    }
}
