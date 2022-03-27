package ks.model.attack.physics.pc;

import ks.model.attack.physics.impl.*;
import ks.model.attack.physics.impl.action.L1AttackActionPc;
import ks.model.attack.physics.impl.commit.L1AttackCommitDefault;
import ks.model.attack.physics.impl.damage.common.ImmuneReduceDamage;
import ks.model.attack.physics.impl.damage.pc.DefaultPcDamage;
import ks.model.attack.physics.impl.damage.pc.PcToNpcDamage;
import ks.model.attack.physics.impl.hitUp.DefaultHitUp;
import ks.model.attack.physics.impl.hitUp.PcToNpcHitUp;
import ks.model.attack.physics.impl.hitUp.TargetScarecrowHitUp;
import ks.model.instance.L1NpcInstance;
import ks.model.pc.L1PcInstance;

public class L1AttackPcToNpc implements L1Attack {
    private final L1PcInstance attacker;
    private final L1NpcInstance target;

    public L1AttackPcToNpc(L1PcInstance attacker, L1NpcInstance target) {
        this.attacker = attacker;
        this.target = target;
    }

    @Override
    public L1AttackHitUp getHitUp() {
        return new TargetScarecrowHitUp(
                new PcToNpcHitUp(
                        new DefaultHitUp(attacker, target)
                )
        );
    }

    @Override
    public L1AttackDamage getDamage() {
        return new ImmuneReduceDamage(new PcToNpcDamage(new DefaultPcDamage(attacker, target)));
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
