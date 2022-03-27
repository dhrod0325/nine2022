package ks.model.skill.magic.skills;

import ks.model.L1Character;
import ks.model.pc.L1PcInstance;
import ks.model.skill.magic.L1SkillRequest;

public class L1SkillWeakNess extends L1SkillAdapter {

    public L1SkillWeakNess(int skillId) {
        super(skillId);
    }

    @Override
    public void runSkill(L1SkillRequest request) {
        super.runSkill(request);

        L1Character targetCharacter = request.getTargetCharacter();
        if (targetCharacter instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) targetCharacter;
            pc.addDmgUp(-5);
            pc.addHitUp(-1);
        }
    }

    @Override
    public void stopSkill(L1Character targetCharacter) {
        if (targetCharacter instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) targetCharacter;
            pc.addDmgUp(5);
            pc.addHitUp(1);
        }
    }
}
