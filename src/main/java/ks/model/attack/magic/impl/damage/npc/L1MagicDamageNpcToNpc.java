package ks.model.attack.magic.impl.damage.npc;

import ks.model.attack.magic.impl.L1MagicDamage;
import ks.model.attack.magic.impl.L1MagicDamageDecorator;
import ks.model.attack.magic.impl.L1MagicParam;

public class L1MagicDamageNpcToNpc extends L1MagicDamageDecorator {
    public L1MagicDamageNpcToNpc(L1MagicDamage magicDamage) {
        super(magicDamage);
    }

    @Override
    public int calcDamage(L1MagicParam magicParam) {
        return super.calcDamage(magicParam);
    }
}
