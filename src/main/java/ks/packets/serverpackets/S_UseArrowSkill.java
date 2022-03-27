package ks.packets.serverpackets;

import ks.core.network.opcode.L1Opcodes;
import ks.model.L1Character;

import java.util.concurrent.atomic.AtomicInteger;


public class S_UseArrowSkill extends ServerBasePacket {
    private static final AtomicInteger sequentialNumber = new AtomicInteger(0);

    public S_UseArrowSkill(L1Character cha, int targetObj, int spellGfx, int x, int y, boolean isHit) {
        this(cha, targetObj, spellGfx, x, y, isHit, 1);
    }

    public S_UseArrowSkill(L1Character cha, int targetObj, int spellGfx, int x, int y, boolean isHit, int actId) {
        if (cha.getGfxId().getTempCharGfx() == 3860) {
            actId = 21;
        }

        writeC(L1Opcodes.S_OPCODE_ATTACKPACKET);
        writeC(actId);
        writeD(cha.getId());

        writeD(targetObj);

        if (isHit) {
            writeC(6);
        } else {
            writeC(0);
        }

        writeC(0x00);
        writeC(cha.getHeading());
        writeD(sequentialNumber.incrementAndGet());

        writeH(spellGfx);

        writeC(127);

        if (spellGfx != 13392) {
            writeH(cha.getX());
            writeH(cha.getY());
            writeH(x);
            writeH(y);
        } else {
            writeH(x);
            writeH(y);
            writeH(cha.getX());
            writeH(cha.getY());
        }

        writeC(0);
        writeC(0);
        writeC(0);
        writeC(0);
    }
}
