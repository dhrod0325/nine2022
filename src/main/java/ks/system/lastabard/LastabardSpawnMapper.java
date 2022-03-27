package ks.system.lastabard;


import ks.core.datatables.MapsTable;
import ks.core.datatables.npc.NpcTable;
import ks.model.L1Npc;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;

public class LastabardSpawnMapper implements RowMapper<LastabardSpawn> {
    private final Logger logger = LogManager.getLogger();

    private int calcCount(L1Npc npc, int count, double rate) {
        if (rate == 0) {
            return 0;
        }

        if (rate == 1 || npc.isAmountFixed()) {
            return count;
        } else {
            return (int) (count * rate);
        }
    }

    @Override
    public LastabardSpawn mapRow(ResultSet rs, int i) {
        try {
            if (rs.getInt("count") == 0) {
                return null;
            }

            int id = rs.getInt("id");

            int npcTemplateId = rs.getInt("npc_templateid");
            L1Npc npcTemplate = NpcTable.getInstance().getTemplate(npcTemplateId);

            if (npcTemplate == null) {
                logger.warn("[Lastabard] missing mob data for id:" + npcTemplateId + " in npc table");
                return null;
            }

            double amountRate = MapsTable.getInstance().getMonsterAmount(rs.getShort("mapid"));
            int count = calcCount(npcTemplate, rs.getInt("count"), amountRate);

            if (count == 0)
                return null;

            LastabardSpawn spawn = new LastabardSpawn(npcTemplate);
            spawn.setId(id);
            spawn.setNpcId(npcTemplateId);
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
            spawn.setName(npcTemplate.getName());
            spawn.setDoorId(rs.getInt("spawnlist_door"));
            spawn.setCountMapId(rs.getInt("count_map"));

            if (count > 1 && spawn.getLocX1() == 0) {
                int range = Math.min(count * 6, 30);
                spawn.setLocX1(spawn.getLocX() - range);
                spawn.setLocY1(spawn.getLocY() - range);
                spawn.setLocX2(spawn.getLocX() + range);
                spawn.setLocY2(spawn.getLocY() + range);
            }

            return spawn;
        } catch (Exception e) {
            logger.error("오류", e);
        }

        return null;
    }
}
