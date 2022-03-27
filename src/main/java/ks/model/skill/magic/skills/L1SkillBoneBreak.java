package ks.model.skill.magic.skills;

import ks.model.L1Character;
import ks.model.skill.magic.L1SkillRequest;
import ks.util.L1StatusUtils;

public class L1SkillBoneBreak extends L1SkillAdapter {
    public L1SkillBoneBreak(int skillId) {
        super(skillId);
    }

    @Override
    public void runSkill(L1SkillRequest request) {
        super.runSkill(request);

        L1Character attacker = request.getSkillUseCharacter();
        L1Character target = request.getTargetCharacter();

        L1StatusUtils.shockStun(target, 2000);
    }
}
