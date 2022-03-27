package ks.packets.serverpackets;

import ks.core.network.opcode.L1Opcodes;

import java.util.List;

public class S_ClanAttention extends ServerBasePacket {
    public S_ClanAttention() {
        writeC(L1Opcodes.S_OPCODE_CLANATTENTION);
        writeD(2);
    }

    public S_ClanAttention(int i) {
        writeC(L1Opcodes.S_OPCODE_CLANATTENTION);
        writeH(i);
    }

    /**
     * 멘트
     */
    public S_ClanAttention(boolean onoff, String clanName) {
        writeC(L1Opcodes.S_OPCODE_CLANATTENTION);
        writeC(onoff ? 32 : 31);
        writeH(269);
        writeS(clanName);
    }

    public S_ClanAttention(int count, List<String> attentionList) {
        writeC(L1Opcodes.S_OPCODE_CLANATTENTION);
        writeH(2);
        writeD(count);
        for (String name : attentionList) {
            writeS(name);
        }
    }

    public S_ClanAttention(String name) {
        writeC(L1Opcodes.S_OPCODE_CLANATTENTION);
        writeH(2);
        writeD(1);
        writeS(name);
    }
}
