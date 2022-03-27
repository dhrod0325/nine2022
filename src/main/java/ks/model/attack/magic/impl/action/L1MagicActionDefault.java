package ks.model.attack.magic.impl.action;

import ks.model.L1Character;
import ks.model.attack.magic.impl.L1MagicAction;
import ks.model.attack.magic.impl.action.vo.L1MagicActionVo;
import ks.model.attack.utils.L1AttackUtils;
import ks.model.instance.extend.ReceiveDamageAble;

public class L1MagicActionDefault implements L1MagicAction {
    private final L1Character attacker;

    private final L1Character target;

    public L1MagicActionDefault(L1Character attacker, L1Character target) {
        this.attacker = attacker;
        this.target = target;
    }

    @Override
    public void commit(L1MagicActionVo vo) {
        if (L1AttackUtils.isNotAttackAbleByTargetStatus(target)) {
            vo.setDamage(0);
            vo.setDrainMana(0);
        }

        int drainMana = vo.getDrainMana();

        if (drainMana > 0 && target.getCurrentMp() > 0) {
            if (drainMana > target.getCurrentMp()) {
                drainMana = target.getCurrentMp();
            }

            int newMp = attacker.getCurrentMp() + drainMana;
            attacker.setCurrentMp(newMp);
        }

        if (target instanceof ReceiveDamageAble) {
            ReceiveDamageAble damageAble = (ReceiveDamageAble) target;
            damageAble.receiveManaDamage(attacker, vo.getDrainMana());
            damageAble.receiveDamage(attacker, vo.getDamage());
        }
    }
}
