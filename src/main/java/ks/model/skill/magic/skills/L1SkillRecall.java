package ks.model.skill.magic.skills;

import ks.model.skill.magic.L1SkillRequest;
import ks.util.L1TeleportUtils;

public class L1SkillRecall extends L1SkillAdapter {
    public L1SkillRecall(int skillId) {
        super(skillId);
    }

    @Override
    public void runSkill(L1SkillRequest request) {
        super.runSkill(request);

        L1TeleportUtils.teleportToTargetFront(request.getTargetCharacter(), request.getSkillUseCharacter(), 1);
    }
}
