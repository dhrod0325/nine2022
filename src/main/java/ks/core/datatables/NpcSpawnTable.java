package ks.core.datatables;

import ks.app.LineageAppContext;
import ks.app.aop.LogTime;
import ks.core.datatables.mapper.SpawnMapper;
import ks.model.L1Npc;
import ks.model.L1Spawn;
import ks.model.instance.L1NpcInstance;
import ks.model.pc.L1PcInstance;
import ks.util.common.SqlUtils;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class NpcSpawnTable {
    private final Map<Integer, L1Spawn> spawnTable = new HashMap<>();

    public static NpcSpawnTable getInstance() {
        return LineageAppContext.getBean(NpcSpawnTable.class);
    }

    public void load() {
        spawnTable.clear();

        List<L1Spawn> list = selectList();

        for (L1Spawn o : list) {
            if (o == null)
                continue;

            spawnTable.put(o.getId(), o);
        }
    }

    public List<L1Spawn> selectList() {
        return SqlUtils.query("SELECT * FROM spawnlist_npc", new SpawnMapper());
    }

    public void storeSpawn(L1PcInstance pc, L1Npc npc) {
        int count = 1;
        String note = npc.getName();

        SqlUtils.update("INSERT INTO spawnlist_npc SET location=?,count=?,npc_templateid=?,locx=?,locy=?,heading=?,mapid=?",
                note,
                count,
                npc.getNpcId(),
                pc.getX(),
                pc.getY(),
                pc.getHeading(),
                pc.getMapId()
        );
    }

    public void removeSpawn(L1NpcInstance npc) {
        Integer id = SqlUtils.select("select id from spawnlist_npc where npc_templateid=? and mapid=? and locx=? and locy=?", Integer.class,
                npc.getNpcId(),
                npc.getMapId(),
                npc.getX(),
                npc.getY()
        );

        if (id == null) {
            return;
        }

        spawnTable.remove(id);

        SqlUtils.update("delete from spawnlist_npc where npc_templateid=? and mapid=? and locx=? and locy=?",
                npc.getNpcId(),
                npc.getMapId(),
                npc.getX(),
                npc.getY()
        );

        SqlUtils.update("update spawnlist set count=0 where npc_templateid=? and mapid=? and locx=? and locy=?",
                npc.getNpcId(),
                npc.getMapId(),
                npc.getX(),
                npc.getY()
        );
    }

    public void deleteNpc(int npcId, int locX, int locY, int mapId) {
        SqlUtils.update("update spawnlist_npc set count=0 where npc_templateid=? and locx=? and locy=? and mapid=?",
                npcId,
                locX,
                locY,
                mapId
        );
    }

    public L1Spawn getTemplate(int i) {
        return spawnTable.get(i);
    }

    @LogTime
    public void loadAndSpawn() {
        load();
        spawnAll();
    }

    private void spawnAll() {
        for (L1Spawn spawn : spawnTable.values()) {
            spawn.init();
        }
    }
}
