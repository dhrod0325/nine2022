package ks.model.attack.magic.impl.prob;

import ks.core.datatables.SkillsTable;
import ks.model.L1Skills;
import ks.model.attack.magic.impl.L1MagicProbability;
import ks.model.attack.utils.L1MagicUtils;
import ks.model.pc.L1PcInstance;
import ks.util.common.NumberUtils;

import static ks.constants.L1SkillId.*;

public class L1MagicProbResistance implements L1MagicProbability {
    private final L1PcInstance target;

    public L1MagicProbResistance(L1PcInstance target) {
        this.target = target;
    }

    @Override
    public int calcProbability(int skillId) {
        L1Skills skill = SkillsTable.getInstance().getTemplate(skillId);

        int probability = 0;

        switch (skillId) {
            case EARTH_BIND:
                probability -= target.getResistance().getHold();
                break;
            case TRUE_TARGET:
            case BONE_BREAK:
            case SHOCK_STUN:
            case MOB_RANGESTUN_30:
            case 30081:
            case EMPIRE:
                probability -= target.getResistance().getStun();
                break;
            case FOG_OF_SLEEPING:
                probability -= target.getResistance().getSleep();
                break;
            case ICE_LANCE:
            case FREEZING_BLIZZARD:
                probability -= target.getResistance().getFreeze();
                break;
            case CURSE_BLIND:
            case DARKNESS:
                probability -= target.getResistance().getBlind();
                break;
            default:
                break;
        }

        //속성저항력
        double attrDefence = L1MagicUtils.calcAttrResistance(target, skill.getAttr());

        if (attrDefence > 0) {
            probability -= probability *= attrDefence;
        }

        if (NumberUtils.contains(skillId, ERASE_MAGIC, EARTH_BIND, AREA_OF_SILENCE, WIND_SHACKLE, POLLUTE_WATER, STRIKER_GALE)) {
            probability -= target.getResistance().getElf();
        }

        return probability;
    }
}
