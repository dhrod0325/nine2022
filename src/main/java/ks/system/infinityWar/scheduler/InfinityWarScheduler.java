package ks.system.infinityWar.scheduler;

import ks.app.LineageAppContext;
import ks.system.infinityWar.model.InfinityWar;
import ks.system.infinityWar.system.InfinityWarSystem;
import ks.system.infinityWar.table.InfinityWarTable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
public class InfinityWarScheduler {
    private final Logger logger = LogManager.getLogger();

    @Resource
    private InfinityWarTable infinityWarTable;

    @Scheduled(fixedDelay = 1000)
    public void playerCheck() {
        if (!LineageAppContext.isRun()) {
            return;
        }

        List<InfinityWar> list = infinityWarTable.getList();

        for (InfinityWar war : list) {
            InfinityWarSystem system = war.getWarSystem();

            if (system.isOpen()) {
                system.playerCheck();
            }
        }
    }

    @Scheduled(fixedDelay = 1000)
    public void run() {
        if (!LineageAppContext.isRun()) {
            return;
        }

        List<InfinityWar> list = infinityWarTable.getList();

        for (InfinityWar war : list) {
            InfinityWarSystem system = war.getWarSystem();

            if (!system.isOpen()) {
                if (system.isOpenTime()) {
                    if (!system.isOpen()) {
                        system.setOpen(true);
                        LineageAppContext.commonTaskScheduler().execute(system);
                    }
                } else {
                    system.setOpen(false);
                }
            }
        }
    }
}
