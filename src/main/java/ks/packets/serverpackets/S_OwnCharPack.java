package ks.packets.serverpackets;

import ks.core.network.opcode.L1Opcodes;
import ks.model.pc.L1PcInstance;

public class S_OwnCharPack extends ServerBasePacket {
    public S_OwnCharPack(L1PcInstance pc) {
        buildPacket(pc);
    }

    private void buildPacket(L1PcInstance pc) {
        writeC(L1Opcodes.S_OPCODE_SHOWOBJ);
        writeH(pc.getX());
        writeH(pc.getY());
        writeD(pc.getId());

        if (pc.isDead()) {
            writeH(pc.getTempCharGfxAtDead());
        } else {
            writeH(pc.getGfxId().getTempCharGfx());
        }

        if (pc.isDead()) {
            writeC(pc.getActionStatus());
        } else {
            writeC(pc.getCurrentWeapon());
        }

        writeC(pc.getHeading());
        writeC(pc.getLight().getOwnLightSize());
        writeC(pc.getMoveState().getMoveSpeed());
        writeD(pc.getExp());
        writeH(pc.getLawful());
        writeS(pc.getHuntName());
        writeS(pc.getTitle());
        writeC(pc.getStatus());

        if (pc.getClanId() > 0) {
            writeD(pc.getClan().getEmblemId());
            writeS(pc.getClanName());
        } else {
            writeD(0);
            writeS("");
        }

        writeS(null);
        writeC(0);

        if (pc.isInParty()) {
            writeC(100 * pc.getCurrentHp() / pc.getMaxHp());
        } else {
            writeC(0xFF);
        }

        writeC(pc.isThirdSpeed() ? 0x08 : 0x00);

        writeC(0);
        writeC(0);
        writeC(0xFF);
        writeC(0xFF);
    }
}