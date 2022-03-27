package ks.model.attack.magic.impl.prob;

import ks.core.datatables.SkillsTable;
import ks.model.L1Skills;
import ks.model.attack.magic.impl.L1MagicProbability;
import ks.model.attack.utils.L1MagicUtils;
import ks.model.instance.L1NpcInstance;
import ks.model.instance.L1SummonInstance;
import ks.model.pc.L1PcInstance;
import ks.model.skill.utils.L1SkillUtils;
import ks.util.common.NumberUtils;
import ks.util.common.random.RandomUtils;
import ks.util.log.L1LogUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static ks.constants.L1SkillId.*;

public class L1MagicProbPcToNpc implements L1MagicProbability {
    private final Logger logger = LogManager.getLogger(getClass());

    private final L1PcInstance attacker;
    private final L1NpcInstance target;

    public L1MagicProbPcToNpc(L1PcInstance attacker, L1NpcInstance target) {
        this.attacker = attacker;
        this.target = target;
    }

    @Override
    public int calcProbability(int skillId) {
        L1Skills skill = SkillsTable.getInstance().getTemplate(skillId);

        int probability;

        int attackLevel = attacker.getLevel();
        int attackInt = attacker.getAbility().getTotalInt();
        int defenseLevel = target.getLevel();
        int defenseMr = target.getResistance().getEffectedMrBySkill();

        switch (skillId) {
            case BONE_BREAK:
            case SHOCK_STUN:
            case EMPIRE: {
                probability = new L1MagicProbPcStun(attacker, target).calcProbability(skillId);
                probability /= 4;

                break;
            }
            case ERASE_MAGIC:// 이레
            case STRIKER_GALE:// 게일
            case ENTANGLE:
            case WIND_SHACKLE:
            case POLLUTE_WATER:
                probability = skill.getProbabilityValue();
                probability += L1SkillUtils.calcProbability(attackLevel - defenseLevel, 10, 25);
                break;
            case AREA_OF_SILENCE:
                probability = skill.getProbabilityValue();
                probability += L1SkillUtils.calcProbability(attackLevel - defenseLevel, 10, 25);

                if (RandomUtils.nextInt(100) < probability) {
                    probability = 500;
                } else {
                    probability = 0;
                }

                return probability;
            case ELEMENTAL_FALL_DOWN:
            case RETURN_TO_NATURE:
                if (skillId == RETURN_TO_NATURE) {
                    if (target instanceof L1SummonInstance) {
                        L1SummonInstance summon = (L1SummonInstance) target;
                        defenseLevel = summon.getMaster().getLevel();
                    }
                }

                probability = 30 + (attackLevel - defenseLevel) * 2;

                if (skillId == ELEMENTAL_FALL_DOWN) {
                    probability += 10;
                }

                break;
            case CANCELLATION:
            case DECAY_POTION:
            case ICE_LANCE:
            case FOG_OF_SLEEPING:
            case SILENCE: //클래스케어
            case SLOW:
            case GRATE_SLOW:
            case DARKNESS: //클래스케어
            case WEAKNESS:
            case CURSE_POISON:
            case CURSE_BLIND: //클래스케어
            case WEAPON_BREAK: //클래스케어
            case MANA_DRAIN:
                if (attackInt > 30) {
                    attackInt = 30;
                }

                probability = (int) ((attackInt - (defenseMr / 5.24)) * 2) * skill.getProbabilityValue();
                probability += skill.getProbabilityDice();
                probability -= probability * L1MagicUtils.reduceDamageByMr(target.getResistance().getEffectedMrBySkill());
                probability += attacker.getTotalMagicHitUp();

                if (NumberUtils.contains(skillId, SILENCE, SLOW, GRATE_SLOW, DARKNESS, WEAKNESS, CURE_POISON, CURSE_BLIND, WEAPON_BREAK, FOG_OF_SLEEPING)) {
                    probability *= 0.1;
                }

                if (probability < 1) {
                    probability = 1;
                }

                if (probability > 95) {
                    probability = 95;
                }

                if (!attacker.isWizard()) {
                    probability /= 2;

                    if (attacker.getAbility().getTotalInt() < 20) {
                        probability = 0;
                    }
                }

                if (skillId == CANCELLATION) {
                    probability = 100;
                }

                break;
            case TURN_UNDEAD:
                if (attackInt > 45) {
                    attackInt = 45;
                }

                if (attackLevel > 60) {
                    attackLevel = 60;
                }

                probability = (int) ((attackInt * 2.6 + (attackLevel * 2.0)) - (defenseMr + (defenseLevel)) - 80);
                probability += skill.getProbabilityValue();

                probability += attacker.getTotalMagicHitUp();

                if (!attacker.isWizard()) {
                    probability /= 4;
                }

                if (probability > 95) {
                    probability = 95;
                }

                break;
            case TAMING_MONSTER:
                probability = new L1MagicProbDiffLevel(attacker, target, 1, 2).calcProbability(skillId);

                if (!target.getTemplate().isTamable()) {
                    probability = 0;
                } else {
                    double probabilityRevision = 1;

                    if ((target.getMaxHp() / 4) > target.getCurrentHp()) {
                        probabilityRevision = 1.3;
                    } else if ((target.getMaxHp() * 2 / 4) > target.getCurrentHp()) {
                        probabilityRevision = 1.2;
                    } else if ((target.getMaxHp() * 3 / 4) > target.getCurrentHp()) {
                        probabilityRevision = 1.1;
                    }

                    probability *= probabilityRevision;
                }

                break;
            default: {
                probability = new L1MagicProbDefaultPc(attacker, target).calcProbability(skillId);
            }

            break;
        }

        double attrDefence = L1MagicUtils.calcAttrResistance(target, skill.getAttr());

        if (attrDefence > 0) {
            probability -= probability *= attrDefence;
        }

        if (skill.getSkillId() == MOB_STUN_1) {
            if (skill.getProbabilityValue() < 5) {
                probability = skill.getProbabilityValue();
            }
        }

        switch (skillId) {
            case DECAY_POTION:
            case SILENCE:
            case SLOW:
            case DARKNESS:
            case WEAKNESS:
            case CURSE_POISON:
            case CURSE_BLIND:
            case WEAPON_BREAK:
            case MANA_DRAIN:
                if (target.getResistance().getMr() >= 150) {
                    probability = 0;
                }

                if (skillId == MANA_DRAIN) {
                    if (probability > 65) {
                        probability = 65;
                    }
                }

                break;
            default:
                if (probability > 95) {
                    probability = 95;
                }

                break;
        }

        L1LogUtils.gmLog(attacker, "스킬명 : {} 확률 : {}", skill.getName(), probability);

        return probability;
    }
}
