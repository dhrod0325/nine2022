package ks.model.attack.magic.npc;

import ks.model.attack.magic.impl.L1MagicAction;
import ks.model.attack.magic.impl.L1MagicAttack;
import ks.model.attack.magic.impl.L1MagicDamage;
import ks.model.attack.magic.impl.L1MagicProbability;
import ks.model.attack.magic.impl.action.L1MagicActionDefault;
import ks.model.attack.magic.impl.damage.common.L1MagicDamageCounterMirror;
import ks.model.attack.magic.impl.damage.common.L1MagicDamageDefault;
import ks.model.attack.magic.impl.damage.common.L1MagicDamageFinalBurn;
import ks.model.attack.magic.impl.damage.common.L1MagicDamageValidator;
import ks.model.attack.magic.impl.damage.npc.L1MagicDamageNpc;
import ks.model.attack.magic.impl.damage.npc.L1MagicDamageNpcToNpc;
import ks.model.attack.magic.impl.prob.L1MagicProbNpcToNpc;
import ks.model.instance.L1NpcInstance;

public class L1NpcToNpcMagic implements L1MagicAttack {
    private final L1MagicProbability magicProbability;
    private final L1MagicDamage magicDamage;
    private final L1MagicAction magicAction;

    public L1NpcToNpcMagic(L1NpcInstance npc, L1NpcInstance target) {
        this.magicProbability = new L1MagicProbNpcToNpc(npc, target);
        this.magicDamage = new L1MagicDamageValidator(
                new L1MagicDamageCounterMirror(
                        new L1MagicDamageFinalBurn(
                                new L1MagicDamageNpcToNpc(
                                        new L1MagicDamageNpc(
                                                new L1MagicDamageDefault(npc, target)
                                        )
                                )
                        )
                )
        );

        this.magicAction = new L1MagicActionDefault(npc, target);
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
