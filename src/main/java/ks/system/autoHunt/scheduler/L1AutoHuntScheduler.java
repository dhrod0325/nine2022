package ks.system.autoHunt.scheduler;

import ks.app.LineageAppContext;
import ks.model.pc.L1PcInstance;
import ks.system.autoHunt.L1AutoHuntAi;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class L1AutoHuntScheduler {
    private final Logger logger = LogManager.getLogger();

    private final Map<Integer, L1AutoHuntAi> autoHuntPcList = new ConcurrentHashMap<>();

    public static L1AutoHuntScheduler getInstance() {
        return LineageAppContext.getBean(L1AutoHuntScheduler.class);
    }

    @Scheduled(fixedDelay = 10)
    public void run() {
        if (!LineageAppContext.isRun()) {
            return;
        }

        start();
    }

    public void start() {
        long time = System.currentTimeMillis();

        for (L1AutoHuntAi ai : autoHuntPcList.values()) {
            try {
                if (ai == null) {
                    continue;
                }

                LineageAppContext.robotAiScheduler().execute(() -> ai.toAI(time));
            } catch (Exception e) {
                logger.error("오류", e);
            }
        }
    }

    public void add(L1PcInstance pc, L1AutoHuntAi ai) {
        autoHuntPcList.put(pc.getId(), ai);
    }

    public void remove(L1PcInstance pc) {
        autoHuntPcList.remove(pc.getId());
    }
}
