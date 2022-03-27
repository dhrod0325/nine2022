package ks.model.skill.magic.skills;

import ks.model.L1Character;
import ks.model.L1PolyMorph;
import ks.model.skill.magic.L1SkillRequest;
import ks.util.L1CommonUtils;

public class L1SkillShapeChange extends L1SkillAdapter {
    public L1SkillShapeChange(int skillId) {
        super(skillId);
    }

    @Override
    public void runSkill(L1SkillRequest request) {
        super.runSkill(request);
        L1CommonUtils.polyAction(request.getSkillUseCharacter(), request.getTargetCharacter());
    }

    @Override
    public void stopSkill(L1Character cha) {
        L1PolyMorph.undoPoly(cha);
    }
}
