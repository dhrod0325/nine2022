package ks.model.attack.magic.impl.prob;

import ks.core.datatables.SkillsTable;
import ks.model.L1Skills;
import ks.model.attack.magic.impl.L1MagicProbability;
import ks.model.instance.L1NpcInstance;
import ks.model.pc.L1PcInstance;
import ks.util.common.random.RandomUtils;

import static ks.constants.L1SkillId.*;

public class L1MagicProbNpcToPc implements L1MagicProbability {
    private final L1NpcInstance attacker;

    private final L1PcInstance target;

    public L1MagicProbNpcToPc(L1NpcInstance attacker, L1PcInstance target) {
        this.attacker = attacker;
        this.target = target;
    }

    @Override
    public int calcProbability(int skillId) {
        L1Skills skill = SkillsTable.getInstance().getTemplate(skillId);

        int probability = 0;

        int attackLevel = attacker.getLevel();
        int attackInt = attacker.getAbility().getInt();

        int defenseLevel = target.getLevel();
        int defenseMr = target.getResistance().getEffectedMrBySkill();

        switch (skillId) {
            case MOB_RANGESTUN_18:
            case MOB_RANGESTUN_19:
            case MOB_RANGESTUN_30: {
                probability = new L1MagicProbNpcStun(attacker, target).calcProbability(skillId);

                break;
            }
            case ERASE_MAGIC:
            case EARTH_BIND:
            case ARMOR_BRAKE:
            case STRIKER_GALE:
            case ENTANGLE:
            case WIND_SHACKLE:
            case POLLUTE_WATER:
                probability = new L1MagicProbDiffLevel(attacker, target, 10, 25).calcProbability(skillId);
                break;
            case ELEMENTAL_FALL_DOWN:
            case RETURN_TO_NATURE:
                probability = 30 + (attackLevel - defenseLevel) * 2;

                if (skillId == ELEMENTAL_FALL_DOWN) {
                    probability += 10;
                }

                break;

            case COUNTER_BARRIER:// 카운터배리어고정 20%확률
                probability = 20;
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

                if (probability < 1) {
                    probability = 1;
                }

                if (probability > 90) {
                    probability = 90;
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

                if (probability > 95) {
                    probability = 95;
                }

                break;
            default: {
                int dice1 = skill.getProbabilityDice();
                int magicLevel = attacker.getAbility().getMagicLevel();
                int diceCount1 = attacker.getAbility().getMagicBonus() + magicLevel;

                if (diceCount1 < 1) {
                    diceCount1 = 1;
                }

                if (dice1 > 0) {
                    for (int i = 0; i < diceCount1; i++) {
                        probability += (RandomUtils.nextInt(dice1) + 1);
                    }
                }

                probability -= target.getResistance().getEffectedMrBySkill();
            }
            break;
        }

        probability += new L1MagicProbResistance(target).calcProbability(skillId);

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
                if (target.getResistance().getEffectedMrBySkill() >= 150) {
                    probability = 0;
                }

                if (skillId == MANA_DRAIN) {
                    if (probability > 65) {
                        probability = 65;
                    }
                }

                break;
            default:
                if (probability > 90) {
                    probability = 90;
                }

                break;
        }

        return probability;
    }
}
