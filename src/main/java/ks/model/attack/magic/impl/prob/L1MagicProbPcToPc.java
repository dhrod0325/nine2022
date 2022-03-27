package ks.model.attack.magic.impl.prob;

import ks.core.datatables.SkillsTable;
import ks.model.L1Skills;
import ks.model.attack.magic.impl.L1MagicProbability;
import ks.model.attack.utils.L1MagicUtils;
import ks.model.pc.L1PcInstance;
import ks.model.skill.magic.L1Skill;
import ks.model.skill.magic.L1SkillFactory;
import ks.model.skill.magic.skills.L1SkillShockStun;
import ks.model.skill.utils.L1SkillUtils;
import ks.util.L1CharPosUtils;
import ks.util.common.random.RandomUtils;
import ks.util.log.L1LogUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static ks.constants.L1SkillId.*;

public class L1MagicProbPcToPc implements L1MagicProbability {
    private final Logger logger = LogManager.getLogger(getClass());

    private final L1PcInstance attacker;
    private final L1PcInstance target;

    public L1MagicProbPcToPc(L1PcInstance attacker, L1PcInstance target) {
        this.attacker = attacker;
        this.target = target;
    }

    @Override
    public int calcProbability(int skillId) {
        L1Skills skill = SkillsTable.getInstance().getTemplate(skillId);

        int attackLevel = attacker.getLevel();
        int attackInt = attacker.getAbility().getTotalInt();

        int defenseLevel = target.getLevel();
        int defenseMr = target.getResistance().getEffectedMrBySkill();

        int probability;

        L1Skill runSkill = L1SkillFactory.create(skillId);

        if (runSkill instanceof L1SkillShockStun) {
            probability = new L1MagicProbPcStun(attacker, target).calcProbability(skillId);
        } else {
            switch (skillId) {
                case ERASE_MAGIC:// 이레
                case EARTH_BIND:// 어바
                case ARMOR_BRAKE:// 아머브레이크
                case STRIKER_GALE:// 게일
                case ENTANGLE:
                case WIND_SHACKLE:
                case POLLUTE_WATER:
                case AREA_OF_SILENCE:
                    probability = skill.getProbabilityValue();
                    probability += L1SkillUtils.calcProbability(attackLevel - defenseLevel, 25, 0);
                    break;
                case ELEMENTAL_FALL_DOWN:
                case RETURN_TO_NATURE:
                    probability = 30 + (attackLevel - defenseLevel) * 2;
                    if (skillId == ELEMENTAL_FALL_DOWN) {
                        probability += 10;
                    }

                    break;
                case CANCELLATION:
                case DECAY_POTION:
                case ICE_LANCE:
                case FOG_OF_SLEEPING:
                case SILENCE:
                case SLOW:
                case GRATE_SLOW:
                case DARKNESS:
                case WEAKNESS:
                case CURSE_POISON:
                case CURSE_BLIND: //클래스케어
                case WEAPON_BREAK: //클래스케어
                case MANA_DRAIN:
                    probability = skill.getProbabilityValue();

                    int t1 = attackInt / 4;
                    probability += t1;

                    int t2 = attacker.getTotalMagicHitUp() * 3;
                    probability += t2;

                    int t4 = (int) (probability * (L1MagicUtils.reduceDamageByMr(defenseMr) * 1.65));
                    probability -= t4;

                    if (probability < 0) {
                        probability = 0;
                    }

                    if (probability > 0) {
                        probability = RandomUtils.nextInt(0, probability);
                    }

                    if (probability > 90) {
                        probability = 90;
                    }

                    if (!attacker.isWizard()) {
                        probability /= 2;

                        if (attacker.getAbility().getTotalInt() < 20) {
                            probability = 0;
                        }
                    }

                    break;
                case CONFUSION:
                    probability = RandomUtils.nextInt(20, 30);
                    break;
                case THUNDER_GRAB:
                case HORROR_OF_DEATH:
                case GUARD_BREAK:
                case FEAR:
                case PHANTASM:
                case PANIC:
                case BONE_BREAK:
                    probability = new L1MagicProbDiffLevel(attacker, target, 1, 2).calcProbability(skillId);
                    break;
                default: {
                    probability = new L1MagicProbDefaultPc(attacker, target).calcProbability(skillId);
                }

                break;
            }
        }

        probability += new L1MagicProbResistance(target).calcProbability(skillId);

        if (probability > 95) {
            probability = 95;
        }

        if (L1CharPosUtils.isSafeZone(attacker) || L1CharPosUtils.isSafeZone(target)) {
            L1LogUtils.gmLog(attacker, "세이프존에서 디버프 확률마법 실패 : {}", target.getName());
            probability = 0;
        }

        L1LogUtils.gmLog(attacker, "스킬명 : {}, 확률 : {}", skill.getName(), probability);

        return probability;
    }
}
