package ks.model.skill.magic.skills;

import ks.model.L1Character;
import ks.model.skill.magic.L1SkillRequest;

public class L1SkillResistMagic extends L1SkillAdapter {

    public L1SkillResistMagic(int skillId) {
        super(skillId);
    }

    private void statUp(L1Character cha, int type) {
        cha.getResistance().addMr(10 * type);
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
