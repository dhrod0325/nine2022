package ks.model.skill.magic.skills;

import ks.model.poison.L1DamagePoison;
import ks.model.skill.magic.L1SkillRequest;

public class L1SkillCursePoison extends L1SkillAdapter {
    public L1SkillCursePoison(int skillId) {
        super(skillId);
    }

    @Override
    public void runSkill(L1SkillRequest request) {
        super.runSkill(request);
        L1DamagePoison.doInfection(request.getSkillUseCharacter(), request.getTargetCharacter(), 3000, 5);
    }
}
