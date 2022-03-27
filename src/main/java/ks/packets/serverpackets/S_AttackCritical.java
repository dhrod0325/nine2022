package ks.packets.serverpackets;

import ks.constants.L1ActionCodes;
import ks.core.network.opcode.L1Opcodes;
import ks.model.L1Character;
import ks.util.L1CommonUtils;

import java.util.concurrent.atomic.AtomicInteger;

public class S_AttackCritical extends ServerBasePacket {
    private static final AtomicInteger _sequentialNumber = new AtomicInteger(0);

    public S_AttackCritical(L1Character cha, int targetObj, int spellGfx, int x, int y) {
        int newHeading = L1CommonUtils.calcHeading(cha.getX(), cha.getY(), x, y);
        cha.setHeading(newHeading);

        int autoNum = _sequentialNumber.incrementAndGet();

        writeC(L1Opcodes.S_OPCODE_ATTACKPACKET);
        writeC(L1ActionCodes.ACTION_Attack);
        writeD(0);
        writeD(targetObj);
        writeC(1);
        writeC(0x00);
        writeC(newHeading);
        writeD(autoNum); // 번호가 겹치지 않게 보낸다
        writeH(spellGfx);
        writeC(0);
        writeH(x);
        writeH(y);
        writeH(x);
        writeH(y);
        writeD(0);
    }
}
