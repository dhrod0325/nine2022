package ks.packets.serverpackets;

import ks.core.network.opcode.L1Opcodes;
import ks.model.L1Buddy;

public class S_Buddy extends ServerBasePacket {
    public S_Buddy(int objId, L1Buddy buddy) {
        buildPacket(objId, buddy);
    }

    private void buildPacket(int objId, L1Buddy buddy) {
        writeC(L1Opcodes.S_OPCODE_SHOWHTML);
        writeD(objId);
        writeS("buddy");
        writeH(0x02);
        writeH(0x02);

        writeS(buddy.getBuddyListString());
        writeS(buddy.getOnlineBuddyListString());
    }
}
