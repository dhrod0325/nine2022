package ks.model;

import ks.model.map.L1Map;
import ks.model.map.L1NullMap;
import ks.model.map.L1WorldMap;
import ks.model.types.Point;
import ks.util.common.random.RandomUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class L1Location extends Point {
    private static final Logger logger = LogManager.getLogger(L1Location.class.getName());

    protected L1Map map = new L1NullMap();

    public L1Location() {
        super();
    }

    public L1Location(L1Location loc) {
        this(loc.x, loc.y, loc.map);
    }

    public L1Location(int x, int y, int mapId) {
        super(x, y);
        setMap(mapId);
    }

    public L1Location(int x, int y, L1Map map) {
        super(x, y);
        this.map = map;
    }

    public L1Location(Point pt, int mapId) {
        super(pt);
        setMap(mapId);
    }

    public L1Location(Point pt, L1Map map) {
        super(pt);
        this.map = map;
    }

    public static L1Location randomLocation(L1Location baseLocation, int min, int max, boolean isRandomTeleport) {
        return randomLocation(baseLocation.getX(), baseLocation.getY(), baseLocation.getMap(), (short) baseLocation.getMapId(), min, max, isRandomTeleport);
    }

    public static L1Location randomLocationWithArea(int x1, int x2, int y1, int y2, int mapId) {
        int rangeX = x2 - x1;
        int rangeY = y2 - y1;

        L1Location loc = new L1Location();
        loc.setMap(mapId);

        while (!loc.getMap().isPassable(loc)) {
            loc.setX(RandomUtils.nextInt(rangeX) + x1);
            loc.setY(RandomUtils.nextInt(rangeY) + y1);
        }

        return loc;
    }

    public static L1Location randomLocation(int x, int y, L1Map maps, short mapid, int min, int max, boolean isRandomTeleport) {
        if (min > max) {
            throw new IllegalArgumentException("min > max가 되는 인수는 무효");
        }

        if (max <= 0) {
            return new L1Location(x, y, mapid);
        }

        if (min < 0) {
            min = 0;
        }

        L1Location newLocation = new L1Location();

        int newX;
        int newY;

        newLocation.setMap(maps);

        int locX1 = x - max;
        int locX2 = x + max;
        int locY1 = y - max;
        int locY2 = y + max;

        // map 범위
        int mapX1 = maps.getX();
        int mapX2 = mapX1 + maps.getWidth();
        int mapY1 = maps.getY();
        int mapY2 = mapY1 + maps.getHeight();

        // 최대에서도 맵의 범위내까지 보정
        if (locX1 < mapX1) {
            locX1 = mapX1;
        }

        if (locX2 > mapX2) {
            locX2 = mapX2;
        }
        if (locY1 < mapY1) {
            locY1 = mapY1;
        }
        if (locY2 > mapY2) {
            locY2 = mapY2;
        }

        int diffX = locX2 - locX1; // x방향
        int diffY = locY2 - locY1; // y방향

        diffX = Math.max(diffX, 0);
        diffY = Math.max(diffY, 0);

        int amax = (int) Math.pow(1 + (max * 2), 2);
        int amin = (min == 0) ? 0 : (int) Math.pow(1 + ((min - 1) * 2), 2);
        int trialLimit = 200 * amax / (amax - amin);

        for (int i = 0; i <= trialLimit; i++) {
            newX = locX1 + RandomUtils.nextInt(diffX + 1);
            newY = locY1 + RandomUtils.nextInt(diffY + 1);

            newLocation.set(newX, newY);

            int a = Math.max(Math.abs(newLocation.getX() - x), Math.abs(newLocation.getY() - y));

            if (a < min) {
                continue;
            }

            if (isRandomTeleport) {
                if (L1CastleLocation.checkInAllWarArea(newX, newY, mapid)) {
                    continue;
                }

                if (L1HouseLocation.isInHouse(newX, newY, mapid)) {
                    continue;
                }
            }

            if (maps.isInMap(newX, newY) && maps.isPassable(newX, newY)) {
                break;
            }

            if (i == trialLimit) {
                newLocation.set(x, y);
            }
        }

        return newLocation;
    }

    public void set(L1Location loc) {
        map = loc.map;
        x = loc.x;
        y = loc.y;
    }

    public void set(int x, int y, int mapId) {
        set(x, y);
        setMap(mapId);
    }

    public void set(int x, int y, L1Map map) {
        set(x, y);
        this.map = map;
    }

    public void set(Point pt, int mapId) {
        set(pt);
        setMap(mapId);
    }

    public void set(Point pt, L1Map map) {
        set(pt);
        this.map = map;
    }

    public L1Map getMap() {
        return map;
    }

    public void setMap(L1Map map) {
        this.map = map;
    }

    public void setMap(int mapId) {
        map = L1WorldMap.getInstance().getMap((short) mapId);
    }

    public int getMapId() {
        return map.getId();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof L1Location)) {
            return false;
        }

        L1Location loc = (L1Location) obj;

        return (this.getMap() == loc.getMap()) && (this.getX() == loc.getX()) && (this.getY() == loc.getY());
    }

    @Override
    public int hashCode() {
        return 7 * map.getId() + super.hashCode();
    }

    @Override
    public String toString() {
        return String.format("(%d, %d) on %d", x, y, map.getId());
    }

    public L1Location randomLocation(int max) {
        return randomLocation(0, max, false);
    }

    public L1Location randomLocation(int max, boolean isRandomTeleport) {
        return randomLocation(0, max, isRandomTeleport);
    }

    public L1Location randomLocation(int min, int max, boolean isRandomTeleport) {
        return randomLocation(this, min, max, isRandomTeleport);
    }
}
