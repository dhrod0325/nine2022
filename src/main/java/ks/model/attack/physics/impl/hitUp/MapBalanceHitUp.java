package ks.model.attack.physics.impl.hitUp;

import ks.core.datatables.balance.MapBalance;
import ks.core.datatables.balance.MapBalanceTable;
import ks.model.attack.physics.impl.L1AttackHitUp;
import ks.model.attack.physics.impl.L1AttackHitUpDecorator;
import ks.model.attack.physics.impl.vo.L1AttackParam;

public class MapBalanceHitUp extends L1AttackHitUpDecorator {
    public MapBalanceHitUp(L1AttackHitUp attackHitUp) {
        super(attackHitUp);
    }

    @Override
    public int calcHitUp(L1AttackParam attackParam) {
        int hitRate = super.calcHitUp(attackParam);

        hitRate += (getTarget().getAC().getAc() * 3) / 23;

        MapBalance d = MapBalanceTable.getInstance().getData(getAttacker().getMapId());

        if (d != null) {
            hitRate *= d.getHitLeverage();
        }

        return hitRate;
    }
}