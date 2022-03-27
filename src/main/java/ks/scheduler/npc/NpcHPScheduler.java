package ks.scheduler.npc;

import ks.app.LineageAppContext;
import ks.model.instance.L1NpcInstance;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class NpcHPScheduler {
    private static final Logger logger = LogManager.getLogger(NpcHPScheduler.class);

    private final List<L1NpcInstance> list = new CopyOnWriteArrayList<>();

    public static NpcHPScheduler getInstance() {
        return LineageAppContext.getBean(NpcHPScheduler.class);
    }

    @Scheduled(fixedDelay = 1000)
    public void run() {
        if (!LineageAppContext.isRun()) {
            return;
        }

        list.stream().filter(Objects::nonNull).forEach(npc -> {
            if (!npc.hprRunning) {
                remove(npc);
                return;
            }

            if (npc.HpRegenTime <= System.currentTimeMillis()) {
                if ((!npc.destroyed && !npc.isDead())
                        && (npc.getCurrentHp() > 0 && npc
                        .getCurrentHp() < npc.getMaxHp())) {
                    int hpr = npc.getHpr();
                    npc.setCurrentHp(npc.getCurrentHp() + hpr);
                    npc.HpRegenTime = npc.getHprInterval() + System.currentTimeMillis();
                } else {
                    remove(npc);
                }
            }
        });
    }

    public void add(L1NpcInstance npc) {
        if (!list.contains(npc))
            list.add(npc);
    }

    public void remove(L1NpcInstance npc) {
        if (list.contains(npc)) {
            list.remove(npc);

            if (npc != null) {
                npc.hprRunning = false;
                npc.HpRegenTime = 0;
            }
        }
    }
}
