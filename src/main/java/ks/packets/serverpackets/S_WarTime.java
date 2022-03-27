package ks.packets.serverpackets;

import ks.app.config.prop.ServerConfig;
import ks.core.network.opcode.L1Opcodes;


public class S_WarTime extends ServerBasePacket {
    public S_WarTime(int time) {
        writeC(L1Opcodes.S_OPCODE_WARTIME);
        writeH(6); // 리스트의 수(6이상은 무효)
        writeS(ServerConfig.SERVER_TIME_ZONE); // 시간의 뒤() 중에 표시되는 캐릭터 라인
        writeH(1);// 순번
        writeC(136);
        writeH(time);// 6:00
        writeH(2);// 순번
        writeC(178);
        writeH(time);// 6:30
        writeH(3);// 순번
        writeC(220);
        writeH(time);// 7:00
        writeH(4);// 순번
        writeC(218);
        writeH(time + 1);// 10:00
        writeH(5);// 순번
        writeC(4);
        writeH(time + 2);// 10:30
        writeH(6);// 순번
        writeC(46);// 11:00
        writeD(time + 2);
        writeC(0);
    }
}
