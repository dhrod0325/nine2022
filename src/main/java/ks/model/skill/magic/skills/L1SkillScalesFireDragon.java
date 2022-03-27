package ks.model.skill.magic.skills;

import ks.model.L1Character;
import ks.model.skill.magic.L1SkillRequest;

public class L1SkillScalesFireDragon extends L1SkillAdapter {
    public L1SkillScalesFireDragon(int skillId) {
        super(skillId);
    }

    private void statUp(L1Character target, int type) {
        target.getResistance().addStun(10 * type);
        target.addHitUp(5 * type);
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
