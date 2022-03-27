package ks.system.robot.ai.scheduler;

import ks.app.LineageAppContext;
import ks.model.L1World;
import ks.system.robot.L1RobotType;
import ks.system.robot.is.L1RobotInstance;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class L1RobotAiScheduler {
    private final Logger logger = LogManager.getLogger();

    public static L1RobotAiScheduler getInstance() {
        return LineageAppContext.getBean(L1RobotAiScheduler.class);
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

        Collection<L1RobotInstance> robotList = L1World.getInstance().getRobotPlayers();

        for (L1RobotInstance robot : robotList) {
            try {
                if (robot == null) {
                    continue;
                }

                if (robot.getRobotType() == L1RobotType.JOMBI)
                    continue;

                if (robot.getAiSleepTime() <= System.currentTimeMillis()) {
                    LineageAppContext.robotAiScheduler().execute(() -> robot.toAI(time));
                }
            } catch (Exception e) {
                logger.error("오류", e);
            }
        }
    }
}
