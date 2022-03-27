package ks.packets.serverpackets;

import ks.core.network.opcode.L1Opcodes;
import ks.model.L1Character;

public class S_HPMeter extends ServerBasePacket {
    public S_HPMeter(int objId, int hpRatio) {
        buildPacket(objId, hpRatio);
    }

    public S_HPMeter(L1Character cha) {
        int objId = cha.getId();
        int hpRatio = 100;

        if (0 < cha.getMaxHp()) {
            hpRatio = 100 * cha.getCurrentHp() / cha.getMaxHp();
        }

        buildPacket(objId, hpRatio);
    }

    private void buildPacket(int objId, int hpRatio) {
        writeC(L1Opcodes.S_OPCODE_HPMETER);
        writeD(objId);
        writeC(hpRatio);
        writeH(0);
    }
}
