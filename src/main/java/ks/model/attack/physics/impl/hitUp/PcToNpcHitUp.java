package ks.model.attack.physics.impl.hitUp;

import ks.model.attack.physics.impl.L1AttackHitUp;
import ks.model.attack.physics.impl.L1AttackHitUpDecorator;
import ks.model.attack.physics.impl.vo.L1AttackParam;
import ks.model.attack.utils.L1AttackUtils;
import ks.model.instance.L1NpcInstance;
import ks.model.instance.L1PetInstance;
import ks.model.instance.L1SummonInstance;
import ks.model.pc.L1PcInstance;
import ks.util.L1CharPosUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PcToNpcHitUp extends L1AttackHitUpDecorator {
    private final Logger logger = LogManager.getLogger();

    public PcToNpcHitUp(L1AttackHitUp attackHitUp) {
        super(attackHitUp);
    }

    @Override
    public int calcHitUp(L1AttackParam attackParam) {
        try {
            L1PcInstance attacker = getAttacker();
            L1NpcInstance target = getTarget();

            if (target instanceof L1PetInstance || target instanceof L1SummonInstance) {
                if (L1CharPosUtils.isSafeZone(attacker) || L1CharPosUtils.isSafeZone(target)) {
                    return 0;
                }
            }

            if (!L1AttackUtils.isAttackAbleGhost(getAttacker(), getTarget())) {
                return 0;
            }

            return super.calcHitUp(attackParam);
        } catch (Exception e) {
            logger.error("오류", e);
        }

        return 0;
    }

    @Override
    public L1PcInstance getAttacker() {
        return (L1PcInstance) super.getAttacker();
    }

    @Override
    public L1NpcInstance getTarget() {
        return (L1NpcInstance) super.getTarget();
    }
}
