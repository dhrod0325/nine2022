package ks.model.skill.magic.skills;

import ks.model.L1Character;
import ks.model.skill.magic.L1SkillRequest;

public class L1SkillBerserkers extends L1SkillAdapter {
    public L1SkillBerserkers(int skillId) {
        super(skillId);
    }

    private void statUp(L1Character cha, int type) {
        cha.getAC().addAc(10 * type);
        cha.addDmgUp(5 * type);
        cha.addHitUp(2 * type);
    }

    @Override
    public void runSkill(L1SkillRequest request) {
        super.runSkill(request);
        statUp(request.getTargetCharacter(), 1);
    }

    @Override
    public void stopSkill(L1Character cha) {
        statUp(cha, -1);
    }
}
