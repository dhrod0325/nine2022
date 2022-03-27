package ks.packets.serverpackets;

import ks.core.network.opcode.L1Opcodes;
import ks.util.L1CommonUtils;

public class S_ChangeShape extends ServerBasePacket {
    public S_ChangeShape(int objId, int polyId) {
        buildPacket(objId, polyId, false);
    }

    public S_ChangeShape(int objId, int polyId, boolean weaponTakeoff) {
        buildPacket(objId, polyId, weaponTakeoff);
    }

    private void buildPacket(int objId, int polyId, boolean weaponTakeoff) {
        writeC(L1Opcodes.S_OPCODE_POLY);
        writeD(objId);

        writeH(L1CommonUtils.changeGfx(polyId));

        writeH(weaponTakeoff ? 0 : 29);
    }
}
