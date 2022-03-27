package ks.scheduler;

import ks.app.LineageAppContext;
import ks.model.L1World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class HpMpRegenScheduler {
    private static final Logger logger = LogManager.getLogger(HpMpRegenScheduler.class);

    @Scheduled(fixedDelay = 1000 * 8)
    public void hpRegen() {
        if (!LineageAppContext.isRun()) {
            return;
        }

        L1World.getInstance().getAllPlayers().forEach(pc -> {
            try {
                if (pc.isDead()) {
                    return;
                }

                pc.getHpMpRegen().regenHpBySchedule();
            } catch (Exception e) {
                logger.error(e);
            }
        });
    }

    @Scheduled(fixedDelay = 1000 * 16)
    public void mpRegen() {
        if (!LineageAppContext.isRun()) {
            return;
        }

        L1World.getInstance().getAllPlayers().forEach(pc -> {
            if (pc.isDead()) {
                return;
            }

            pc.getHpMpRegen().regenMpBySchedule();
        });
    }
}
