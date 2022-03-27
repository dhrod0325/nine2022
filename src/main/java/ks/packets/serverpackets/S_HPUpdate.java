package ks.packets.serverpackets;

import ks.core.network.opcode.L1Opcodes;
import ks.model.pc.L1PcInstance;
import ks.util.common.IntRange;

public class S_HPUpdate extends ServerBasePacket {
    private static final IntRange hpRange = new IntRange(1, 32767);

    public S_HPUpdate(int currentHp, int maxHp) {
        buildPacket(currentHp, maxHp);
    }

    public S_HPUpdate(L1PcInstance pc) {
        buildPacket(pc.getCurrentHp(), pc.getMaxHp());
    }

    public void buildPacket(int currentHp, int maxHp) {
        writeC(L1Opcodes.S_OPCODE_HPUPDATE);
        writeH(hpRange.ensure(currentHp));
        writeH(hpRange.ensure(maxHp));
    }
}
