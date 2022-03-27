package ks.model.skill.magic.skills;

import ks.model.attack.utils.L1MagicUtils;
import ks.model.pc.L1PcInstance;
import ks.model.skill.magic.L1SkillRequest;

public class L1SkillCounterDetection extends L1SkillAdapter {
    public L1SkillCounterDetection(int skillId) {
        super(skillId);
    }

    @Override
    public void runSkill(L1SkillRequest request) {
        super.runSkill(request);

        L1MagicUtils.detectionNpc(request.getTargetCharacter());
    }

    @Override
    public int interceptDamage(L1SkillRequest request, int dmg) {
        if (request.getTargetCharacter() instanceof L1PcInstance) {
            return request.getMagic().calcMagicDamage(request.getSkillId());
        }

        return super.interceptDamage(request, dmg);
    }

}
