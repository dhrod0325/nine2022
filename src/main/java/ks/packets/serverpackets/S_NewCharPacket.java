package ks.packets.serverpackets;

import ks.core.network.opcode.L1Opcodes;
import ks.model.pc.L1PcInstance;

public class S_NewCharPacket extends ServerBasePacket {
    public S_NewCharPacket(L1PcInstance pc) {
        buildPacket(pc);
    }

    private void buildPacket(L1PcInstance pc) {
        writeC(L1Opcodes.S_OPCODE_NEWCHARPACK);
        writeS(pc.getName());
        writeS("");
        writeC(pc.getType());
        writeC(pc.getSex());
        writeH(pc.getLawful());
        writeH(pc.getMaxHp());
        writeH(pc.getMaxMp());
        writeC(pc.getAC().getAc());
        writeC(pc.getLevel());
        writeC(pc.getAbility().getStr());
        writeC(pc.getAbility().getDex());
        writeC(pc.getAbility().getCon());
        writeC(pc.getAbility().getWis());
        writeC(pc.getAbility().getCha());
        writeC(pc.getAbility().getInt());
        writeC(0);
        writeD(pc.getBirthDay());

        int checkCode = pc.getLevel() ^ pc.getAbility().getStr() ^ pc.getAbility().getDex() ^ pc.getAbility().getCon() ^ pc.getAbility().getWis() ^ pc.getAbility().getCha() ^ pc.getAbility().getInt();

        writeC(checkCode & 0xff);
        writeD(0);
    }

}
