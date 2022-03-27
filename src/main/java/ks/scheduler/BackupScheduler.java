package ks.scheduler;

import ks.app.LineageAppContext;
import ks.app.aop.LogTime;
import ks.app.config.prop.CodeConfig;
import ks.util.L1ServerUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ScheduledFuture;

@Component
public class BackupScheduler {
    private static final Logger logger = LogManager.getLogger(BackupScheduler.class);
    private boolean startBackup = false;

    private ScheduledFuture<?> future;

    public static BackupScheduler getInstance() {
        return LineageAppContext.getBean(BackupScheduler.class);
    }

    public void start() {
        logger.info("백업 스케쥴러 : 사용여부 - " + CodeConfig.DB_BACKUP_USE);

        if (CodeConfig.DB_BACKUP_USE) {
            int backUpTime = 1000 * 60 * CodeConfig.DB_BACKUP_MININUTE;

            logger.info("백업 스케쥴러 : 동작간격 - " + CodeConfig.DB_BACKUP_MININUTE + "분");
            logger.info("백업 스케쥴러 : " + CodeConfig.DB_BACKUP_MININUTE + "분 후에 동작이 시작됩니다");

            future = LineageAppContext.backUpTaskScheduler().scheduleAtFixedRate(this::backUp,
                    Instant.now().plusMillis(backUpTime),
                    Duration.ofMillis(backUpTime)
            );
        }
    }

    @LogTime
    public void backUp() {
        if (startBackup) {
            return;
        }

        startBackup = true;

        try {
            L1ServerUtils.getInstance().backUp("data");
        } catch (Exception e) {
            logger.error("오류", e);
        }

        startBackup = false;
    }

    public void restart() {
        stop();
        start();
    }

    public void stop() {
        if (future != null) {
            logger.info("백업 스케쥴러 : 정지");

            future.cancel(true);
            future = null;
        }
    }
}
