package ks.model.attack.magic.impl.damage.npc;

import ks.core.datatables.balance.MapBalance;
import ks.core.datatables.balance.MapBalanceTable;
import ks.model.attack.magic.impl.L1MagicDamage;
import ks.model.attack.magic.impl.L1MagicDamageDecorator;
import ks.model.attack.magic.impl.L1MagicParam;
import ks.model.pc.L1PcInstance;

public class L1MagicDamageNpcToPc extends L1MagicDamageDecorator {
    public L1MagicDamageNpcToPc(L1MagicDamage magicDamage) {
        super(magicDamage);
    }

    @Override
    public int calcDamage(L1MagicParam magicParam) {
        int damage = super.calcDamage(magicParam);

        L1PcInstance target = (L1PcInstance) getTarget();

        damage -= target.getTotalReduction();

        MapBalance d = MapBalanceTable.getInstance().getData(getAttacker().getMapId());

        if (d != null) {
            damage *= d.getMagicDmgLeverage();
        }

        return damage;
    }
}
