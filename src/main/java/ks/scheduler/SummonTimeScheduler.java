package ks.scheduler;

import ks.app.LineageAppContext;
import ks.model.instance.L1SummonInstance;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class SummonTimeScheduler {
    private static final Logger logger = LogManager.getLogger(SummonTimeScheduler.class);

    private final List<L1SummonInstance> list = new CopyOnWriteArrayList<>();

    public static SummonTimeScheduler getInstance() {
        return LineageAppContext.getBean(SummonTimeScheduler.class);
    }

    @Scheduled(fixedDelay = 2000)
    public void run() {
        if (!LineageAppContext.isRun()) {
            return;
        }
        list.forEach(npc -> {
            try {
                if (npc == null || npc.destroyed) {
                    removeNpc(npc);
                    return;
                }

                if (npc.sumTime <= System.currentTimeMillis()) {
                    npc.Death();
                    removeNpc(npc);
                }
            } catch (Exception e) {
                logger.error(e);
            }
        });
    }

    public void addNpc(L1SummonInstance npc) {
        if (!list.contains(npc))
            list.add(npc);
    }

    public void removeNpc(L1SummonInstance npc) {
        list.remove(npc);
    }

    public int getSize() {
        return list.size();
    }

}
