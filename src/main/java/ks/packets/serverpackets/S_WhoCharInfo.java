package ks.packets.serverpackets;

import ks.core.network.opcode.L1Opcodes;
import ks.model.pc.L1PcInstance;
import ks.util.L1CommonUtils;

public class S_WhoCharInfo extends ServerBasePacket {
    public S_WhoCharInfo(L1PcInstance target) {
        writeC(L1Opcodes.S_OPCODE_MSG);
        writeC(0x08);

        String s = L1CommonUtils.getWhoCharInfo(target);

        writeS(s);
        writeD(0);
    }
}
