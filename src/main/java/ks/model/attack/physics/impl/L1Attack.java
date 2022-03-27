package ks.model.attack.physics.impl;

public interface L1Attack {
    L1AttackHitUp getHitUp();

    L1AttackDamage getDamage();

    L1AttackAction getAction();

    L1AttackCommit getCommit();
}
