package ks.core.datatables;

import ks.app.LineageAppContext;
import ks.app.aop.LogTime;
import ks.core.ObjectIdFactory;
import ks.core.datatables.npc.NpcTable;
import ks.model.L1Npc;
import ks.model.L1World;
import ks.model.instance.L1FurnitureInstance;
import ks.util.common.SqlUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class FurnitureSpawnTable {
    private static final Logger logger = LogManager.getLogger(FurnitureSpawnTable.class);

    public static FurnitureSpawnTable getInstance() {
        return LineageAppContext.getBean(FurnitureSpawnTable.class);
    }

    @LogTime
    public void load() {
        SqlUtils.query("SELECT * FROM spawnlist_furniture", (rs, i) -> {
            try {
                L1Npc npc = NpcTable.getInstance().getTemplate(rs.getInt(2));
                if (npc != null) {
                    L1FurnitureInstance furniture = new L1FurnitureInstance(npc);
                    furniture.setId(ObjectIdFactory.getInstance().nextId());

                    furniture.setItemObjId(rs.getInt(1));
                    furniture.setX(rs.getInt(3));
                    furniture.setY(rs.getInt(4));
                    furniture.setMap((short) rs.getInt(5));
                    furniture.setHomeX(furniture.getX());
                    furniture.setHomeY(furniture.getY());
                    furniture.setHeading(0);

                    L1World.getInstance().storeObject(furniture);
                    L1World.getInstance().addVisibleObject(furniture);
                }
            } catch (Exception e) {
                logger.error(e);
            }

            return null;
        });
    }

    public void insertFurniture(L1FurnitureInstance furniture) {
        SqlUtils.update("INSERT INTO spawnlist_furniture SET item_obj_id=?, npcid=?, locx=?, locy=?, mapid=?, house_Id=?",
                furniture.getItemObjId(),
                furniture.getTemplate().getNpcId(),
                furniture.getX(),
                furniture.getY(),
                furniture.getMapId()
        );
    }

    public void deleteFurniture(L1FurnitureInstance furniture) {
        SqlUtils.update("DELETE FROM spawnlist_furniture WHERE item_obj_id=?", furniture.getItemObjId());
    }
}
