package ks.model.skill.magic.skills;

import ks.model.L1Character;
import ks.model.skill.magic.L1SkillRequest;
import ks.packets.serverpackets.S_OwnCharAttrDef;

public class L1SkillScalesEarthDragon extends L1SkillAdapter {
    public L1SkillScalesEarthDragon(int skillId) {
        super(skillId);
    }

    private void statUp(L1Character target, int type) {
        target.getAC().addAc(3 * type);
        target.getResistance().addHold(-10 * type);
        target.sendPackets(new S_OwnCharAttrDef(target));
    }

    @Override
    public void runSkill(L1SkillRequest request) {
        super.runSkill(request);

        statUp(request.getTargetCharacter(), -1);
    }

    @Override
    public void stopSkill(L1Character targetCharacter) {
        super.stopSkill(targetCharacter);

        statUp(targetCharacter, 1);
    }
}
