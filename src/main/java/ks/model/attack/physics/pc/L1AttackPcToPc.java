package ks.model.attack.physics.pc;

import ks.model.attack.physics.impl.*;
import ks.model.attack.physics.impl.action.L1AttackActionPc;
import ks.model.attack.physics.impl.commit.L1AttackCommitDefault;
import ks.model.attack.physics.impl.damage.common.ImmuneReduceDamage;
import ks.model.attack.physics.impl.damage.common.TargetPcDamage;
import ks.model.attack.physics.impl.damage.pc.DefaultPcDamage;
import ks.model.attack.physics.impl.damage.pc.PcToPcDamage;
import ks.model.attack.physics.impl.hitUp.DefaultHitUp;
import ks.model.attack.physics.impl.hitUp.PcToPcHitUp;
import ks.model.pc.L1PcInstance;

public class L1AttackPcToPc implements L1Attack {
    private final L1PcInstance attacker;
    private final L1PcInstance target;

    public L1AttackPcToPc(L1PcInstance attacker, L1PcInstance target) {
        this.attacker = attacker;
        this.target = target;
    }

    @Override
    public L1AttackHitUp getHitUp() {
        return new PcToPcHitUp(
                new DefaultHitUp(attacker, target)
        );
    }

    @Override
    public L1AttackDamage getDamage() {
        return new ImmuneReduceDamage(
                new TargetPcDamage(
                        new PcToPcDamage(
                                new DefaultPcDamage(attacker, target))
                )
        );
    }

    @Override
    public L1AttackAction getAction() {
        return new L1AttackActionPc(attacker, target);
    }

    @Override
    public L1AttackCommit getCommit() {
        return new L1AttackCommitDefault(attacker, target);
    }
}
