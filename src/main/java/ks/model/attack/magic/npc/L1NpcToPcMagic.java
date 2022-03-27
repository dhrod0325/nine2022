package ks.model.attack.magic.npc;

import ks.model.attack.magic.impl.L1MagicAction;
import ks.model.attack.magic.impl.L1MagicAttack;
import ks.model.attack.magic.impl.L1MagicDamage;
import ks.model.attack.magic.impl.L1MagicProbability;
import ks.model.attack.magic.impl.action.L1MagicActionDefault;
import ks.model.attack.magic.impl.damage.common.*;
import ks.model.attack.magic.impl.damage.npc.L1MagicDamageNpc;
import ks.model.attack.magic.impl.damage.npc.L1MagicDamageNpcToPc;
import ks.model.attack.magic.impl.prob.L1MagicProbNpcToPc;
import ks.model.instance.L1NpcInstance;
import ks.model.pc.L1PcInstance;

public class L1NpcToPcMagic implements L1MagicAttack {
    private final L1MagicProbability magicProbability;
    private final L1MagicDamage magicDamage;
    private final L1MagicAction magicAction;

    public L1NpcToPcMagic(L1NpcInstance npc, L1PcInstance target) {
        this.magicProbability = new L1MagicProbNpcToPc(npc, target);
        this.magicDamage = new L1MagicDamageValidator(
                new L1MagicDamageCounterMirror(
                        new L1MagicDamageFinalBurn(
                                new L1MagicDamageNpcToPc(
                                        new L1MagicDamageTargetPc(
                                                new L1MagicDamageNpc(
                                                        new L1MagicDamageDefault(npc, target)
                                                )
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