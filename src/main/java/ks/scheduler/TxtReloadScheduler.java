package ks.scheduler;

import ks.app.LineageAppContext;
import ks.model.txt.L1TxtAlert;
import ks.model.txt.L1TxtChat;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class TxtReloadScheduler {
    private static final Logger logger = LogManager.getLogger(TxtReloadScheduler.class);

    @Scheduled(fixedDelay = 1000 * 60)
    public void run() {
        if (!LineageAppContext.isRun())
            return;

        try {
            L1TxtAlert.getInstance().load();
            L1TxtChat.getInstance().load();
        } catch (Exception e) {
            logger.error(e);
        }
    }
}
