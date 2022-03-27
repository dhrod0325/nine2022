package ks.packets.serverpackets;

import ks.core.network.opcode.L1Opcodes;

public class S_War extends ServerBasePacket {
    public S_War(int type, String clan_name1, String clan_name2) {
        buildPacket(type, clan_name1, clan_name2);
    }

    private void buildPacket(int type, String clan_name1, String clan_name2) {
        writeC(L1Opcodes.S_OPCODE_WAR);
        writeC(type);
        writeS(clan_name1);
        writeS(clan_name2);
    }
}
