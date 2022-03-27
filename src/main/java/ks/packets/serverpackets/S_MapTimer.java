package ks.packets.serverpackets;

import ks.constants.L1PacketBoxType;
import ks.core.network.opcode.L1Opcodes;
import ks.model.pc.L1PcInstance;

public class S_MapTimer extends ServerBasePacket {
    public S_MapTimer(L1PcInstance pc) {
        buildPacket(pc);
    }

    public void buildPacket(L1PcInstance pc) {
        int a = 0;
        int c = 0;
        int d = 0;
        int e = 0;

        writeC(L1Opcodes.S_OPCODE_PACKETBOX);
        writeC(L1PacketBoxType.MAP_TIMER_OUT);
        writeD(4);
        writeD(1);
        writeS(" ");
        writeD(a);

        writeD(2);
        writeS(" ");
        writeD(c);
        writeD(3);
        writeS(" ");
        writeD(d);
        writeD(6);
        writeS(" ");
        writeD(e);
    }
}