package ks.packets.serverpackets;

import ks.core.network.opcode.L1Opcodes;

public class S_DelSkill extends ServerBasePacket {
    public S_DelSkill(int i, int j, int k, int l, int i1, int j1, int k1,
                      int l1, int i2, int j2, int k2, int l2, int i3, int j3, int k3,
                      int l3, int i4, int j4, int k4, int l4, int i5, int j5, int k5,
                      int l5, int dk3, int bw1, int bw2, int bw3) {
        int i6 = i1 + j1 + k1 + l1;
        int j6 = i2 + j2;

        writeC(L1Opcodes.S_OPCODE_DELSKILL);
        if (i6 > 0 && j6 == 0) {
            writeC(50);
        } else if (j6 > 0) {
            writeC(100);
        } else {
            writeC(22);
        }

        writeC(i);
        writeC(j);
        writeC(k);
        writeC(l);
        writeC(i1);
        writeC(j1);
        writeC(k1);
        writeC(l1);
        writeC(i2);
        writeC(j2);
        writeC(k2);
        writeC(l2);
        writeC(i3);
        writeC(j3);
        writeC(k3);
        writeC(l3);
        writeC(i4);
        writeC(j4);
        writeC(k4);
        writeC(l4);
        writeC(i5);
        writeC(j5);
        writeC(k5);
        writeC(l5);
        writeC(dk3);
        writeC(bw1);
        writeC(bw2);
        writeC(bw3);
        // writeD(0); // 신종족 스킬 구조 변경을 위해 주석처리
        writeD(0);
    }

}
