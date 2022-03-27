package ks.packets.serverpackets;

import ks.core.network.opcode.L1Opcodes;

public class S_AddSkill extends ServerBasePacket {
    public S_AddSkill(int level1, int level2, int level3, int level4, int level5, int level6, int level7, int level8, int level9, int level10,
                      int knight1, int knight2,
                      int darkElf1, int darkElf2,
                      int royal1, int royal2,
                      int elf1, int elf2, int elf3, int elf4, int elf5, int elf6,
                      int dragonKnight1, int dragonKnight2, int dragonKnight3,
                      int darkMage1, int darkMage2, int darkMage3) {
        int i6 = level5 + level6 + level7 + level8;
        int j6 = level9 + level10;

        writeC(L1Opcodes.S_OPCODE_ADDSKILL);

        if (i6 > 0 && j6 == 0) {
            writeC(50);
        } else if (j6 > 0) {
            writeC(100);
        } else if (i6 == 0 && j6 == 0) {//
            writeC(32);//
        } else {
            writeC(22);
        }

        writeC(level1);
        writeC(level2);
        writeC(level3);
        writeC(level4);
        writeC(level5);
        writeC(level6);
        writeC(level7);
        writeC(level8);
        writeC(level9);
        writeC(level10);

        writeC(knight1);
        writeC(knight2);

        writeC(darkElf1);
        writeC(darkElf2);

        writeC(royal1);
        writeC(royal2);

        writeC(elf1);
        writeC(elf2);
        writeC(elf3);
        writeC(elf4);
        writeC(elf5);
        writeC(elf6);

        writeC(dragonKnight1);
        writeC(dragonKnight2);
        writeC(dragonKnight3);

        writeC(darkMage1);
        writeC(darkMage2);
        writeC(darkMage3);

        writeD(0);
    }
}
