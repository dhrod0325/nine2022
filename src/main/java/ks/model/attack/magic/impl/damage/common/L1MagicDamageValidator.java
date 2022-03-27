package ks.model.attack.magic.impl.damage.common;

import ks.model.attack.magic.impl.L1MagicDamage;
import ks.model.attack.magic.impl.L1MagicDamageDecorator;
import ks.model.attack.magic.impl.L1MagicParam;
import ks.model.attack.utils.L1AttackUtils;

public class L1MagicDamageValidator extends L1MagicDamageDecorator {
    public L1MagicDamageValidator(L1MagicDamage magicDamage) {
        super(magicDamage);
    }

    @Override
    public int calcDamage(L1MagicParam magicParam) {
        if (L1AttackUtils.isNotHitAble(getAttacker(), getTarget())) {
            return 0;
        }

        if (L1AttackUtils.isNotAttackAbleByTargetStatus(getTarget())) {
            return 0;
        }

        if (!L1AttackUtils.isAttackAbleGhost(getAttacker(), getTarget())) {
            return 0;
        }

        return super.calcDamage(magicParam);
    }
}
