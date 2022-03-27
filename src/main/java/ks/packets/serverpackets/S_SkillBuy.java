package ks.packets.serverpackets;

import ks.core.network.opcode.L1Opcodes;
import ks.model.pc.L1PcInstance;

public class S_SkillBuy extends ServerBasePacket {
    public S_SkillBuy(L1PcInstance pc) {
        int count = count(pc);
        int inCount = 0;
        for (int k = 0; k < count; k++) {
            if (!pc.isSkillMastery((k + 1))) {
                inCount++;
            }
        }

        try {
            writeC(L1Opcodes.S_OPCODE_SKILLBUY);
            writeD(100);
            writeH(inCount);
            for (int k = 0; k < count; k++) {
                if (!pc.isSkillMastery((k + 1))) {
                    writeD(k);
                }
            }
        } catch (Exception e) {
            logger.error(e);
        }
    }

    public int count(L1PcInstance pc) {
        int rc = 0;

        switch (pc.getType()) {
            case 0:
                if (pc.getLevel() > 20 || pc.isGm()) {
                    rc = 16;
                } else if (pc.getLevel() > 10) {
                    rc = 8;
                }
                break;

            case 1:
                if (pc.getLevel() >= 50 || pc.isGm()) {
                    rc = 8;
                }
                break;

            case 2:
                if (pc.getLevel() >= 24 || pc.isGm()) {
                    rc = 23;
                } else if (pc.getLevel() >= 16) {
                    rc = 16;
                } else if (pc.getLevel() >= 8) {
                    rc = 8;
                }
                break;

            case 3: // WIZ
                if (pc.getLevel() >= 12 || pc.isGm()) {
                    rc = 23;
                } else if (pc.getLevel() >= 8) {
                    rc = 16;
                } else if (pc.getLevel() >= 4) {
                    rc = 8;
                }
                break;

            case 4: // DE
                if (pc.getLevel() >= 24 || pc.isGm()) {
                    rc = 16;
                } else if (pc.getLevel() >= 12) {
                    rc = 8;
                }
                break;

            default:
                break;
        }
        return rc;
    }
}
