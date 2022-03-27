package ks.model.skill.magic.skills;

import ks.model.L1Character;
import ks.model.attack.utils.L1MagicUtils;
import ks.model.pc.L1PcInstance;
import ks.model.skill.magic.L1SkillRequest;

public class L1SkillAbsoluteBarrier extends L1SkillAdapter {
    public L1SkillAbsoluteBarrier(int skillId) {
        super(skillId);
    }

    @Override
    public void runSkill(L1SkillRequest request) {
        L1Character cha = request.getTargetCharacter();

        if (cha instanceof L1PcInstance) {
            L1MagicUtils.startAbsoluteBarrier((L1PcInstance) cha, request.getDuration());
        }
    }

    @Override
    public void stopSkill(L1Character cha) {
        if (cha instanceof L1PcInstance) {
            L1MagicUtils.stopAbsoluteBarrier((L1PcInstance) cha);
        }
    }
}
