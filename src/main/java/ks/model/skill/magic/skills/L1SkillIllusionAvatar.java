package ks.model.skill.magic.skills;

import ks.model.L1Character;
import ks.model.skill.magic.L1SkillRequest;

public class L1SkillIllusionAvatar extends L1SkillAdapter {
    public L1SkillIllusionAvatar(int skillId) {
        super(skillId);
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

    private void statUp(L1Character targetCharacter, int type) {
        targetCharacter.addDmgUp(10 * type);
        targetCharacter.addBowDmgUp(10 * type);
    }
}
