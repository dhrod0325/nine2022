package ks.packets.serverpackets;

import ks.constants.L1SkillId;
import ks.core.network.opcode.L1Opcodes;
import ks.model.L1Character;

public class S_SPMR extends ServerBasePacket {
    public S_SPMR(L1Character character) {
        buildPacket(character);
    }

    private void buildPacket(L1Character character) {
        writeC(L1Opcodes.S_OPCODE_SPMR);

        int spmr = character.getAbility().getSp() - character.getAbility().getBonusSp();

        if (character.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.STATUS_WISDOM_POTION)) {
            writeC(spmr - 4);
        } else {
            writeC(spmr - 2);
        }

        writeC(character.getResistance().getMr() - character.getResistance().getBaseMr());
    }
}
