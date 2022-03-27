package ks.model.skill.magic.skills;

import ks.model.L1Character;
import ks.model.skill.magic.L1SkillRequest;
import ks.packets.serverpackets.S_OwnCharAttrDef;

public class L1SkillElementalFallDown extends L1SkillAdapter {
    public L1SkillElementalFallDown(int skillId) {
        super(skillId);
    }

    private void statUp(L1Character targetCharacter, int type) {

        int i = -50 * type;

        int attr = targetCharacter.getAddAttrKind();

        switch (attr) {
            case 1:
                targetCharacter.getResistance().addEarth(i);
                break;
            case 2:
                targetCharacter.getResistance().addFire(i);
                break;
            case 4:
                targetCharacter.getResistance().addWater(i);
                break;
            case 8:
                targetCharacter.getResistance().addWind(i);
                break;
            default:
                break;
        }

        targetCharacter.setAddAttrKind(0);
        targetCharacter.sendPackets(new S_OwnCharAttrDef(targetCharacter));
    }

    @Override
    public void runSkill(L1SkillRequest request) {
        super.runSkill(request);

        statUp(request.getTargetCharacter(), 1);
    }

    @Override
    public void stopSkill(L1Character targetCharacter) {
        statUp(targetCharacter, -1);
    }
}
