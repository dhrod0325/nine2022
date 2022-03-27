package ks.core.datatables;

import ks.app.LineageAppContext;
import ks.app.aop.LogTime;
import ks.core.datatables.npc.NpcTable;
import ks.model.L1Npc;
import ks.model.L1Spawn;
import ks.model.L1SpawnResult;
import ks.model.pc.L1PcInstance;
import ks.util.common.NumberUtils;
import ks.util.common.SqlUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class SpawnTable {
    private static final Logger logger = LogManager.getLogger(SpawnTable.class.getName());

    private final Map<Integer, L1Spawn> spawnTable = new HashMap<>();

    public static SpawnTable getInstance() {
        return LineageAppContext.getBean(SpawnTable.class);
    }

    public static void storeSpawn(L1PcInstance pc, L1Npc npc) {
        storeSpawn(pc, npc, 0);
    }

    public static void storeSpawn(L1PcInstance pc, L1Npc npc, int range) {
        int count = 1;
        int minRespawnDelay = 60;
        int maxRespawnDelay = 120;

        String note = npc.getName();

        SqlUtils.update("INSERT INTO spawnlist SET location=?,count=?,npc_templateid=?,group_id=?,locx=?,locy=?,randomx=?,randomy=?,heading=?,min_respawn_delay=?,max_respawn_delay=?,mapid=?",
                note,
                count,
                npc.getNpcId(),
                0,
                pc.getX(),
                pc.getY(),
                range,
                range,
                pc.getHeading(),
                minRespawnDelay,
                maxRespawnDelay,
                pc.getMapId()
        );
    }

    private static int calcCount(L1Npc npc, int count, double rate) {
        if (rate == 0) {
            return 0;
        }
        if (rate == 1 || npc.isAmountFixed()) {
            return count;
        } else {
            return NumberUtils.randomRound((count * rate));
        }
    }

    public void spawnAll() {
        spawnByMapId(-1);
    }

    public void spawnByMapId(int mapId) {
        Map<Integer, L1SpawnResult> checkList = new TreeMap<>();

        for (L1Spawn spawn : spawnTable.values()) {
            if (spawn.getMapId() != mapId && mapId != -1) {
                continue;
            }

            spawn.init();

            L1SpawnResult result = checkList.getOrDefault((int) spawn.getMapId(), new L1SpawnResult());
            result.setMapId(spawn.getMapId());
            result.setTotalCount(result.getTotalSpawnCount() + spawn.getTotalSpawnCount());
            result.setTotalAdenaCount(result.getTotalAdenaCount() + spawn.getTotalAdenaCount());
            checkList.put((int) spawn.getMapId(), result);
        }

        for (L1SpawnResult result : checkList.values()) {
            logger.trace("몬스터 스폰 맵아이디 : {} 몬스터 수 : {} 아덴 : {}",
                    result.getMapId(),
                    result.getTotalSpawnCount(),
                    result.getTotalAdenaCount());
        }
    }

    @LogTime
    public void loadAndSpawn() {
        load();
        spawnAll();
    }

    public void load() {
        spawnTable.clear();

        SqlUtils.query("SELECT *,(select spawnlist_door.id from spawnlist_door where keeper=spawnlist.id) doorId FROM spawnlist", (rs, i) -> {
            try {
                int npcTemplateId = rs.getInt("npc_templateid");
                L1Npc template = NpcTable.getInstance().getTemplate(npcTemplateId);

                if (template == null) {
                    logger.warn("npc를 찾을 수 없습니다 : " + npcTemplateId);
                } else {
                    if (rs.getInt("count") == 0) {
                        return null;
                    }

                    double amount_rate = MapsTable.getInstance().getMonsterAmount(rs.getShort("mapid"));
                    int count = calcCount(template, rs.getInt("count"), amount_rate);

                    if (count == 0) {
                        return null;
                    }

                    L1Spawn spawn = new L1Spawn(template);
                    spawn.setId(rs.getInt("id"));
                    spawn.setAmount(count);
                    spawn.setGroupId(rs.getInt("group_id"));
                    spawn.setLocX(rs.getInt("locx"));
                    spawn.setLocY(rs.getInt("locy"));
                    spawn.setRandomX(rs.getInt("randomx"));
                    spawn.setRandomY(rs.getInt("randomy"));
                    spawn.setLocX1(rs.getInt("locx1"));
                    spawn.setLocY1(rs.getInt("locy1"));
                    spawn.setLocX2(rs.getInt("locx2"));
                    spawn.setLocY2(rs.getInt("locy2"));
                    spawn.setHeading(rs.getInt("heading"));
                    spawn.setMinRespawnDelay(rs.getInt("min_respawn_delay"));
                    spawn.setMaxRespawnDelay(rs.getInt("max_respawn_delay"));
                    spawn.setMapId(rs.getShort("mapid"));
                    spawn.setRespawnScreen(rs.getBoolean("respawn_screen"));
                    spawn.setMovementDistance(rs.getInt("movement_distance"));
                    spawn.setRest(rs.getBoolean("rest"));
                    spawn.setNearSpawn(rs.getInt("near_spawn"));
                    spawn.setName(template.getName());
                    spawn.setDoorId(rs.getInt("doorId"));

                    if (count > 1 && spawn.getLocX1() == 0) {
                        int range = Math.min(count * 6, 30);
                        spawn.setLocX1(spawn.getLocX() - range);
                        spawn.setLocY1(spawn.getLocY() - range);
                        spawn.setLocX2(spawn.getLocX() + range);
                        spawn.setLocY2(spawn.getLocY() + range);
                    }

                    spawnTable.put(spawn.getId(), spawn);
                }

            } catch (Exception e) {
                logger.error(e);
            }

            return null;
        });
    }

    public L1Spawn getTemplate(int Id) {
        return spawnTable.get(Id);
    }

    public List<L1Spawn> getSpawnListByMapId(int mapId) {
        List<L1Spawn> result = new ArrayList<>();
        for (L1Spawn o : spawnTable.values()) {
            if (o.getMapId() == mapId) {
                result.add(o);
            }
        }

        return result;
    }

    public List<L1Spawn> findSpawnList(int templateId) {
        List<L1Spawn> result = new ArrayList<>();

        for (L1Spawn spawn : spawnTable.values()) {
            if (spawn.getTemplate().getNpcId() == templateId) {
                result.add(spawn);
            }
        }
        return result;
    }
}
