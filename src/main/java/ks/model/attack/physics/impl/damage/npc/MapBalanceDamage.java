package ks.model.attack.physics.impl.damage.npc;

import ks.core.datatables.balance.MapBalance;
import ks.core.datatables.balance.MapBalanceTable;
import ks.model.attack.physics.impl.L1AttackDamage;
import ks.model.attack.physics.impl.L1AttackDamageDecorator;
import ks.model.attack.physics.impl.vo.L1AttackParam;

public class MapBalanceDamage extends L1AttackDamageDecorator {
    public MapBalanceDamage(L1AttackDamage attackDamage) {
        super(attackDamage);
    }

    @Override
    public int calcDamage(L1AttackParam attackParam) {
        int damage = super.calcDamage(attackParam);

        MapBalance d = MapBalanceTable.getInstance().getData(getAttacker().getMapId());

        if (d != null) {
            damage *= d.getDmgLeverage();
        }

        return damage;
    }
}
