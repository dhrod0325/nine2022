package ks.core.datatables;

import ks.app.LineageAppContext;
import ks.app.aop.LogTime;
import ks.core.ObjectIdFactory;
import ks.core.datatables.npc.NpcTable;
import ks.model.L1Npc;
import ks.model.L1World;
import ks.model.instance.L1ModelInstance;
import ks.util.common.SqlUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class ModelSpawnTable {
    private static final Logger logger = LogManager.getLogger(ModelSpawnTable.class.getName());

    public static ModelSpawnTable getInstance() {
        return LineageAppContext.getBean(ModelSpawnTable.class);
    }

    @LogTime
    public void load() {
        SqlUtils.query("SELECT * FROM spawnlist_model", (rs, i) -> {
            L1Npc l1npc = NpcTable.getInstance().getTemplate(rs.getInt(2));

            if (l1npc != null) {
                L1ModelInstance field;
                try {
                    field = (L1ModelInstance) (NpcTable.getInstance().newNpcInstance(rs.getInt(2)));
                    field.setId(ObjectIdFactory.getInstance().nextId());
                    field.setX(rs.getInt("locx"));
                    field.setY(rs.getInt("locy"));
                    field.setMap((short) rs.getInt("mapid"));
                    field.setHomeX(field.getX());
                    field.setHomeY(field.getY());
                    field.setHeading(rs.getInt("heading"));
                    field.setLightSize(l1npc.getLightSize());
                    field.getLight().turnOnOffLight();

                    L1World.getInstance().addVisibleObject(field);

                } catch (Exception e) {
                    logger.error(e);
                }
            }

            return null;
        });
    }
}
