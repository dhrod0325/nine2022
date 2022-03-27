package ks.model.attack.physics.impl;

import ks.model.L1Character;
import ks.model.attack.physics.impl.vo.L1AttackParam;

public class L1AttackDamageDecorator implements L1AttackDamage {
    private final L1AttackDamage attackDamage;

    public L1AttackDamageDecorator(L1AttackDamage attackDamage) {
        this.attackDamage = attackDamage;
    }

    @Override
    public int calcDamage(L1AttackParam attackParam) {
        return attackDamage.calcDamage(attackParam);
    }

    @Override
    public L1Character getAttacker() {
        return attackDamage.getAttacker();
    }

    @Override
    public L1Character getTarget() {
        return attackDamage.getTarget();
    }
}
