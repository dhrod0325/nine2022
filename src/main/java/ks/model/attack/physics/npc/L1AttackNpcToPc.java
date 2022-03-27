package ks.model.attack.physics.npc;

import ks.model.attack.physics.impl.*;
import ks.model.attack.physics.impl.action.L1AttackActionNpc;
import ks.model.attack.physics.impl.commit.L1AttackCommitDefault;
import ks.model.attack.physics.impl.damage.common.ImmuneReduceDamage;
import ks.model.attack.physics.impl.damage.common.TargetPcDamage;
import ks.model.attack.physics.impl.damage.npc.DefaultNpcDamage;
import ks.model.attack.physics.impl.damage.npc.MapBalanceDamage;
import ks.model.attack.physics.impl.hitUp.DefaultHitUp;
import ks.model.attack.physics.impl.hitUp.MapBalanceHitUp;
import ks.model.instance.L1NpcInstance;
import ks.model.pc.L1PcInstance;

public class L1AttackNpcToPc implements L1Attack {
    private final L1NpcInstance attacker;
    private final L1PcInstance target;

    public L1AttackNpcToPc(L1NpcInstance attacker, L1PcInstance target) {
        this.attacker = attacker;
        this.target = target;
    }

    @Override
    public L1AttackHitUp getHitUp() {
        return new MapBalanceHitUp(new DefaultHitUp(attacker, target));
    }

    @Override
    public L1AttackDamage getDamage() {
        return new ImmuneReduceDamage(
                new TargetPcDamage(
                        new MapBalanceDamage(
                                new DefaultNpcDamage(attacker, target)
                        )
                )
        );
    }

    @Override
    public L1AttackAction getAction() {
        return new L1AttackActionNpc(attacker, target);
    }

    @Override
    public L1AttackCommit getCommit() {
        return new L1AttackCommitDefault(attacker, target);
    }
}
