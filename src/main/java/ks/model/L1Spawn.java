package ks.model;

import ks.app.LineageAppContext;
import ks.app.config.prop.CodeConfig;
import ks.constants.L1NpcConstants;
import ks.core.ObjectIdFactory;
import ks.core.datatables.DoorSpawnTable;
import ks.model.instance.L1DoorInstance;
import ks.model.instance.L1ItemInstance;
import ks.model.instance.L1MonsterInstance;
import ks.model.instance.L1NpcInstance;
import ks.model.pc.L1PcInstance;
import ks.model.types.Point;
import ks.util.L1InstanceFactory;
import ks.util.common.random.RandomUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ScheduledFuture;

public class L1Spawn {
    private static final Logger logger = LogManager.getLogger();

    private static final int SPAWN_TYPE_PC_AROUND = 1;
    private static final int SPAWN_TYPE_AREA = 2;
    private static final int SPAWN_TYPE_RANDOM = 3;
    private static final int SPAWN_TYPE_DEFAULT = 4;

    private static final int PC_AROUND_DISTANCE = 30;

    private final L1Npc template;
    protected int id;
    protected String location;
    protected int amount;
    protected int npcId;
    protected int groupId;
    protected int locX;
    protected int locY;
    protected int randomX;
    protected int randomY;
    protected int locX1;
    protected int locY1;
    protected int locX2;
    protected int locY2;
    protected int heading;
    protected int minRespawnDelay;
    protected int maxRespawnDelay;
    protected short mapId;
    protected boolean respawnScreen;
    protected int movementDistance;
    protected boolean rest;
    protected int nearSpawn;
    protected Map<Integer, Point> homePoint = new HashMap<>();
    protected String name;
    private int doorId;
    private int spawnNum;

    private int totalSpawnCount = 0;
    private int totalAdenaCount = 0;

    public L1Spawn(L1Npc mobTemplate) throws SecurityException, ClassNotFoundException {
        template = mobTemplate;
    }

    public void setDoorId(int doorId) {
        this.doorId = doorId;
    }

    public int getDoorId() {
        return doorId;
    }

