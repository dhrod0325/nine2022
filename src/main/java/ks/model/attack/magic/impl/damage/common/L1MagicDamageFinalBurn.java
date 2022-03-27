package ks.model.attack.magic.impl.damage.common;

import ks.model.attack.magic.impl.L1MagicDamage;
import ks.model.attack.magic.impl.L1MagicDamageDecorator;
import ks.model.attack.magic.impl.L1MagicParam;

import static ks.constants.L1SkillId.FINAL_BURN;
import static ks.constants.L1SkillId.STATUS_FINAL_BURN;

public class L1MagicDamageFinalBurn extends L1MagicDamageDecorator {
    public L1MagicDamageFinalBurn(L1MagicDamage magicDamage) {
        super(magicDamage);
    }

    @Override
    public int calcDamage(L1MagicParam magicParam) {
        int damage = super.calcDamage(magicParam);

        if (magicParam.getSkillId() == FINAL_BURN) {
            damage = getAttacker().getCurrentMp();

            if (getTarget().getSkillEffectTimerSet().hasSkillEffect(STATUS_FINAL_BURN)) {
                damage *= 0.1;
            } else {
                getTarget().getSkillEffectTimerSet().setSkillEffect(STATUS_FINAL_BURN, 2000);
            }
        }

        return damage;
    }
}
