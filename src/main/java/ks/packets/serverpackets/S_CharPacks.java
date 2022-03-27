package ks.packets.serverpackets;

import ks.core.network.opcode.L1Opcodes;

public class S_CharPacks extends ServerBasePacket {
    public S_CharPacks(String name, String clanName, int type, int sex, int lawful, int hp, int mp, int ac, int lv, int str, int dex, int con, int wis, int cha, int intel, int accessLevel, int birthday) {
        writeC(L1Opcodes.S_OPCODE_CHARLIST);

        writeS(name);

        writeS(clanName);
        writeC(type);
        writeC(sex);
        writeH(lawful);
        writeH(hp);
        writeH(mp);
        writeC(ac);
        writeC(lv);
        writeC(str);
        writeC(dex);
        writeC(con);
        writeC(wis);
        writeC(cha);
        writeC(intel);
        writeC(0);
        writeD(birthday);

        int checkCode = lv ^ str ^ dex ^ con ^ wis ^ cha ^ intel;
        writeC(checkCode & 0xFF);
    }
}
