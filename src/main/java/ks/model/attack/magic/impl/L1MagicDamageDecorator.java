package ks.model.attack.magic.impl;

import ks.model.L1Character;

public class L1MagicDamageDecorator implements L1MagicDamage {
    private final L1MagicDamage magicDamage;

    public L1MagicDamageDecorator(L1MagicDamage magicDamage) {
        this.magicDamage = magicDamage;
    }

    @Override
    public int calcDamage(L1MagicParam magicParam) {
        return magicDamage.calcDamage(magicParam);
    }

    @Override
    public L1Character getTarget() {
        return magicDamage.getTarget();
    }

    @Override
    public L1Character getAttacker() {
        return magicDamage.getAttacker();
    }

    public L1MagicDamage getMagicDamage() {
        return magicDamage;
    }
}
