package ks.model.skill.magic.skills;

import ks.model.L1Character;
import ks.model.pc.L1PcInstance;
import ks.model.skill.magic.L1SkillRequest;
import ks.packets.serverpackets.S_Sound;

public class L1SkillLight extends L1SkillAdapter {
    public L1SkillLight(int skillId) {
        super(skillId);
    }

    @Override
    public void stopSkill(L1Character targetCharacter) {
        super.stopSkill(targetCharacter);

        if (targetCharacter instanceof L1PcInstance) {
            if (!targetCharacter.isInvisible()) {
                L1PcInstance pc = (L1PcInstance) targetCharacter;
                pc.getLight().turnOnOffLight();
            }
        }
    }

    @Override
    public void runSkill(L1SkillRequest request) {
        super.runSkill(request);

        L1Character skillUseCharacter = request.getSkillUseCharacter();

        if (skillUseCharacter instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) skillUseCharacter;
            pc.getLight().turnOnOffLight();
        }
    }

    @Override
    public void sendGrfx(L1SkillRequest request, boolean isSkillAction) {
        super.sendGrfx(request, isSkillAction);
        if (!isSkillAction) {
            return;
        }
        L1Character targetCharacter = request.getTargetCharacter();

        if (targetCharacter instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) targetCharacter;
            pc.sendPackets(new S_Sound(145));
        }

    }
}
