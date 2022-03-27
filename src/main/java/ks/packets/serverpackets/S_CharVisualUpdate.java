package ks.packets.serverpackets;

import ks.core.network.opcode.L1Opcodes;
import ks.model.pc.L1PcInstance;

public class S_CharVisualUpdate extends ServerBasePacket {
    public S_CharVisualUpdate(L1PcInstance pc) {
        writeC(L1Opcodes.S_OPCODE_CHARVISUALUPDATE);
        writeD(pc.getId());
        writeC(pc.getCurrentWeapon());
        writeC(0xff);
        writeC(0xff);

        pc.getAutoAttack().onVisualUpdate();
    }
}
