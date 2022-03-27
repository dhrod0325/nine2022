package ks.packets.serverpackets;

import ks.core.network.opcode.L1Opcodes;
import ks.model.pc.L1PcInstance;

public class S_ShowPolyList extends ServerBasePacket {
    public S_ShowPolyList(int charId, L1PcInstance pc) {
        writeC(L1Opcodes.S_OPCODE_SHOWHTML);
        writeD(charId);
        writeS("monlist");

        if (pc.getFavPolyImgList().size() > 0) {
            writeH(0x01);
            writeH(pc.getFavPolyImgList().size());

            for (Object var : pc.getFavPolyImgList()) {
                writeS(var + "");
            }
        }
    }

    public S_ShowPolyList(int charId, String str) {
        writeC(L1Opcodes.S_OPCODE_SHOWHTML);
        writeD(charId);
        writeS(str);
    }
}
