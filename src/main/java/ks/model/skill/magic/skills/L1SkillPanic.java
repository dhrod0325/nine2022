package ks.model.skill.magic.skills;

import ks.model.L1Character;
import ks.model.pc.L1PcInstance;
import ks.model.skill.magic.L1SkillRequest;

public class L1SkillPanic extends L1SkillAdapter {
    public L1SkillPanic(int skillId) {
        super(skillId);
    }

    private void statUp(L1Character target, int type) {
        target.getAbility().addAddedStr((byte) -1 * type);
        target.getAbility().addAddedDex((byte) -1 * type);
        target.getAbility().addAddedCon((byte) -1 * type);
        target.getAbility().addAddedInt((byte) -1 * type);
        target.getAbility().addAddedWis((byte) -1 * type);
        target.getAbility().addAddedCha((byte) -1 * type);

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
