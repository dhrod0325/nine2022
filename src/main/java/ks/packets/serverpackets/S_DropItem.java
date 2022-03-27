package ks.packets.serverpackets;

import ks.core.network.opcode.L1Opcodes;
import ks.model.L1Item;
import ks.model.instance.L1ItemInstance;

public class S_DropItem extends ServerBasePacket {
    public S_DropItem(L1ItemInstance item) {
        buildPacket(item);
    }

    private void buildPacket(L1ItemInstance is) {
        L1Item item = is.getItem();

        writeC(L1Opcodes.S_OPCODE_SHOWOBJ);
        writeH(is.getX());
        writeH(is.getY());
        writeD(is.getId());
        writeH(item.getGroundGfxId());
        writeC(0);
        writeC(0);

        if (is.isNowLighting()) {
            writeC(item.getLightRange());
        } else {
            writeC(0);
        }
        writeC(0);
        writeD(is.getCount());
        writeC(0);
        writeC(0);

        writeS(is.getViewName());
        writeC(0);
        writeD(0);
        writeD(0);
        writeC(255);
        writeC(0);
        writeC(0);
        writeC(0);
        writeH(65535);
        writeD(0);
        writeC(8);
        writeC(0);
    }
}
