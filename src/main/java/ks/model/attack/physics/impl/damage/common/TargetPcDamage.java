package ks.model.attack.physics.impl.damage.common;

import ks.model.L1Character;
import ks.model.attack.physics.impl.L1AttackDamage;
import ks.model.attack.physics.impl.L1AttackDamageDecorator;
import ks.model.attack.physics.impl.vo.L1AttackParam;
import ks.model.attack.utils.L1DamageUtils;
import ks.model.instance.L1PetInstance;
import ks.model.instance.L1SummonInstance;

import static ks.constants.L1SkillId.IllUSION_AVATAR;

public class TargetPcDamage extends L1AttackDamageDecorator {
    public TargetPcDamage(L1AttackDamage attackDamage) {
        super(attackDamage);
    }

    @Override
    public int calcDamage(L1AttackParam attackParam) {
        int dmg = super.calcDamage(attackParam);

        dmg = L1DamageUtils.targetReceiveDamage(getTarget(), dmg);


        L1Character attacker = getAttacker();

        if (attacker instanceof L1PetInstance || attacker instanceof L1SummonInstance) {
            dmg /= 2;
        }

        if (getTarget().getSkillEffectTimerSet().hasSkillEffect(IllUSION_AVATAR)) {
            dmg *= 1.05;
        }

        return dmg;
    }
}
