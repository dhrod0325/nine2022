package ks.scheduler;

import ks.app.LineageAppContext;
import ks.core.network.util.L1ClientManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ClientThreadObserverScheduler {
    private static final Logger logger = LogManager.getLogger();

    @Scheduled(fixedDelay = 60 * 1000)
    public void run() {
        if (!LineageAppContext.isRun()) {
            return;
        }

        L1ClientManager.getInstance().getAllClients().forEach(client -> {
            try {
                String ip = client.getIp();

                if (client.isClosed()) {
                    logger.info("킥! (" + ip + ")의 접속을 강제 절단 했습니다.");
                    client.disconnect();
                    return;
                }

                if (client.getCheckCount() > 0) {
                    client.setCheckCount(0);
                    return;
                }

                if (client.getActiveChar() == null) {
                    logger.info("일정시간 응답을 얻을 수 없었기 때문에(" + ip + ")의 접속을 강제 절단 했습니다.");
                    client.disconnect();
                }
            } catch (Exception e) {
                logger.error(e);
            }
        });
    }
}
