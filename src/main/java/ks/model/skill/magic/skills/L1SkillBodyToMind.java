package ks.model.skill.magic.skills;

import ks.model.skill.magic.L1SkillRequest;

public class L1SkillBodyToMind extends L1SkillAdapter {

    public L1SkillBodyToMind(int skillId) {
        super(skillId);
    }

    @Override
    public void runSkill(L1SkillRequest request) {
        super.runSkill(request);

        request.getTargetCharacter().setCurrentMp(request.getTargetCharacter().getCurrentMp() + 2);
    }
}
