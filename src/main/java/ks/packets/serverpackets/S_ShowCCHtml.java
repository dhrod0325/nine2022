package ks.packets.serverpackets;

import ks.core.network.opcode.L1Opcodes;

import java.util.List;

public class S_ShowCCHtml extends ServerBasePacket {
    public S_ShowCCHtml(int objId, String htmlId, List<?> params) {
        writeC(L1Opcodes.S_OPCODE_SHOWHTML);
        writeD(objId);
        writeS(htmlId);

        if (params.size() > 0) {
            writeH(0x01);
            writeH(params.size());

            for (Object var : params) {
                writeS(var + "");
            }
        }
    }

    public S_ShowCCHtml(int objId, String htmlId, Object... params) {
        writeC(L1Opcodes.S_OPCODE_SHOWHTML);
        writeD(objId);
        writeS(htmlId);

        if (params.length > 0) {
            writeH(0x01);
            writeH(params.length);

            for (Object var : params) {
                writeS(var + "");
            }
        }
    }
}
