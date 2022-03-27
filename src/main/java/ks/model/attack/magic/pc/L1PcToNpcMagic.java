package ks.model.attack.magic.pc;

import ks.model.attack.magic.impl.L1MagicAction;
import ks.model.attack.magic.impl.L1MagicAttack;
import ks.model.attack.magic.impl.L1MagicDamage;
import ks.model.attack.magic.impl.L1MagicProbability;
import ks.model.attack.magic.impl.action.L1MagicActionDefault;
import ks.model.attack.magic.impl.damage.common.L1MagicDamageCounterMirror;
import ks.model.attack.magic.impl.damage.common.L1MagicDamageDefault;
import ks.model.attack.magic.impl.damage.common.L1MagicDamageFinalBurn;
import ks.model.attack.magic.impl.damage.common.L1MagicDamageValidator;
import ks.model.attack.magic.impl.damage.pc.L1MagicDamagePc;
import ks.model.attack.magic.impl.damage.pc.L1MagicDamagePcToNpc;
import ks.model.attack.magic.impl.prob.L1MagicProbPcToNpc;
import ks.model.instance.L1NpcInstance;
import ks.model.pc.L1PcInstance;

public class L1PcToNpcMagic implements L1MagicAttack {
    private final L1MagicProbability magicProbability;
    private final L1MagicDamage magicDamage;
    private final L1MagicAction magicAction;

    public L1PcToNpcMagic(L1PcInstance attacker, L1NpcInstance target) {
        this.magicProbability = new L1MagicProbPcToNpc(attacker, target);
        this.magicDamage = new L1MagicDamageValidator(
                new L1MagicDamageCounterMirror(
                        new L1MagicDamageFinalBurn(
                                new L1MagicDamagePcToNpc(
                                        new L1MagicDamagePc(
                                                new L1MagicDamageDefault(attacker, target)
                                        )
                                )
                        )
                )
        );
        
        this.magicAction = new L1MagicActionDefault(attacker, target);
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
