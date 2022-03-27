package ks.model.attack.physics.impl;

import ks.model.L1Character;
import ks.model.attack.physics.impl.vo.L1AttackParam;

public class L1AttackHitUpDecorator implements L1AttackHitUp {
    private final L1AttackHitUp attackHitUp;

    public L1AttackHitUpDecorator(L1AttackHitUp attackHitUp) {
        this.attackHitUp = attackHitUp;
    }

    @Override
    public int calcHitUp(L1AttackParam attackParam) {
        return attackHitUp.calcHitUp(attackParam);
    }

    @Override
    public L1Character getAttacker() {
        return attackHitUp.getAttacker();
    }

    @Override
    public L1Character getTarget() {
        return attackHitUp.getTarget();
    }
}
