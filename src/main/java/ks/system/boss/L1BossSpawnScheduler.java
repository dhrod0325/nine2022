package ks.system.boss;

import ks.app.LineageAppContext;
import ks.app.config.prop.ServerConfig;
import ks.model.instance.L1MonsterInstance;
import ks.system.boss.model.L1Boss;
import ks.system.boss.model.L1BossDieHistory;
import ks.system.boss.table.L1BossDieHistoryTable;
import ks.system.boss.table.L1BossSpawnListHotTable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.List;

@Component
public class L1BossSpawnScheduler {
    private final Logger logger = LogManager.getLogger();

    public static L1BossSpawnScheduler getInstance() {
        return LineageAppContext.getBean(L1BossSpawnScheduler.class);
    }

    @Scheduled(fixedDelay = 1000)
    public void run() {
        if (ServerConfig.isTest()) {
            logger.trace("TEST MODE SKIP BossSpawn Scheduler");
            return;
        }

        if (!LineageAppContext.isRun()) {
            return;
        }

        try {
            List<L1Boss> list = L1BossSpawnListHotTable.getInstance().getList();

            Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int min = c.get(Calendar.MINUTE);

            long time = System.currentTimeMillis();

            for (L1Boss boss : list) {
                try {
                    if (boss.isSpawnTime(hour, min, time)) {
                        L1MonsterInstance monster = L1BossSpawnManager.getInstance().findByBoss(boss);

                        if (monster != null) {
                            continue;
                        }

                        List<L1BossDieHistory> dieCheck = L1BossDieHistoryTable.getInstance().selectByDate(boss.getId(), boss.getNpcId(), c.getTime());

                        if (dieCheck.isEmpty()) {
                            logger.info("보스가 스폰되었습니다 {}", boss.getMonName());

                            L1BossSpawnManager.getInstance().addBoss(boss, 1000L * 60 * boss.getDeleteMin());
                            boss.buildNextTime();
                        }
                    }
                } catch (Exception e) {
                    logger.error("오류", e);
                }
            }


        } catch (Exception e) {
            logger.error("오류", e);
        }
    }
}
