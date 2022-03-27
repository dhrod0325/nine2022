package ks.model.skill.magic.skills;

import ks.model.L1Character;
import ks.model.skill.magic.L1SkillRequest;

public class L1SkillDisease extends L1SkillAdapter {

    public L1SkillDisease(int skillId) {
        super(skillId);
    }

    private void statUp(L1Character cha, int type) {
        cha.addDmgUp(-6 * type);
        cha.getAC().addAc(12 * type);
    }

    @Override
    public void runSkill(L1SkillRequest request) {
        super.runSkill(request);

        statUp(request.getTargetCharacter(), 1);
    }

    @Override
    public void stopSkill(L1Character targetCharacter) {
        statUp(targetCharacter, -1);
    }
}
