package ks.model.attack.magic.impl.damage.pc;

import ks.app.config.prop.CodeConfig;
import ks.constants.L1SkillId;
import ks.model.attack.magic.impl.L1MagicDamage;
import ks.model.attack.magic.impl.L1MagicDamageDecorator;
import ks.model.attack.magic.impl.L1MagicParam;
import ks.model.attack.utils.L1MagicUtils;
import ks.model.pc.L1PcInstance;

public class L1MagicDamagePcToPc extends L1MagicDamageDecorator {
    public L1MagicDamagePcToPc(L1MagicDamage magicDamage) {
        super(magicDamage);
    }

    @Override
    public int calcDamage(L1MagicParam magicParam) {
        int dmg = super.calcDamage(magicParam);

        L1PcInstance attacker = (L1PcInstance) getAttacker();
        L1PcInstance target = (L1PcInstance) getTarget();

        dmg -= dmg * L1MagicUtils.reduceDamageByMr(target.getResistance().getEffectedMrBySkill()) * CodeConfig.MAGIC_DMG_REDUCE_BY_MR;

        int reduction = target.getTotalReduction();

        if (attacker.getTotalIgnoreReduction() > 0) {
            reduction -= attacker.getTotalIgnoreReduction();

            if (reduction < 0) {
                reduction = 0;
            }
        }

        dmg -= reduction;

        if (target.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.IllUSION_AVATAR)) {
            dmg += dmg * 0.05;
        }

        return dmg;
    }
}
