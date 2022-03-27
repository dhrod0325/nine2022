package ks.model.attack.physics.impl.damage.npc;

import ks.model.L1Character;
import ks.model.attack.physics.impl.L1AttackDamage;
import ks.model.attack.physics.impl.vo.L1AttackParam;
import ks.model.attack.utils.L1AttackUtils;
import ks.model.instance.L1NpcInstance;
import ks.model.pc.L1PcInstance;
import ks.util.common.IntRange;
import ks.util.common.random.RandomUtils;

public class DefaultNpcDamage implements L1AttackDamage {
    private final L1NpcInstance attacker;
    private final L1Character target;

    public DefaultNpcDamage(L1NpcInstance attacker, L1Character target) {
        this.attacker = attacker;
        this.target = target;
    }

    @Override
    public int calcDamage(L1AttackParam attackParam) {
        int level = attacker.getLevel();

        double damage;

        if (level < 10) {
            damage = RandomUtils.nextInt(level) + 10D + attacker.getAbility().getTotalStr() + 1;
        } else {
            damage = RandomUtils.nextInt(level) + attacker.getAbility().getTotalStr() + 1;
        }

        int diffLevel = (level - target.getLevel()) * 2;
        int diffDmg = RandomUtils.nextInt(0, IntRange.ensure(diffLevel, 0, 10));

        damage += diffDmg;

        damage += attacker.getDmgUp();

        if (L1AttackUtils.isUndeadDamage(attacker)) {
            damage *= 1.5;
        }

        if (target instanceof L1PcInstance) {
            damage -= ((L1PcInstance) target).getTotalReduction();
        }

        damage = damage * attackParam.getLeverage() / 10;
        damage = attacker.onAttack(target, damage);

        L1AttackUtils.addNpcPoisonAttack(attacker, target);

        return (int) damage;
    }

    @Override
    public L1NpcInstance getAttacker() {
        return attacker;
    }

    @Override
    public L1Character getTarget() {
        return target;
    }
}
