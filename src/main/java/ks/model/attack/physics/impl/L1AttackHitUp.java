package ks.model.attack.physics.impl;

import ks.model.L1Character;
import ks.model.attack.physics.impl.vo.L1AttackParam;

public interface L1AttackHitUp {
    int calcHitUp(L1AttackParam attackParam);

    L1Character getAttacker();

    L1Character getTarget();
}
