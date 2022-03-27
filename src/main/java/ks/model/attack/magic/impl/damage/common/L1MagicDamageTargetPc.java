package ks.model.attack.magic.impl.damage.common;

import ks.model.L1Character;
import ks.model.attack.magic.impl.L1MagicDamage;
import ks.model.attack.magic.impl.L1MagicDamageDecorator;
import ks.model.attack.magic.impl.L1MagicParam;
import ks.model.attack.utils.L1DamageUtils;

public class L1MagicDamageTargetPc extends L1MagicDamageDecorator {
    public L1MagicDamageTargetPc(L1MagicDamage magicDamage) {
        super(magicDamage);
    }

    @Override
    public int calcDamage(L1MagicParam magicParam) {
        int dmg = super.calcDamage(magicParam);

        L1Character target = getTarget();

        return L1DamageUtils.targetReceiveDamage(target, dmg);
    }
}
