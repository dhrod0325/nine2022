package ks.model.attack.magic.impl.damage.common;

import ks.core.datatables.SkillsTable;
import ks.model.L1Character;
import ks.model.L1Skills;
import ks.model.attack.magic.impl.L1MagicDamage;
import ks.model.attack.magic.impl.L1MagicParam;
import ks.util.common.random.RandomUtils;

public class L1MagicDamageDefault implements L1MagicDamage {
    private final L1Character attacker;

    private final L1Character target;

    public L1MagicDamageDefault(L1Character attacker, L1Character target) {
        this.attacker = attacker;
        this.target = target;
    }

    @Override
    public int calcDamage(L1MagicParam magicParam) {
        L1Skills skill = SkillsTable.getInstance().getTemplate(magicParam.getSkillId());

        int damage = 0;

        int damageDice = skill.getDamageDice();
        int diceCount = skill.getDamageDiceCount();
        int damageValue = skill.getDamageValue();

        for (int i = 0; i < diceCount; i++) {
            damage += RandomUtils.nextInt(damageDice) + 1;
        }

        damage += damageValue;

        return damage;
    }

    @Override
    public L1Character getTarget() {
        return target;
    }

    @Override
    public L1Character getAttacker() {
        return attacker;
    }
}
