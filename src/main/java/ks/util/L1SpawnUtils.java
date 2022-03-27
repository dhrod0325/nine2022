package ks.util;

import ks.constants.L1NpcConstants;
import ks.core.ObjectIdFactory;
import ks.core.datatables.npc.NpcTable;
import ks.model.L1Location;
import ks.model.L1World;
import ks.model.instance.L1MonsterInstance;
import ks.model.instance.L1NpcInstance;
import ks.model.pc.L1PcInstance;
import ks.scheduler.npc.NpcDeleteScheduler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class L1SpawnUtils {
    private static final Logger logger = LogManager.getLogger();

    public static L1NpcInstance spawn(L1PcInstance pc, int npcId, int randomRange, long deleteMillSecond) {
        return spawn(pc.getX(), pc.getY(), pc.getMapId(), pc.getHeading(), npcId, randomRange, deleteMillSecond, null);
    }

    public static L1NpcInstance spawn(int x, int y, short map, int npcId, int randomRange, long deleteMillSecond) {
        return spawn(x, y, map, npcId, randomRange, deleteMillSecond, null);
    }

    public static L1NpcInstance spawn(int x, int y, short map, int npcId, int randomRange, long deleteMillSecond, NpcDeleteScheduler.NpcDeleteCallBack callBack) {
        return spawn(x, y, map, 5, npcId, randomRange, deleteMillSecond, callBack);
    }

    public static L1NpcInstance randomSpawn(int npcId, int x1, int x2, int y1, int y2, short map,
                                            long deleteMillSecond,
                                            NpcDeleteScheduler.NpcDeleteCallBack callBack) {
        try {
            L1NpcInstance npc = NpcTable.getInstance().newNpcInstance(npcId);
            npc.setId(ObjectIdFactory.getInstance().nextId());
            npc.setMap(map);

            L1Location loc = L1Location.randomLocationWithArea(x1, x2, y1, y2, map);

            npc.setX(loc.getX());
            npc.setHomeX(loc.getX());

            npc.setY(loc.getY());
            npc.setHomeY(loc.getY());

            npc.setHeading(5);
            npc.setRespawn(false);

            L1World.getInstance().storeObject(npc);
            L1World.getInstance().addVisibleObject(npc);

            npc.getLight().turnOnOffLight();
            npc.startChat(L1NpcConstants.CHAT_TIMING_APPEARANCE); // 채팅 개시

            if (npc instanceof L1MonsterInstance) {
                L1MonsterInstance mon = (L1MonsterInstance) npc;
                mon.reSetting();
            }

            if (deleteMillSecond > 0) {
                NpcDeleteScheduler.getInstance().addNpcDelete(npc, deleteMillSecond, callBack);
            }

            return npc;
        } catch (Exception e) {
            logger.error("오류", e);
        }

        return null;
    }

    public static L1NpcInstance spawn(int x, int y, short map, int heading, int npcId, int randomRange, long deleteMillSecond, NpcDeleteScheduler.NpcDeleteCallBack callBack) {
        try {
            L1NpcInstance npc = NpcTable.getInstance().newNpcInstance(npcId);
            npc.setId(ObjectIdFactory.getInstance().nextId());
            npc.setMap(map);

            if (randomRange == 0) {
                npc.setLocation(x, y, map);
            } else {
                for (int i = 0; i <= 50; i++) {
                    npc.setX(x + (int) (Math.random() * randomRange) - (int) (Math.random() * randomRange));
                    npc.setY(y + (int) (Math.random() * randomRange) - (int) (Math.random() * randomRange));

                    if (npc.getMap().isInMap(npc.getLocation()) && npc.getMap().isPassable(npc.getLocation())) {
                        break;
                    }

                    if (i == 50) {
                        npc.getLocation().set(x, y, map);
                    }

                    Thread.sleep(1);
                }
            }

            npc.setHomeX(npc.getX());
            npc.setHomeY(npc.getY());

            npc.setHeading(heading);
            npc.setRespawn(false);

            L1World.getInstance().storeObject(npc);
            L1World.getInstance().addVisibleObject(npc);

            npc.getLight().turnOnOffLight();
            npc.startChat(L1NpcConstants.CHAT_TIMING_APPEARANCE); // 채팅 개시

            if (npc instanceof L1MonsterInstance) {
                L1MonsterInstance mon = (L1MonsterInstance) npc;
                mon.reSetting();
            }

            if (deleteMillSecond > 0) {
                NpcDeleteScheduler.getInstance().addNpcDelete(npc, deleteMillSecond, callBack);
            }

            return npc;
        } catch (Exception e) {
            logger.error("오류", e);
        }

        return null;
    }


}
