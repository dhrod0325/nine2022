package ks.packets.serverpackets;

import ks.core.network.opcode.L1Opcodes;
import ks.model.instance.L1SignboardInstance;

public class S_SignboardPack extends ServerBasePacket {
    private static final int STATUS_POISON = 1;

    public S_SignboardPack(L1SignboardInstance signboard) {
        writeC(L1Opcodes.S_OPCODE_SHOWOBJ);
        writeH(signboard.getX());
        writeH(signboard.getY());
        writeD(signboard.getId());
        writeH(signboard.getGfxId().getGfxId());
        writeC(0);
        writeC(getDirection(signboard.getHeading()));
        writeC(0);
        writeC(0);
        writeD(0);
        writeH(0);
        writeS(null);
        writeS(signboard.getName());
        int status = 0;
        if (signboard.getPoison() != null) {
            if (signboard.getPoison().getEffectId() == 1) {
                status |= STATUS_POISON;
            }
        }
        writeC(status);
        writeD(0);
        writeS(null);
        writeS(null);
        writeC(0);
        writeC(0xFF);
        writeC(0);
        writeC(0);
        writeC(0);
        writeC(0xFF);
        writeC(0xFF);
    }

    private int getDirection(int heading) {
        int dir = 0;
        switch (heading) {
            case 2:
                dir = 1;
                break;
            case 3:
                dir = 2;
                break;
            case 4:
                dir = 3;
                break;
            case 6:
                dir = 4;
                break;
            case 7:
                dir = 5;
                break;
        }
        return dir;
    }
}