    public L1Npc getTemplate() {
        return template;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public short getMapId() {
        return mapId;
    }

    public void setMapId(short _mapid) {
        this.mapId = _mapid;
    }

    public boolean isRespawnScreen() {
        return respawnScreen;
    }

    public void setRespawnScreen(boolean flag) {
        respawnScreen = flag;
    }

    public int getMovementDistance() {
        return movementDistance;
    }

    public void setMovementDistance(int i) {
        movementDistance = i;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int i) {
        groupId = i;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getLocX() {
        return locX;
    }

    public void setLocX(int locx) {
        this.locX = locx;
    }

    public int getLocY() {
        return locY;
    }

    public void setLocY(int locy) {
        this.locY = locy;
    }

    public int getNpcId() {
        return npcId;
    }

    public void setNpcId(int npcid) {
        this.npcId = npcid;
    }

    public int getHeading() {
        return heading;
    }

    public void setHeading(int heading) {
        this.heading = heading;
    }

    public int getRandomX() {
        return randomX;
    }

    public void setRandomX(int randomX) {
        this.randomX = randomX;
    }

    public int getRandomY() {
        return randomY;
    }

    public void setRandomY(int randomY) {
        this.randomY = randomY;
    }

    public int getLocX1() {
        return locX1;
    }

    public void setLocX1(int locx1) {
        this.locX1 = locx1;
    }

    public int getLocY1() {
        return locY1;
    }

    public void setLocY1(int locy1) {
        this.locY1 = locy1;
    }

    public int getLocX2() {
        return locX2;
    }

    public void setLocX2(int locx2) {
        this.locX2 = locx2;
    }

    public int getLocY2() {
        return locY2;
    }

    public void setLocY2(int locy2) {
        this.locY2 = locy2;
    }

    public void setMinRespawnDelay(int i) {
        minRespawnDelay = i;
    }

    public void setMaxRespawnDelay(int i) {
        maxRespawnDelay = i;
    }

    protected int calcRespawnDelay() {
        int respawnDelay = minRespawnDelay * 1000;

        int diff = maxRespawnDelay - minRespawnDelay;

        if (diff > 0) {
            respawnDelay += RandomUtils.nextInt(diff) * 1000;
        }

        return respawnDelay;
    }

    public ScheduledFuture<?> respawn(int spawnNumber, int objectId) {
        return LineageAppContext.spawnTaskScheduler().schedule(() -> doSpawn(spawnNumber, objectId, true), Instant.now().plusMillis(calcRespawnDelay()));
    }

    public void init() {
        while (spawnNum < amount) {
            doSpawn(++spawnNum, 0, false);
        }
    }

    public boolean isUseHomePoint() {
        return CodeConfig.SPAWN_HOME_POINT
                && CodeConfig.SPAWN_HOME_POINT_COUNT <= amount
                && CodeConfig.SPAWN_HOME_POINT_DELAY >= minRespawnDelay
                && isAreaSpawn();
    }

    public L1NpcInstance doSpawn(int spawnNumber, int objectId, boolean isReUse) {
        try {
            int newLocX = getLocX();
            int newLocY = getLocY();

            L1NpcInstance npc = L1InstanceFactory.createInstance(template);

            if (objectId == 0) {
                npc.setId(ObjectIdFactory.getInstance().nextId());
            } else {
                npc.setId(objectId);
            }

            int heading = 5;

            if (0 <= getHeading() && getHeading() <= 7) {
                heading = getHeading();
            }

            npc.setHeading(heading);

            int npcId = npc.getTemplate().getNpcId();

            if (npcId == 45488 && getMapId() == 9) {
                npc.setMap((short) (getMapId() + RandomUtils.nextInt(2)));
            } else if (npcId == 45601 && getMapId() == 11) {
                npc.setMap((short) (getMapId() + RandomUtils.nextInt(3)));
            } else {
                npc.setMap(getMapId());
            }

            npc.setMovementDistance(getMovementDistance());
            npc.setRest(isRest());

            if (npc instanceof L1MonsterInstance) {
                if (getDoorId() > 0) {
                    L1DoorInstance door = DoorSpawnTable.getInstance().getDoor(getDoorId());
                    if (door != null) {
                        synchronized (this) {
                            if (door.isOpen()) {
                                door.setDead(false);
                                door.close();
                            }
                        }
                    }
                }
            }

            int spawnType = getSpawnType();

            for (int tryCount = 0; tryCount < 50; tryCount++) {
                switch (spawnType) {
                    case SPAWN_TYPE_PC_AROUND: {
                        List<L1PcInstance> players = new ArrayList<>();

                        for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
                            if (getMapId() == pc.getMapId()) {
                                players.add(pc);
                            }
                        }

                        L1Location loc;

                        if (!players.isEmpty()) {
                            Collections.shuffle(players);

                            L1PcInstance pc = players.get(0);
                            loc = pc.getLocation().randomLocation(PC_AROUND_DISTANCE, false);
                            players.clear();
                        } else {
                            loc = L1Location.randomLocationWithArea(getLocX1(), getLocX2(), getLocY1(), getLocY2(), mapId);
                        }

                        newLocX = loc.getX();
                        newLocY = loc.getY();

                        break;
                    }

                    case SPAWN_TYPE_AREA: {
                        Point pt = homePoint.get(spawnNumber);

                        L1Location loc;

                        if (isUseHomePoint() && pt != null && isReUse) {
                            loc = new L1Location(pt, getMapId()).randomLocation(CodeConfig.SPAWN_HOME_POINT_RANGE, false);
                        } else {
                            loc = L1Location.randomLocationWithArea(getLocX1(), getLocX2(), getLocY1(), getLocY2(), mapId);
                        }

                        newLocX = loc.getX();
                        newLocY = loc.getY();

                        break;
                    }
                    case SPAWN_TYPE_RANDOM: {
                        newLocX = getLocX() + RandomUtils.nextInt(-getRandomX(), getRandomX());
                        newLocY = getLocY() + RandomUtils.nextInt(-getRandomY(), getRandomY());
                        break;
                    }
                    case SPAWN_TYPE_DEFAULT: {
                        newLocX = getLocX();
                        newLocY = getLocY();
                        break;
                    }
                }

                npc.setX(newLocX);
                npc.setHomeX(newLocX);
                npc.setY(newLocY);
                npc.setHomeY(newLocY);

                if (npc.getMap().isInMap(npc.getLocation()) && npc.getMap().isPassable(npc.getLocation())) {
                    if (npc instanceof L1MonsterInstance) {
                        if (isRespawnScreen()) {
                            break;
                        }

                        L1MonsterInstance mobTemp = (L1MonsterInstance) npc;

                        if (L1World.getInstance().getVisiblePlayer(mobTemp).isEmpty()) {
                            break;
                        }

                        LineageAppContext.spawnTaskScheduler().schedule(() -> doSpawn(spawnNumber, objectId, false), Instant.now().plusMillis(3000));

                        return npc;
                    }
                }

                tryCount++;
            }

            if (npc instanceof L1MonsterInstance) {
                L1MonsterInstance mon = (L1MonsterInstance) npc;
                mon.initHide();
                mon.reSetting();
            }

            npc.setSpawn(this);
            npc.setRespawn(minRespawnDelay != 0 && maxRespawnDelay != 0);
            npc.setSpawnNumber(spawnNumber);

            if (!isReUse && isUseHomePoint()) {
                Point pt = new Point(npc.getX(), npc.getY());
                homePoint.put(spawnNumber, pt);
            }

            L1World.getInstance().storeObject(npc);
            L1World.getInstance().addVisibleObject(npc);

            if (getGroupId() != 0) {
                L1MobGroupSpawn.getInstance().doSpawn(npc, getGroupId(), isRespawnScreen(), true);
            }

            npc.getLight().turnOnOffLight();
            npc.startChat(L1NpcConstants.CHAT_TIMING_APPEARANCE);

            totalSpawnCount++;

            L1ItemInstance adena = npc.getInventory().getAdena();

            if (adena != null) {
                totalAdenaCount += adena.getCount();
            }

            logger.trace("spawnType :{} mobName:{} spawnName:{} spawnX:{} ,spawnY:{}", spawnType, npc.getName(), getName(), newLocX, newLocY);

            return npc;
        } catch (Exception e) {
            logger.error(e);
        }

        return null;
    }

    public int getTotalAdenaCount() {
        return totalAdenaCount;
    }

    public int getTotalSpawnCount() {
        return totalSpawnCount;
    }

    public boolean isRest() {
        return rest;
    }

    public void setRest(boolean flag) {
        rest = flag;
    }

    private int getNearSpawn() {
        return nearSpawn;
    }

    public void setNearSpawn(int type) {
        nearSpawn = type;
    }

    public boolean isAreaSpawn() {
        return getLocX1() != 0
                && getLocY1() != 0
                && getLocX2() != 0
                && getLocY2() != 0;
    }

    public boolean isRandomSpawn() {
        return getRandomX() != 0 || getRandomY() != 0;
    }

    public int getSpawnType() {
        if (getNearSpawn() == 1 && isAreaSpawn()) {
            return SPAWN_TYPE_PC_AROUND;
        } else if (isAreaSpawn()) {
            return SPAWN_TYPE_AREA;
        } else if (isRandomSpawn()) {
            return SPAWN_TYPE_RANDOM;
        } else {
            return SPAWN_TYPE_DEFAULT;
        }
    }

    public int getSpawnNum() {
        return spawnNum;
    }
}
