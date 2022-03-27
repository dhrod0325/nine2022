package ks.system.boss;

import ks.app.LineageAppContext;
import ks.app.aop.LogTime;
import ks.app.config.prop.CodeConfig;
import ks.app.config.prop.ServerConfig;
import ks.constants.L1PacketBoxType;
import ks.core.datatables.MapsTable;
import ks.model.L1MonsterDeath;
import ks.model.L1World;
import ks.model.instance.L1MonsterInstance;
import ks.model.instance.L1NpcInstance;
import ks.packets.serverpackets.S_PacketBox;
import ks.packets.serverpackets.S_SystemMessage;
import ks.scheduler.npc.NpcDeleteScheduler;
import ks.system.boss.model.L1Boss;
import ks.system.boss.table.L1BossDieHistoryTable;
import ks.system.boss.table.L1BossSpawnListHotCurrentTable;
import ks.util.L1CommonUtils;
import ks.util.L1SpawnUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class L1BossSpawnManager {
    private final Logger logger = LogManager.getLogger();

    private final Map<L1Boss, L1MonsterInstance> spawnBossMap = new ConcurrentHashMap<>();

    public static L1BossSpawnManager getInstance() {
        return LineageAppContext.getBean(L1BossSpawnManager.class);
    }

    public boolean isSpawned(L1NpcInstance mi) {
        if (mi == null) {
            return false;
        }

        return findByBoss(mi) != null;
    }

    public L1Boss findByBoss(L1NpcInstance mi) {
        for (L1Boss key : spawnBossMap.keySet()) {
            try {
                L1MonsterInstance value = spawnBossMap.get(key);

                if (mi.equals(value)) {
                    return key;
                }
            } catch (Exception e) {
                return null;
            }
        }

        return null;
    }

    public void removeBoss(L1NpcInstance mi) {
        L1Boss bs = findByBoss(mi);
        removeBoss(bs);
    }

    public void removeBoss(L1Boss boss) {
        if (!CodeConfig.USE_BOSS_SYSTEM) {
            logger.info("USE_BOSS_SYSTEM IS FALSE METHOD : removeBoss");
            return;
        }

        if (ServerConfig.isTest()) {
            logger.info("TEST MODE SKIP BossSpawn Scheduler removeBoss");
            return;
        }

        if (boss == null) {
            return;
        }

        L1MonsterInstance a = findByNpcId(boss.getNpcId());

        if (a != null && !a.isDead()) {
            a.deleteMe();
        }

        logger.debug("BOSS 삭제 : {}", boss.getMonName());

        spawnBossMap.remove(boss);

        L1BossSpawnListHotCurrentTable.getInstance().deleteById(boss);
    }

    public void addBoss(L1Boss boss, long deleteTime) {
        addBoss(boss, deleteTime, true);
    }

    public void addBoss(L1Boss boss, long deleteTime, boolean insertDB) {
        addBoss(boss, deleteTime, insertDB, null);
    }

    public void addBoss(L1Boss boss, long deleteTime, boolean insertDB, NpcDeleteScheduler.NpcDeleteCallBack callback) {
        if (!CodeConfig.USE_BOSS_SYSTEM) {
            logger.info("USE_BOSS_SYSTEM IS FALSE METHOD : addBoss");
            return;
        }

        if (ServerConfig.isTest()) {
            logger.info("TEST MODE SKIP BossSpawn Scheduler addBoss");
            return;
        }

        long currentTime = System.currentTimeMillis();

        long d = currentTime + deleteTime;

        if (currentTime > d) {
            logger.info("[BOSS 스폰]:" + boss.getMonName() + ",[스폰 후 삭제 되어야 하는시간이 지나서 삭제함]");
            L1BossSpawnListHotCurrentTable.getInstance().deleteById(boss);
        } else {
            logger.info("[BOSS 스폰]:" + boss.getMonName() + ",[스폰 후 삭제 되어야 하는시간]:" + L1CommonUtils.dateFormat(new Date(d)));

            if (insertDB) {
                L1BossSpawnListHotCurrentTable.getInstance().insert(boss);
            }

            L1MonsterInstance mi = bossSpawn(boss, deleteTime, callback);

            if (mi != null) {
                spawnBossMap.put(boss, mi);
            }
        }
    }

    @LogTime
    public void spawnServerDownPrevBoss() {
        try {
            if (!CodeConfig.USE_BOSS_SYSTEM) {
                logger.info("보스 스폰 시스템을 사용중이 아닙니다");
                return;
            }

            if (ServerConfig.isTest()) {
                logger.info("테스트모드중엔 재시작시 보스가 다시 스폰되지 않습니다");
                return;
            }

            spawnBossMap.clear();

            List<L1Boss> bossList = L1BossSpawnListHotCurrentTable.getInstance().selectList();

            for (L1Boss boss : bossList) {
                if (boss.getDeleteMin() == 0) {
                    addBoss(boss, 0, false);
                } else {
                    Calendar c = Calendar.getInstance();
                    c.setTime(boss.getRegDate());
                    c.add(Calendar.MINUTE, boss.getDeleteMin());

                    long o = c.getTimeInMillis();
                    long currentTime = System.currentTimeMillis();

                    long delete = o - currentTime;

                    if (delete > 0) {
                        addBoss(boss, delete, false);
                    } else {
                        removeBoss(boss);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("오류", e);
        }
    }

    public L1MonsterInstance findByNpcId(int npcId) {
        for (L1MonsterInstance a : spawnBossMap.values()) {
            if (a.getNpcId() == npcId) {
                return a;
            }
        }

        return null;
    }

    public L1MonsterInstance findByBoss(L1Boss boss) {
        return spawnBossMap.get(boss);
    }

    public Collection<L1MonsterInstance> getBossList() {
        return spawnBossMap.values();
    }

    public L1MonsterInstance bossSpawn(L1Boss boss,
                                       long deleteTimeMill,
                                       NpcDeleteScheduler.NpcDeleteCallBack callback) {
        try {
            if (!CodeConfig.USE_BOSS_SYSTEM) {
                logger.info("USE_BOSS_SYSTEM IS FALSE METHOD : bossSpawn");
                return null;
            }

            int npcId = boss.getNpcId();

            int map = boss.getSpawnMap();
            String ment = boss.getMent();

            L1NpcInstance npc;

            if (boss.isAreaSpawn()) {
                npc = L1SpawnUtils.randomSpawn(npcId,
                        boss.getSpawnX1(),
                        boss.getSpawnX2(),
                        boss.getSpawnY1(),
                        boss.getSpawnY2(),
                        (short) map, deleteTimeMill, callback);
            } else {
                int x = boss.getSpawnX1();
                int y = boss.getSpawnY1();

                if (x == 0 && y == 0) {
                    MapsTable.MapData m = MapsTable.getInstance().getMaps().get(map);
                    npc = L1SpawnUtils.randomSpawn(npcId, m.startX, m.endX, m.startY, m.endY, (short) map, deleteTimeMill, callback);
                } else {
                    npc = L1SpawnUtils.spawn(x, y, (short) map, npcId, boss.getRandomRange(), deleteTimeMill, callback);
                }
            }

            if (npc != null) {
                if (callback == null) {
                    npc.addDeleteCallBack(mon -> {
                        L1MonsterDeath death = ((L1MonsterInstance) mon).getDeath();

                        if (death != null && death.getAttacker() != null) {
                            String attackerName = death.getAttacker().getName();
                            logger.info(boss.getMonName() + "가  " + attackerName + "님에게 잡혔습니다");
                            L1BossDieHistoryTable.getInstance().insertOrUpdate(boss.getId(), boss.getNpcId(), new Date(), attackerName);
                        } else {
                            logger.info(boss.getMonName() + "이 삭제되었습니다");
                        }

                        removeBoss(boss);
                    });
                }
            }

            if (!StringUtils.isEmpty(ment)) {
                L1World.getInstance().broadcastPacketToAll(new S_SystemMessage(ment));
                L1World.getInstance().broadcastPacketToAll(new S_PacketBox(L1PacketBoxType.GREEN_MESSAGE, ment));
            }

            return (L1MonsterInstance) npc;
        } catch (Exception e) {
            logger.error(e);
        }

        return null;
    }
}
