package ks.system.portalsystem.table;

import ks.app.LineageAppContext;
import ks.app.aop.LogTime;
import ks.core.datatables.mapper.SpawnMapper;
import ks.model.L1Spawn;
import ks.util.common.SqlUtils;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class PortalSpawnTable {
    private final Map<Integer, L1Spawn> spawnTable = new HashMap<>();

    public static PortalSpawnTable getInstance() {
        return LineageAppContext.getBean(PortalSpawnTable.class);
    }

    @LogTime
    public void load() {
        fillNpcSpawnTable();
    }

    public void fillNpcSpawnTable() {
        spawnTable.clear();
        SqlUtils.query("SELECT * FROM portalsystem_spawnlist", new SpawnMapper());
    }

    public L1Spawn getTemplate(int i) {
        return spawnTable.get(i);
    }
}
