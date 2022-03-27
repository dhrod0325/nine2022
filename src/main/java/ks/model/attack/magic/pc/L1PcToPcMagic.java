package ks.model.attack.magic.pc;

import ks.model.attack.magic.impl.L1MagicAction;
import ks.model.attack.magic.impl.L1MagicAttack;
import ks.model.attack.magic.impl.L1MagicDamage;
import ks.model.attack.magic.impl.L1MagicProbability;
import ks.model.attack.magic.impl.action.L1MagicActionDefault;
import ks.model.attack.magic.impl.damage.common.*;
import ks.model.attack.magic.impl.damage.pc.L1MagicDamagePc;
import ks.model.attack.magic.impl.damage.pc.L1MagicDamagePcToPc;
import ks.model.attack.magic.impl.prob.L1MagicProbPcToPc;
import ks.model.pc.L1PcInstance;

public class L1PcToPcMagic implements L1MagicAttack {
    private final L1MagicProbability magicProbability;
    private final L1MagicDamage magicDamage;
    private final L1MagicAction magicAction;

    public L1PcToPcMagic(L1PcInstance attacker, L1PcInstance target) {
        magicProbability = new L1MagicProbPcToPc(attacker, target);
        magicDamage = new L1MagicDamageValidator(
                new L1MagicDamageCounterMirror(
                        new L1MagicDamageFinalBurn(
                                new L1MagicDamagePcToPc(
                                        new L1MagicDamageTargetPc(
                                                new L1MagicDamagePc(
                                                        new L1MagicDamageDefault(attacker, target)
                                                )
                                        )
                                )
                        )
                )
        );

        magicAction = new L1MagicActionDefault(attacker, target);
    }

    @Override
    public L1MagicProbability getProbability() {
        return magicProbability;
    }

    @Override
    public L1MagicDamage getDamage() {
        return magicDamage;
    }

    @Override
    public L1MagicAction getAction() {
        return magicAction;
    }
}