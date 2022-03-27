package ks.model.skill.magic.skills;

import ks.model.L1Character;
import ks.model.L1Teleport;
import ks.model.pc.L1PcInstance;
import ks.model.skill.magic.L1SkillRequest;

public class L1SkillDrakeMassTeleport extends L1SkillAdapter {
    public L1SkillDrakeMassTeleport(int skillId) {
        super(skillId);
    }

    @Override
    public void runSkill(L1SkillRequest request) {
        super.runSkill(request);

        L1Character cha = request.getTargetCharacter();

        if (cha instanceof L1PcInstance) {
            L1PcInstance target = (L1PcInstance) cha;

            if (cha.isDead()) {
                setRunSkillState(STATUS_CONTINUE);
                return;
            }

            L1Teleport.teleport(target,
                    target.getX() + (int) (Math.random() * 5) - (int) (Math.random() * 5),
                    target.getY() + (int) (Math.random() * 5) - (int) (Math.random() * 5),
                    target.getMapId(), target.getHeading(), true);
        }

        setRunSkillState(STATUS_CONTINUE);
    }
}
