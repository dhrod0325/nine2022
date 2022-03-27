package ks.model.attack.magic.impl.damage.npc;

import ks.app.config.prop.CodeConfig;
import ks.core.datatables.balance.MapBalance;
import ks.core.datatables.balance.MapBalanceTable;
import ks.model.L1Character;
import ks.model.attack.magic.impl.L1MagicDamage;
import ks.model.attack.magic.impl.L1MagicDamageDecorator;
import ks.model.attack.magic.impl.L1MagicParam;
import ks.model.attack.utils.L1MagicUtils;

public class L1MagicDamageNpc extends L1MagicDamageDecorator {
    public L1MagicDamageNpc(L1MagicDamage magicDamage) {
        super(magicDamage);
    }

    @Override
    public int calcDamage(L1MagicParam magicParam) {
        int damage = super.calcDamage(magicParam);

        L1Character attacker = getAttacker();

        int totalSp = attacker.getAbility().getSp();
        int totalInt = attacker.getAbility().getTotalInt();

        double coefficient = (1.0 + (totalInt - 8) * 0.2 + totalSp * 0.15);

        damage = (int) (damage * coefficient);

        MapBalance d = MapBalanceTable.getInstance().getData(getAttacker().getMapId());

        if (d != null) {
            damage *= d.getDmgLeverage();
        }

        damage -= damage * L1MagicUtils.reduceDamageByMr(getTarget().getResistance().getEffectedMrBySkill()) * CodeConfig.MAGIC_DMG_REDUCE_BY_MR;

        return damage;
    }
}
