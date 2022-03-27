package ks.model.skill.magic.skills;

import ks.model.L1Character;
import ks.model.pc.L1PcInstance;
import ks.model.skill.magic.L1SkillRequest;

import static ks.constants.L1SkillId.STATUS_COMA_3;
import static ks.constants.L1SkillId.STATUS_COMA_5;

public class L1SkillStatusComa extends L1SkillAdapter {
    public L1SkillStatusComa(int skillId) {
        super(skillId);
    }

    @Override
    public void runSkill(L1SkillRequest request) {
        super.runSkill(request);
        statUp(request.getTargetCharacter(), 1);
    }

    @Override
    public void stopSkill(L1Character cha) {
        statUp(cha, -1);
    }

    private void statUp(L1Character cha, int type) {
        switch (skillId) {
            case STATUS_COMA_3: {
                L1PcInstance pc = (L1PcInstance) cha;

                pc.getAC().addAc(-3 * type);
                pc.addHitUp(3 * type);
                pc.addBowHitup(3 * type);
                pc.getAbility().addSp(3 * type);

                pc.getAbility().addAddedStr(5 * type);
                pc.getAbility().addAddedDex(5 * type);
                pc.getAbility().addAddedCon(type);
            }
            break;
            case STATUS_COMA_5: {
                L1PcInstance pc = (L1PcInstance) cha;

                pc.getAC().addAc(-8 * type);
                pc.addHitUp(5 * type);
                pc.addBowHitup(5 * type);
                pc.getAbility().addSp(5 * type);

                pc.getAbility().addAddedStr(5 * type);
                pc.getAbility().addAddedDex(5 * type);
                pc.getAbility().addAddedCon(type);
            }
            break;
        }
    }
}
