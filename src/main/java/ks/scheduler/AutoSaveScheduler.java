package ks.scheduler;

import ks.app.LineageAppContext;
import ks.app.config.prop.CodeConfig;
import ks.core.datatables.buff.CharBuffTable;
import ks.model.L1World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class AutoSaveScheduler {
    private static final Logger logger = LogManager.getLogger(AutoSaveScheduler.class);

    public static final String AUTO_SAVE_KEY = "AUTO_SAVE_CHAR_INTERVAL";

    public static final String AUTO_SAVE_INV_KEY = "AUTO_SAVE_CHAR_INV_INTERVAL";

    @Scheduled(fixedDelay = 200)
    public void autoSaveCharacter() {
        if (!LineageAppContext.isRun()) {
            return;
        }

        L1World.getInstance().getAllPlayers().forEach(pc -> {
            if (pc.getTimer().isTimeOver(AUTO_SAVE_KEY)) {
                CharBuffTable.save(pc);
                pc.save();

                pc.getTimer().setWaitTime(AUTO_SAVE_KEY, CodeConfig.AUTO_SAVE_CHAR_INTERVAL);
            }
        });
    }

    @Scheduled(fixedDelay = 200)
    public void autoSaveCharacterInventory() {
        if (!LineageAppContext.isRun()) {
            return;
        }

        L1World.getInstance().getAllPlayers().forEach((pc) -> {
            try {
                if (pc.getTimer().isTimeOver(AUTO_SAVE_INV_KEY)) {
                    pc.saveInventory();
                    pc.getTimer().setWaitTime(AUTO_SAVE_INV_KEY, CodeConfig.AUTO_SAVE_CHAR_INV_INTERVAL);
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        });
    }
}
