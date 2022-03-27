package ks.system.bossTraning;

import ks.app.LineageAppContext;
import ks.core.ObjectIdFactory;
import ks.core.datatables.npc.NpcTable;
import ks.model.L1Object;
import ks.model.L1World;
import ks.model.instance.L1NpcInstance;
import ks.model.map.L1WorldMap;
import ks.model.pc.L1PcInstance;
import ks.util.L1TeleportUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Component
public class BossTrainingSystem {
    private final Logger logger = LogManager.getLogger();

    private static final int MAX_MAP_SIZE = 19;

    private static final List<Integer> list = new ArrayList<>();

    public static BossTrainingSystem getInstance() {
        return LineageAppContext.getBean(BossTrainingSystem.class);
    }

    public void startRaid(int mapId) {
        if (mapId != 1400) {
            L1WorldMap.getInstance().cloneMap(1400, mapId);
        }

        fillSpawn(mapId);

        list.add(mapId);

        LineageAppContext.commonTaskScheduler().schedule(() -> removeRoom(mapId), Instant.now().plusMillis(1000 * 60 * 120));
    }

    public void removeRoom(int mapId) {
        if (list.contains(mapId)) {
            list.remove(mapId);

            for (L1Object obj : L1World.getInstance().getVisibleObjects(mapId)) {
                if ((obj instanceof L1NpcInstance)) {
                    L1NpcInstance npc = (L1NpcInstance) obj;
                    npc.deleteMe();
                }

                if ((obj instanceof L1PcInstance)) {
                    L1PcInstance pc = (L1PcInstance) obj;
                    L1TeleportUtils.teleportToGiran(pc);
                }
            }

            BossTrainingTable.getInstance().deleteByKeyId(mapId);
        }
    }

    public int generateMapId() {
        int mapId = 1400;

        if (list.isEmpty()) {
            return mapId;
        }

        do {
            mapId++;
        } while (list.contains(mapId));

        return mapId;
    }

    public void fillSpawn(int mapId) {
        try {
            L1NpcInstance npc = NpcTable.getInstance().newNpcInstance(460000179);
            npc.setId(ObjectIdFactory.getInstance().nextId());
            npc.setX(32902);
            npc.setY(32818);
            npc.setMap((short) mapId);
            npc.setHomeX(npc.getX());
            npc.setHomeY(npc.getY());
            npc.setHeading(5);
            npc.setLightSize(0);
            npc.getLight().turnOnOffLight();
            L1World.getInstance().storeObject(npc);
            L1World.getInstance().addVisibleObject(npc);
        } catch (Exception e) {
            logger.error("오류", e);
        }
    }

    public boolean isFull() {
        return list.size() == MAX_MAP_SIZE;
    }

}