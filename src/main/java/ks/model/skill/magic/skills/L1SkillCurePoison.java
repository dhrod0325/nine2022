package ks.model.skill.magic.skills;

import ks.model.skill.magic.L1SkillRequest;

public class L1SkillCurePoison extends L1SkillAdapter {
    public L1SkillCurePoison(int skillId) {
        super(skillId);
    }

    @Override
    public void runSkill(L1SkillRequest request) {
        super.runSkill(request);
        request.getTargetCharacter().curePoison();
    }
}
