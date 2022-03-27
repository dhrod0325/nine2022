package ks.model.skill.magic.skills;

import ks.model.L1Character;
import ks.model.skill.magic.L1SkillRequest;

public class L1SkillHorrorOfDeath extends L1SkillAdapter {
    public L1SkillHorrorOfDeath(int skillId) {
        super(skillId);
    }

    private void statUp(L1Character target, int type) {
        target.getAbility().addAddedStr((byte) -10 * type);
        target.getAbility().addAddedInt((byte) -10 * type);
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
}
