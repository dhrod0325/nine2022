package ks.model.skill.magic.skills;

import ks.model.L1Character;
import ks.model.skill.magic.L1SkillRequest;
import ks.packets.serverpackets.S_OwnCharAttrDef;

public class L1SkillResistElemental extends L1SkillAdapter {

    public L1SkillResistElemental(int skillId) {
        super(skillId);
    }

    private void statUp(L1Character cha, int type) {
        cha.getResistance().addAllNaturalResistance(10 * type);
    }

    @Override
    public void runSkill(L1SkillRequest request) {
        super.runSkill(request);

        statUp(request.getTargetCharacter(), 1);
        request.getTargetCharacter().sendPackets(new S_OwnCharAttrDef(request.getTargetCharacter()));
    }

    @Override
    public void stopSkill(L1Character cha) {
        statUp(cha, -1);
        cha.sendPackets(new S_OwnCharAttrDef(cha));
    }
}
