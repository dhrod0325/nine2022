package ks.system.lastabard;

import ks.app.LineageAppContext;
import ks.app.aop.LogTime;
import ks.model.L1Spawn;
import ks.util.common.SqlUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class LastabardSpawnTable {
    private final Map<Integer, LastabardSpawn> spawnTable = new HashMap<>();

    public static LastabardSpawnTable getInstance() {
        return LineageAppContext.getBean(LastabardSpawnTable.class);
    }

    public List<LastabardSpawn> selectList() {
        return SqlUtils.query("SELECT * FROM spawnlist_lastabard", new LastabardSpawnMapper());
    }

    @LogTime
    public void loadAndSpawn() {
        load();

        for (LastabardSpawn spawn : spawnTable.values()) {
            spawn.init();
        }
    }

    public void load() {
        spawnTable.clear();

        List<LastabardSpawn> list = selectList();

        for (LastabardSpawn spawnDat : list) {
            if (spawnDat == null)
                continue;

            spawnTable.put(spawnDat.getId(), spawnDat);
        }
    }

    public List<L1Spawn> findSpawnList(int templateId) {
        List<L1Spawn> result = new ArrayList<>();
        for (LastabardSpawn spawn : spawnTable.values()) {
            if (spawn.getTemplate().getNpcId() == templateId) {
                result.add(spawn);
            }
        }

        return result;
    }

    public void spawnByMapId(int mapId) {
        for (LastabardSpawn spawn : spawnTable.values()) {
            if (spawn.getMapId() == mapId) {
                spawn.init();
            }
        }
    }
}
