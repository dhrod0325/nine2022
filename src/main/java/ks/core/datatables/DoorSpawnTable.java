package ks.core.datatables;

import ks.app.LineageAppContext;
import ks.app.aop.LogTime;
import ks.core.ObjectIdFactory;
import ks.core.datatables.npc.NpcTable;
import ks.model.L1Npc;
import ks.model.L1World;
import ks.model.instance.L1DoorInstance;
import ks.util.common.SqlUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class DoorSpawnTable {
    private static final Logger logger = LogManager.getLogger(DoorSpawnTable.class);

    private final Map<Integer, L1DoorInstance> doorList = new ConcurrentHashMap<>();

    public static DoorSpawnTable getInstance() {
        return LineageAppContext.getBean(DoorSpawnTable.class);
    }

    @LogTime
    public void load() {
        doorList.clear();

        List<L1DoorInstance> list = selectList();

        for (L1DoorInstance door : list) {
            if (door == null) {
                continue;
            }

            doorList.put(door.getId(), door);

            L1World.getInstance().storeObject(door);
            L1World.getInstance().addVisibleObject(door);
        }
    }

    public List<L1DoorInstance> selectList() {
        return SqlUtils.query("SELECT * FROM spawnlist_door", (rs, i) -> {
            try {
                L1Npc npc = NpcTable.getInstance().getTemplate(81158);

                if (npc != null) {
                    L1DoorInstance door = new L1DoorInstance(npc);
                    door.setId(ObjectIdFactory.getInstance().nextId());

                    door.setDoorId(rs.getInt("id"));
                    door.getGfxId().setGfxId(rs.getInt("gfxid"));
                    door.setX(rs.getInt("locx"));
                    door.setY(rs.getInt("locy"));

                    short mapId = rs.getShort("mapid");
                    door.setMap(mapId);

                    door.setHomeX(rs.getInt("locx"));
                    door.setHomeY(rs.getInt("locy"));
                    door.setDirection(rs.getInt("direction"));
                    door.setLeftEdgeLocation(rs.getInt("left_edge_location"));
                    door.setRightEdgeLocation(rs.getInt("right_edge_location"));
                    door.setMaxHp(rs.getInt("hp"));
                    door.setCurrentHp(rs.getInt("hp"));
                    door.setKeeperId(rs.getInt("keeper"));
                    door.isPassAbleDoor(false);
                    door.setAutoCloseTime(rs.getInt("auto_close_time"));
                    door.setAutoCloseNotOpenAble(rs.getInt("auto_close_not_open_able"));
                    door.setGiveItemId(rs.getInt("give_item_id"));

                    return door;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        });
    }

    public Collection<L1DoorInstance> getDoorList() {
        return doorList.values();
    }

    public L1DoorInstance getDoor(int npcId) {
        L1DoorInstance sTemp = null;

        if (npcId <= 0)
            return null;

        for (L1DoorInstance door : doorList.values()) {
            if (door.getDoorId() == npcId) {
                sTemp = door;
                break;
            }
        }

        return sTemp;
    }
}
