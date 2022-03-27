package ks.packets.serverpackets;

import ks.constants.L1ActionCodes;
import ks.core.network.opcode.L1Opcodes;
import ks.model.instance.L1DoorInstance;

public class S_DoorPack extends ServerBasePacket {
    private static final int STATUS_POISON = 1;

    public S_DoorPack(L1DoorInstance door) {
        buildPacket(door);
    }

    private void buildPacket(L1DoorInstance door) {
        writeC(L1Opcodes.S_OPCODE_SHOWOBJ);
        writeH(door.getX());
        writeH(door.getY());
        writeD(door.getId());
        writeH(door.getGfxId().getGfxId());

        int doorStatus = door.getActionStatus();
        int openStatus = door.getOpenStatus();

        if (door.isDead())
            writeC(doorStatus);
        else if (openStatus == L1ActionCodes.ACTION_Open)
            writeC(openStatus);
        else if (door.getMaxHp() > 1 && doorStatus != 0)
            writeC(doorStatus);
        else
            writeC(openStatus);

        writeC(0);
        writeC(0);
        writeC(0);
        writeD(1);
        writeH(0);
        writeS(null);
        writeS(null);

        int status = 0;
        if (door.getPoison() != null) {
            if (door.getPoison().getEffectId() == 1) {
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
}
