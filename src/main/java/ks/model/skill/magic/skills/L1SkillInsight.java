package ks.model.skill.magic.skills;

import ks.model.L1Character;
import ks.model.pc.L1PcInstance;
import ks.model.skill.magic.L1SkillRequest;

public class L1SkillInsight extends L1SkillAdapter {
    public L1SkillInsight(int skillId) {
        super(skillId);
    }

    private void statUp(L1Character target, int type) {
        target.getAbility().addAddedStr(type);
        target.getAbility().addAddedDex(type);
        target.getAbility().addAddedCon(type);
        target.getAbility().addAddedInt(type);
        target.getAbility().addAddedWis(type);
        target.getAbility().addAddedCha(type);

        if (target instanceof L1PcInstance) {
            ((L1PcInstance) target).getPcExpManager().resetMr();
        }
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
