package ks.model.skill.magic.skills;

import ks.model.L1Character;
import ks.model.pc.L1PcInstance;
import ks.model.skill.magic.L1SkillRequest;

public class L1SkillFear extends L1SkillAdapter {
    public L1SkillFear(int skillId) {
        super(skillId);
    }

    private void statUp(L1Character targetCharacter, int type) {
        if (targetCharacter instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) targetCharacter;
            pc.addDg(-5 * type);
        }
    }

    @Override
    public void runSkill(L1SkillRequest request) {
        super.runSkill(request);
        statUp(request.getTargetCharacter(), 1);
    }

    @Override
    public void stopSkill(L1Character targetCharacter) {
        super.stopSkill(targetCharacter);
        statUp(targetCharacter, -1);
    }
}
