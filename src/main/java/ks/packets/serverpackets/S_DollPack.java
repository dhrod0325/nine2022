package ks.packets.serverpackets;

import ks.core.network.opcode.L1Opcodes;
import ks.model.instance.L1DollInstance;

public class S_DollPack extends ServerBasePacket {
    public S_DollPack(L1DollInstance pet) {
        writeC(L1Opcodes.S_OPCODE_SHOWOBJ);
        writeH(pet.getX());
        writeH(pet.getY());
        writeD(pet.getId());
        writeH(pet.getGfxId().getGfxId());
        writeC(pet.getActionStatus());
        writeC(pet.getHeading());
        writeC(0);
        writeC(pet.getMoveState().getMoveSpeed()); // ⅩΥ【Ι -
        writeD(0);
        writeH(0);
        writeS(pet.getNameId());
        writeS(pet.getTitle());
        writeC(0);
        writeD(0); // ??
        writeS(null); // ??
        writeS(pet.getMaster() != null ? pet.getMaster().getName() : "");
        writeC(0); // ??
        writeC(0xFF);
        writeC(0);
        writeC(pet.getLevel()); // PC = 0, Mon = Lv
        writeC(0);
        writeC(0xFF);
        writeC(0xFF);
    }
}
