package ks.model.attack.physics.impl.commit;

import ks.model.L1Character;
import ks.model.attack.physics.impl.L1AttackCommit;
import ks.model.attack.physics.impl.action.utils.L1ActionCounterResult;
import ks.model.attack.physics.impl.action.utils.L1ActionUtils;
import ks.model.attack.physics.impl.vo.L1AttackParam;
import ks.model.instance.extend.DrainHpMpAble;
import ks.model.instance.extend.ReceiveDamageAble;
import ks.util.log.L1LogUtils;

public class L1AttackCommitDefault implements L1AttackCommit {
    private final L1Character attacker;
    private final L1Character target;

    public L1AttackCommitDefault(L1Character attacker, L1Character target) {
        this.attacker = attacker;
        this.target = target;
    }

    @Override
    public void commit(L1AttackParam param) {
        if (param.isHitUp()) {
            L1ActionCounterResult actionCounterResult = L1ActionUtils.actionCounter(attacker, target);

            if (actionCounterResult.isSuccess() && actionCounterResult.getCounterDmg() > 0) {
                if (attacker != null) {
                    ((ReceiveDamageAble) attacker).receiveDamage(target, actionCounterResult.getCounterDmg());
                }
                return;
            }
        }

        if (attacker instanceof DrainHpMpAble) {
            DrainHpMpAble drainAttacker = (DrainHpMpAble) attacker;

            int drainMana = drainAttacker.getDrainMp();
            int drainHp = drainAttacker.getDrainHp();

            L1LogUtils.gmLog(attacker, "drainMana : {}, drainHp : {}", drainMana, drainHp);

            drainAttacker.drainMana(target);
            drainAttacker.drainHp(target);
        }

        if (target != null) {
            target.receiveDamage(attacker, param.getDamage());
        }
    }
}
