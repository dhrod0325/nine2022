package ks.packets.serverpackets;

import ks.core.network.opcode.L1Opcodes;
import ks.model.instance.L1FollowerInstance;
import ks.model.pc.L1PcInstance;

public class S_FollowerPack extends ServerBasePacket {
    private static final int STATUS_POISON = 1;

    public S_FollowerPack(L1FollowerInstance follower, L1PcInstance pc) {
        writeC(L1Opcodes.S_OPCODE_SHOWOBJ);
        writeH(follower.getX());
        writeH(follower.getY());
        writeD(follower.getId());
        writeH(follower.getGfxId().getGfxId());
        writeC(follower.getActionStatus());
        writeC(follower.getHeading());
        writeC(follower.getLight().getChaLightSize());
        writeC(follower.getMoveState().getMoveSpeed());
        writeD(0);
        writeH(0);
        writeS(follower.getNameId());
        writeS(follower.getTitle());
        int status = 0;
        if (follower.getPoison() != null) {
            if (follower.getPoison().getEffectId() == 1) {
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
        writeC(follower.getLevel());
        writeC(0);
        writeC(0xFF);
        writeC(0xFF);
    }
}
