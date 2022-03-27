package ks.scheduler;

import ks.app.LineageAppContext;
import ks.model.L1World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class CharacterQuitCheckScheduler {
    private static final Logger logger = LogManager.getLogger(CharacterQuitCheckScheduler.class);

    @Scheduled(fixedDelay = 1000 * 10)
    public void scheduled() {
        if (!LineageAppContext.isRun()) {
            return;
        }

        L1World.getInstance().getAllPlayers().stream()
                .filter(Objects::nonNull)
                .filter(pc -> pc.getClient() != null)
                .forEach(pc -> {
                    try {
                        if (pc.getClient().isClosed()) {
                            pc.logout();
                            pc.disconnect();
                        }
                    } catch (Exception e) {
                        logger.error(e);
                    }
                });
    }
}
