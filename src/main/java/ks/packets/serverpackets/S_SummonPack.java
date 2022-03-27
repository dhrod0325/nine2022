package ks.packets.serverpackets;

import ks.core.network.opcode.L1Opcodes;
import ks.model.L1Character;
import ks.model.instance.L1SummonInstance;
import ks.model.pc.L1PcInstance;

public class S_SummonPack extends ServerBasePacket {
    private static final int STATUS_POISON = 1;

    public S_SummonPack(L1SummonInstance pet, L1PcInstance pc) {
        buildPacket(pet, pc);
    }

    private void buildPacket(L1SummonInstance pet, L1PcInstance pc) {
        if (pet == null) {
            return;
        }

        writeC(L1Opcodes.S_OPCODE_SHOWOBJ);
        writeH(pet.getX());
        writeH(pet.getY());
        writeD(pet.getId());
        writeH(pet.getGfxId().getGfxId());
        writeC(pet.getActionStatus());
        writeC(pet.getHeading());
        writeC(pet.getLight().getChaLightSize());
        writeC(pet.getMoveState().getMoveSpeed());

        writeD(0);
        writeH(0);
        writeS(pet.getNameId());
        writeS(pet.getTitle());

        int status = 0;

        if (pet.getPoison() != null) {
            if (pet.getPoison().getEffectId() == 1) {
                status |= STATUS_POISON;
            }
        }

        writeC(status);
        writeD(0);
        writeS(null);

        if (pet.isExsistMaster()) {
            L1Character master = pet.getMaster();

            if (master instanceof L1PcInstance) {
                writeS(((L1PcInstance) master).getHuntName());
            } else {
                writeS(master.getName());
            }
        } else {
            writeS("");
        }

        writeC(0);

        if (pet.getMaster() != null && pet.getMaster().getId() == pc.getId()) {
            int percent = pet.getMaxHp() != 0 ? 100 * pet.getCurrentHp() / pet.getMaxHp() : 100;
            writeC(percent);
        } else {
            writeC(0xFF);
        }

        writeC(0);
        writeC(pet.getLevel());
        writeC(0);
        writeC(0xFF);
        writeC(0xFF);
    }
}
