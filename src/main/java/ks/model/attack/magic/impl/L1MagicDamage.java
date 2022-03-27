package ks.model.attack.magic.impl;

import ks.model.L1Character;

public interface L1MagicDamage {
    int calcDamage(L1MagicParam magicParam);

    L1Character getTarget();

    L1Character getAttacker();
}
