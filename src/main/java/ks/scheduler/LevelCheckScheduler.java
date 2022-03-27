package ks.scheduler;

import ks.app.LineageAppContext;
import ks.model.L1World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class LevelCheckScheduler {
    private final Logger logger = LogManager.getLogger();

    @Scheduled(fixedDelay = 1000 * 60)
    public void scheduled() {
        if (!LineageAppContext.isRun()) {
            return;
        }

        check();
    }

    private void check() {
        L1World.getInstance().getAllPlayers()
                .stream()
                .forEach(pc -> {
                    try {
                        if (pc.isGm()) {
                            return;
                        }

                        if (pc.getLevel() > pc.getHighLevel()) {
                            pc.disconnect();
                        } else if (pc.getLevel() > 99) {
                            pc.disconnect();
                        }
                    } catch (Exception e) {
                        logger.error("오류", e);
                    }
                });
    }
}
