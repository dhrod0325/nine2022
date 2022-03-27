package ks.model.skill.magic.skills;

import ks.model.L1Character;
import ks.model.skill.magic.L1SkillRequest;

public class L1SkillIllusionLich extends L1SkillAdapter {
    public L1SkillIllusionLich(int skillId) {
        super(skillId);
    }

    @Override
    public void runSkill(L1SkillRequest request) {
        super.runSkill(request);

        L1Character target = request.getTargetCharacter();
        statUp(target, 1);
    }

    @Override
    public void stopSkill(L1Character target) {
        super.stopSkill(target);
        statUp(target, -1);
    }

    private void statUp(L1Character target, int type) {
        target.getAbility().addSp(2 * type);
    }
}
