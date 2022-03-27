package ks.model.skill.magic.skills;

import ks.model.skill.magic.L1SkillRequest;
import ks.model.skill.utils.L1SkillUtils;

public class L1SkillDetection extends L1SkillAdapter {
    public L1SkillDetection(int skillId) {
        super(skillId);
    }

    @Override
    public void runSkill(L1SkillRequest request) {
        super.runSkill(request);

        L1SkillUtils.detection(request.getTargetCharacter());
    }
}
