package ks.core.datatables.mapper;

import ks.core.datatables.npc.NpcTable;
import ks.model.L1Npc;
import ks.model.L1Spawn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SpawnMapper implements RowMapper<L1Spawn> {
    private final Logger logger = LogManager.getLogger();

    @Override
    public L1Spawn mapRow(ResultSet rs, int rowNum) throws SQLException {
        try {
            int npcTemplateId = rs.getInt("npc_templateid");

            L1Npc npc = NpcTable.getInstance().getTemplate(npcTemplateId);

            if (npc == null) {
                logger.warn("엔피씨를 찾을수 없음:{}", npcTemplateId);
            } else {
                if (rs.getInt("count") == 0) {
                    return null;
                }

                L1Spawn spawn = new L1Spawn(npc);
                spawn.setId(rs.getInt("id"));
                spawn.setAmount(rs.getInt("count"));
                spawn.setLocX(rs.getInt("locx"));
                spawn.setLocY(rs.getInt("locy"));
                spawn.setRandomX(rs.getInt("randomx"));
                spawn.setRandomY(rs.getInt("randomy"));
                spawn.setLocX1(0);
                spawn.setLocY1(0);
                spawn.setLocX2(0);
                spawn.setLocY2(0);
                spawn.setHeading(rs.getInt("heading"));
                spawn.setMinRespawnDelay(rs.getInt("respawn_delay"));
                spawn.setMapId(rs.getShort("mapid"));
                spawn.setMovementDistance(rs.getInt("movement_distance"));
                spawn.setName(npc.getName());

                return spawn;
            }
        } catch (Exception e) {
            logger.error(e);
        }

        return null;
    }
}
