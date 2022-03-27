package ks.model.attack.physics.impl.hitUp;

import ks.model.L1Character;
import ks.model.attack.physics.impl.L1AttackHitUp;
import ks.model.attack.physics.impl.vo.L1AttackParam;
import ks.model.attack.utils.L1AttackUtils;
import ks.model.instance.L1PetInstance;
import ks.model.instance.L1SummonInstance;
import ks.util.L1CharPosUtils;
import ks.util.common.IntRange;
import ks.util.common.random.RandomUtils;

import static ks.constants.L1SkillId.*;

public class DefaultHitUp implements L1AttackHitUp {
    private final L1Character attacker;
    private final L1Character target;

    public DefaultHitUp(L1Character attacker, L1Character target) {
        this.attacker = attacker;
        this.target = target;
    }

    @Override
    public L1Character getAttacker() {
        return attacker;
    }

    @Override
    public L1Character getTarget() {
        return target;
    }

    @Override
    public int calcHitUp(L1AttackParam attackParam) {
        if (attacker instanceof L1PetInstance || attacker instanceof L1SummonInstance) {
            if (L1CharPosUtils.isSafeZone(target)) {
                return 0;
            }
        }

        if (L1AttackUtils.isNotHitAble(attacker, target)) {
            if (attackParam.isHitCheck()) {
                return 0;
            }
        }

        if (!attacker.getLocation().isInScreen(target.getLocation())) {
            return 0;
        }

        int attackerLevel = attacker.getLevel();

        int hitRate = attackerLevel;

        if (!attacker.isLongAttack()) {
            hitRate += attacker.getTotalHitUp();
        } else {
            hitRate += attacker.getTotalBowHitUp();
            if (target.getSkillEffectTimerSet().hasSkillEffect(STRIKER_GALE)) {
                hitRate *= 4;
            }
        }

        int targetLevel = target.getLevel();
        int levDiffRate = (attackerLevel - targetLevel);

        hitRate += IntRange.ensure(levDiffRate, -5, 5);

        if (getAttacker() instanceof L1PetInstance) {
            L1PetInstance pet = (L1PetInstance) getAttacker();

            hitRate += pet.getLevel() / 10;
            hitRate += pet.getHitByWeapon();
        } else {
            hitRate += attackerLevel / 3;//3레벨당 명중 보너스
        }

        if (!attacker.isLongAttack() && target.getSkillEffectTimerSet().hasSkillEffect(UNCANNY_DODGE)) {
            hitRate -= L1AttackUtils.missByUncannyDodge(target);
        }

        hitRate = IntRange.ensure(hitRate, 2, 150);

        attackParam.setHitRate(hitRate);

        int targetAc = target.getAC().getAc();

        int defenderValue = (int) (targetAc * 1.5) * -1;

        int defenderDice;

        if (targetAc >= 0) {
            defenderDice = 10 - targetAc;
        } else {
            defenderDice = 10 + RandomUtils.nextInt(defenderValue) + 1;
        }

        int attackerDice = RandomUtils.nextInt(1, 15) + hitRate - 10;

        if (target.getSkillEffectTimerSet().hasSkillEffect(MIRROR_IMAGE)) {
            attackerDice -= 5.5;
        }
        if (target.getSkillEffectTimerSet().hasSkillEffect(FEAR)) {
            attackerDice += 4.5;
        }

        int fumble = hitRate - 9;
        int critical = hitRate + 10;

        if (attackerDice <= fumble) {
            hitRate = 0;
        } else if (attackerDice >= critical) {
            hitRate = 100;
        } else {
            if (attackerDice > defenderDice) {
                hitRate = 100;
            } else if (attackerDice <= defenderDice) {
                hitRate = 0;
            }
        }

        return IntRange.ensure(hitRate, 0, 100);
    }
}
