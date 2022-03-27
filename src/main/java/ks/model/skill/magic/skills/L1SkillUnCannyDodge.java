package ks.model.skill.magic.skills;

import ks.model.L1Character;
import ks.model.pc.L1PcInstance;
import ks.model.skill.magic.L1SkillRequest;

public class L1SkillUnCannyDodge extends L1SkillAdapter {
    public L1SkillUnCannyDodge(int skillId) {
        super(skillId);
    }

    @Override
    public void runSkill(L1SkillRequest request) {
        super.runSkill(request);

        if (request.getTargetCharacter() instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) request.getTargetCharacter();
            pc.addDg(-5);
        }
    }

    @Override
    public void stopSkill(L1Character targetCharacter) {
        if (targetCharacter instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) targetCharacter;
            pc.addDg(5);
        }
    }
}
