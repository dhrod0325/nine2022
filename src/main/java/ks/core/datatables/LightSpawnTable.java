package ks.core.datatables;

import ks.app.LineageAppContext;
import ks.app.aop.LogTime;
import ks.core.ObjectIdFactory;
import ks.core.datatables.npc.NpcTable;
import ks.model.L1Npc;
import ks.model.L1World;
import ks.model.instance.L1FieldObjectInstance;
import ks.util.common.SqlUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LightSpawnTable {
    private final Logger logger = LogManager.getLogger();

    public static LightSpawnTable getInstance() {
        return LineageAppContext.getBean(LightSpawnTable.class);
    }

    @LogTime
    public void load() {
        List<L1FieldObjectInstance> list = selectList();

        for (L1FieldObjectInstance field : list) {
            if (field == null)
                continue;

            L1World.getInstance().storeObject(field);
            L1World.getInstance().addVisibleObject(field);
        }
    }

    public List<L1FieldObjectInstance> selectList() {
        return SqlUtils.query("SELECT * FROM spawnlist_light", (rs, i) -> {
            L1Npc l1npc = NpcTable.getInstance().getTemplate(rs.getInt(2));

            if (l1npc != null) {
                try {
                    L1FieldObjectInstance field = (L1FieldObjectInstance) (NpcTable.getInstance().newNpcInstance(rs.getInt(2)));
                    field.setId(ObjectIdFactory.getInstance().nextId());
                    field.setX(rs.getInt("locx"));
                    field.setY(rs.getInt("locy"));
                    field.setMap((short) rs.getInt("mapid"));
                    field.setHomeX(field.getX());
                    field.setHomeY(field.getY());
                    field.setHeading(0);
                    field.setLightSize(l1npc.getLightSize());
                    field.getLight().turnOnOffLight();

                    return field;
                } catch (Exception e) {
                    logger.error("오류", e);
                }
            }

            return null;
        });
    }
}
