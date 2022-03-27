package ks.model.attack.physics.impl.hitUp;

import ks.model.L1CastleLocation;
import ks.model.L1Character;
import ks.model.attack.physics.impl.L1AttackHitUp;
import ks.model.attack.physics.impl.L1AttackHitUpDecorator;
import ks.model.attack.physics.impl.vo.L1AttackParam;
import ks.packets.serverpackets.S_SystemMessage;
import ks.scheduler.WarTimeScheduler;
import ks.util.L1CharPosUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PcToPcHitUp extends L1AttackHitUpDecorator {
    private final Logger logger = LogManager.getLogger();

    public PcToPcHitUp(L1AttackHitUp attackHitUp) {
        super(attackHitUp);
    }

    @Override
    public int calcHitUp(L1AttackParam attackParam) {
        try {
            L1Character attacker = getAttacker();
            L1Character target = getTarget();

            if (L1CharPosUtils.isSafeZone(attacker) || L1CharPosUtils.isSafeZone(target)) {
                return 0;
            }

            int hitRate = super.calcHitUp(attackParam);

            int castleId = L1CastleLocation.getCastleIdByArea(attacker);

            if (castleId > 0) {
                if (!WarTimeScheduler.getInstance().isNowWar(castleId)) {
                    attacker.sendPackets(new S_SystemMessage("\\fY공성장에서는 PK가 제한됩니다."));
                    hitRate = 0;
                }
            }

            return hitRate;
        } catch (Exception e) {
            logger.error("오류", e);
        }

        return 0;
    }
}
