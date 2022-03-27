package ks.scheduler;

import ks.app.LineageAppContext;
import ks.model.pc.L1PcInstance;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class PcInvisDelayScheduler {
    private final Logger logger = LogManager.getLogger();

    private final List<L1PcInstance> list = new CopyOnWriteArrayList<>();

    public static PcInvisDelayScheduler getInstance() {
        return LineageAppContext.getCtx().getBean(PcInvisDelayScheduler.class);
    }

    @Scheduled(fixedDelay = 1000)
    public void run() {
        if (!LineageAppContext.isRun()) {
            return;
        }

        list.stream().filter(Objects::nonNull).forEach(pc -> {
            try {
                if (pc.getInvisDelayTime() <= System.currentTimeMillis()) {
                    pc.addInvisDelayCounter(-1);
                    removePc(pc);
                }
            } catch (Exception e) {
                logger.error("오류", e);
            }
        });
    }

    public void addPc(L1PcInstance npc) {
        if (!list.contains(npc))
            list.add(npc);
    }

    public void removePc(L1PcInstance npc) {
        list.remove(npc);
    }
}
