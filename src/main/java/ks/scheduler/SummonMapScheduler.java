package ks.scheduler;

import ks.app.LineageAppContext;
import ks.core.datatables.MapsTable;
import ks.model.L1World;
import ks.util.L1CommonUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SummonMapScheduler {
    private static final Logger logger = LogManager.getLogger(SummonMapScheduler.class);

    public static SummonMapScheduler getInstance() {
        return LineageAppContext.getBean(SummonMapScheduler.class);
    }

    @Scheduled(fixedDelay = 1000 * 10)
    public void run() {
        if (!LineageAppContext.isRun()) {
            return;
        }
        L1World.getInstance().getAllPlayers().forEach(pc -> {
            if (L1CommonUtils.isLastabardMap(pc.getMapId()) || !pc.getMap().isTakePets()) {
                try {
                    if (!pc.getPetList().isEmpty()) {
                        String mapName = MapsTable.getInstance().getMapName(pc.getMapId());
                        pc.sendPackets(mapName + "은 펫,서먼 사용 불가 지역입니다");
                        pc.returnAllPetAndSummon();
                    }
                } catch (Exception e) {
                    logger.error(e);
                }
            }
        });
    }
}
