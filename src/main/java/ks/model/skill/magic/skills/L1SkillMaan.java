package ks.model.skill.magic.skills;

import ks.model.L1Character;
import ks.model.skill.magic.L1SkillRequest;

import static ks.constants.L1SkillId.*;

public class L1SkillMaan extends L1SkillAdapter {
    public L1SkillMaan(int skillId) {
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
        if (cha.getSkillEffectTimerSet().hasSkillEffect(skillId)) {
            cha.getSkillEffectTimerSet().removeSkillEffect(skillId);
        }

        switch (skillId) {
            case LIND_MAAN: {
                cha.getResistance().addSleep(15 * type);
                break;
            }

            case FAFU_MAAN: {
                cha.getResistance().addFreeze(15 * type);
                break;
            }

            case ANTA_MAAN: {
                cha.getResistance().addHold(15 * type);
                break;
            }

            case VALA_MAAN: {
                cha.getResistance().addStun(15 * type);
                break;
            }

            case BIRTH_MAAN: {
                cha.getResistance().addHold(15 * type);
                cha.getResistance().addFreeze(15 * type);
                break;
            }

            case SHAPE_MAAN: {
                shapeAndLife(cha, type);
                break;
            }

            case LIFE_MAAN: {
                shapeAndLife(cha, type);
                cha.getResistance().addStun(15 * type);
                break;
            }
        }
    }

    private void shapeAndLife(L1Character cha, int type) {
        cha.getResistance().addHold(15 * type);
        cha.getResistance().addFreeze(15 * type);
        cha.getResistance().addSleep(15 * type);
    }
}
