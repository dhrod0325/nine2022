package ks.scheduler;

import ks.app.LineageAppContext;
import ks.model.instance.L1DollInstance;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class DollDeleteScheduler {
    private static final Logger logger = LogManager.getLogger(DollDeleteScheduler.class);

    private final List<L1DollInstance> dollList = new CopyOnWriteArrayList<>();

    public static DollDeleteScheduler getInstance() {
        return LineageAppContext.getCtx().getBean(DollDeleteScheduler.class);
    }

    @Scheduled(fixedDelay = 1000)
    public void run() {
        if (!LineageAppContext.isRun()) {
            return;
        }

        dollList.stream().filter(Objects::nonNull).forEach(doll -> {
            try {
                if (doll.destroyed) {
                    removeDollDelete(doll);
                    return;
                }

                if (doll.getMaster() == null) {
                    removeDollDelete(doll);
                    return;
                }

                if (doll.dollTime <= System.currentTimeMillis()) {
                    doll.deleteDoll();
                    removeDollDelete(doll);
                }
            } catch (Exception e) {
                logger.error(e);
            }
        });
    }

    public void addDollDelete(L1DollInstance npc) {
        synchronized (dollList) {
            dollList.add(npc);
        }
    }

    public void removeDollDelete(L1DollInstance npc) {
        dollList.remove(npc);
    }

    public int getSize() {
        return dollList.size();
    }
}
