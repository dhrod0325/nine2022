package ks.model.trap;

import ks.constants.L1NpcConstants;
import ks.core.ObjectIdFactory;
import ks.core.datatables.npc.NpcTable;
import ks.core.storage.TrapStorage;
import ks.model.L1Location;
import ks.model.L1Npc;
import ks.model.L1World;
import ks.model.instance.L1NpcInstance;
import ks.model.instance.L1TrapInstance;
import ks.model.map.L1Map;
import ks.model.pc.L1PcInstance;
import ks.model.types.Point;
import ks.util.L1InstanceFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class L1MonsterTrap extends L1Trap {
    private static final Logger logger = LogManager.getLogger(L1MonsterTrap.class.getName());

    private final int count;

    private final L1Npc npcTemp;

    public L1MonsterTrap(TrapStorage storage) {
        super(storage);

        int npcId = storage.getInt("monsterNpcId");
        count = storage.getInt("monsterCount");
        npcTemp = NpcTable.getInstance().getTemplate(npcId);
    }

    private void addListIfPassable(List<Point> list, L1Map map, Point pt) {
        if (map.isPassable(pt)) {
            list.add(pt);
        }
    }

    private List<Point> getSpawnablePoints(L1Location loc) {
        List<Point> result = new ArrayList<>();
        L1Map m = loc.getMap();
        int x = loc.getX();
        int y = loc.getY();
        for (int i = 0; i < 5; i++) {
            addListIfPassable(result, m, new Point(5 - i + x, i + y));
            addListIfPassable(result, m, new Point(-(5 - i) + x, -i + y));
            addListIfPassable(result, m, new Point(-i + x, 5 - i + y));
            addListIfPassable(result, m, new Point(i + x, -(5 - i) + y));
        }
        return result;
    }

    private L1NpcInstance createNpc() {
        return L1InstanceFactory.createInstance(npcTemp);
    }

    private void spawn(L1Location loc) {
        L1NpcInstance npc = createNpc();
        npc.setId(ObjectIdFactory.getInstance().nextId());
        npc.getLocation().set(loc);
        npc.setHomeX(loc.getX());
        npc.setHomeY(loc.getY());

        L1World.getInstance().storeObject(npc);
        L1World.getInstance().addVisibleObject(npc);

        npc.onNpcAI();
        npc.getLight().turnOnOffLight();
        npc.startChat(L1NpcConstants.CHAT_TIMING_APPEARANCE);
    }

    @Override
    public void onTrod(L1PcInstance from, L1TrapInstance trap) {
        sendEffect(trap);

        List<Point> points = getSpawnablePoints(trap.getLocation());

        if (points.isEmpty()) {
            return;
        }

        try {
            int cnt = 0;
            while (true) {
                for (Point pt : points) {
                    spawn(new L1Location(pt, trap.getMap()));
                    cnt++;
                    if (count <= cnt) {
                        return;
                    }
                }
            }
        } catch (Exception e) {
            logger.error("L1MonsterTrap[]Error", e);
        }
    }
}
