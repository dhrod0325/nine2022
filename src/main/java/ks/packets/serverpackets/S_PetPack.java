package ks.packets.serverpackets;

import ks.core.network.opcode.L1Opcodes;
import ks.model.L1Character;
import ks.model.instance.L1PetInstance;
import ks.model.pc.L1PcInstance;

public class S_PetPack extends ServerBasePacket {
    private static final int STATUS_POISON = 1;

    public S_PetPack(L1PetInstance pet, L1PcInstance pc) {
        buildPacket(pet, pc);
    }

    private void buildPacket(L1PetInstance pet, L1PcInstance pc) {
        writeC(L1Opcodes.S_OPCODE_SHOWOBJ);
        writeH(pet.getX());
        writeH(pet.getY());
        writeD(pet.getId());
        writeH(pet.getGfxId().getGfxId()); // SpriteID in List.spr
        writeC(pet.getActionStatus()); // Modes in List.spr
        writeC(pet.getHeading());
        writeC(pet.getLight().getChaLightSize()); // (Bright) - 0~15
        writeC(pet.getMoveState().getMoveSpeed()); // 스피드 - 0:normal, 1:fast,
        // 2:slow
        writeD(pet.getExp());
        writeH(pet.getTempLawful());
        writeS(pet.getName());
        writeS(pet.getTitle());
        int status = 0;
        if (pet.getPoison() != null) {
            if (pet.getPoison().getEffectId() == 1) {
                status |= STATUS_POISON;
            }
        }
        writeC(status);
        writeD(0); // ??
        writeS(null); // ??

        if (pet.getMaster() != null) {
            L1Character master = pet.getMaster();

            if (master instanceof L1PcInstance) {
                writeS(((L1PcInstance) master).getHuntName());
            } else {
                writeS(master.getName());
            }
        } else {
            writeS("");
        }

//        writeS(pet.getMaster() != null ? pet.getMaster().getName() : "");

        writeC(0); // ??
        // HP의 퍼센트
        if (pet.getMaster() != null && pet.getMaster().getId() == pc.getId()) {
            writeC(100 * pet.getCurrentHp() / pet.getMaxHp());
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
