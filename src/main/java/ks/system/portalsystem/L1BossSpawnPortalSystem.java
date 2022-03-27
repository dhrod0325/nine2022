package ks.system.portalsystem;

import ks.model.L1MonsterDeath;
import ks.model.instance.L1MonsterInstance;
import ks.scheduler.timer.BaseTime;
import ks.system.boss.L1BossSpawnManager;
import ks.system.boss.model.L1Boss;
import ks.system.boss.model.L1BossDieHistory;
import ks.system.boss.table.L1BossDieHistoryTable;
import ks.system.boss.table.L1BossSpawnListHotTable;
import ks.system.portalsystem.model.L1PortalBoss;
import ks.system.portalsystem.model.L1PortalBossInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public abstract class L1BossSpawnPortalSystem extends L1AbstractPortalSystem {
    private static final Logger logger = LogManager.getLogger(L1AbstractPortalSystem.class);

    protected final L1PortalBoss portalBoss = new L1PortalBoss(this);

    private final List<L1Boss> bossList = new ArrayList<>();

    public L1BossSpawnPortalSystem() {
        registerBossInfo(portalBoss);
    }

    public abstract void registerBossInfo(L1PortalBoss portalBoss);

    @Override
    public void open(BaseTime time) {
        super.open(time);

        bossList.clear();
        portalBoss.setBossSpawn(false);
    }

    @Override
    public void run(BaseTime time) {
        super.run(time);

        if (portalBoss.getBossSpawnTime().toInstant().isBefore(time.toInstant())) {
            if (!portalBoss.isBossSpawn()) {
                bossStart();
            }
        }
    }

    @Override
    public void shutdown() {
        super.shutdown();

        for (L1Boss bs : bossList) {
            L1BossSpawnManager.getInstance().removeBoss(bs);
        }
    }

    public void bossStart() {
        logger.debug("보스 시작");

        portalBoss.setBossSpawn(true);

        for (L1PortalBossInfo o : portalBoss.getPortalBossInfoList()) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            List<L1BossDieHistory> list = L1BossDieHistoryTable.getInstance().selectListByPortalInfo(o.getNpcId(), getPortalData().getId(), format.format(getPortalData().getStartTime()));

            if (!list.isEmpty()) {
                continue;
            }

            int remainingMinute = Math.toIntExact(TimeUnit.MILLISECONDS.toMinutes(getRemainingSecond()));
            int tt = getPortalData().getDurationMinute() - 2;

            if (remainingMinute >= tt) {
                continue;
            }

            L1Boss boss = L1BossSpawnListHotTable.getInstance().findByNpcId(o.getNpcId());

            if (boss != null) {
                boss.setSpawnX1(o.getX());
                boss.setSpawnY1(o.getY());
                boss.setSpawnMap(o.getMapId());

                L1BossSpawnManager.getInstance().addBoss(boss, boss.getDeleteMin(), true, npc -> {
                    if (npc instanceof L1MonsterInstance) {
                        L1MonsterInstance mon = (L1MonsterInstance) npc;

                        L1MonsterDeath death = mon.getDeath();

                        if (death != null && death.getAttacker() != null) {
                            String attackerName = death.getAttacker().getName();
                            L1BossDieHistoryTable.getInstance().insertOrUpdate(boss.getId(), boss.getNpcId(), new Date(), attackerName, getPortalData().getId(), getPortalData().getStartTime());
                        }

                        L1BossSpawnManager.getInstance().removeBoss(boss);
                    }
                });

                if (!bossList.contains(boss)) {
                    bossList.add(boss);
                    logger.info("포털시스템 보스스폰 " + boss.getMonName() + ",삭제시간 : " + boss.getDeleteMin() + "분 후 ");
                }
            } else {
                logger.warn("등록되어있는 보스 없음 : " + o.getNpcId());
            }
        }
    }

    public L1PortalBoss getPortalBoss() {
        return portalBoss;
    }
}
