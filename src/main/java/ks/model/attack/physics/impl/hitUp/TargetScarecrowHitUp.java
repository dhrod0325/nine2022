package ks.model.attack.physics.impl.hitUp;

import ks.model.attack.physics.impl.L1AttackHitUp;
import ks.model.attack.physics.impl.L1AttackHitUpDecorator;
import ks.model.attack.physics.impl.vo.L1AttackParam;
import ks.model.instance.L1ScarecrowInstance;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TargetScarecrowHitUp extends L1AttackHitUpDecorator {
    private final Logger logger = LogManager.getLogger();

    public TargetScarecrowHitUp(L1AttackHitUp attackHitUp) {
        super(attackHitUp);
    }

    @Override
    public int calcHitUp(L1AttackParam attackParam) {
        try {
            if (getTarget() instanceof L1ScarecrowInstance) {
                return 100;
            }

            return super.calcHitUp(attackParam);
        } catch (Exception e) {
            logger.error("오류", e);
        }

        return 0;
    }
}