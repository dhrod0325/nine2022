package ks.model.skill.magic.skills;

import ks.model.L1Character;
import ks.model.skill.magic.L1SkillRequest;

public class L1SkillMindBreak extends L1SkillAdapter {
    public L1SkillMindBreak(int skillId) {
        super(skillId);
    }

    @Override
    public void runSkill(L1SkillRequest request) {
        super.runSkill(request);

        L1Character target = request.getTargetCharacter();

        if (target.getCurrentMp() >= 5) {
            target.setCurrentMp(target.getCurrentMp() - 5);
            request.setDamage(28);
        }
    }
}
