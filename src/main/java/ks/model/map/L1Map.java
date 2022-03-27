package ks.model.map;

import ks.model.L1Location;
import ks.model.types.Point;

import java.util.HashMap;
import java.util.Map;

public abstract class L1Map {
    public static final short MAP_2D = 631;
    public static final short MAP_USER_SHOP = 800;
    public static final short MAP_FISHING = 5490;
    public static final short MAP_ICE_CASTLE1 = 2101;
    public static final short MAP_ICE_CASTLE2 = 2151;

    private final Map<L1Location, Boolean> attackAbleMap = new HashMap<>();

    protected L1Map() {
    }

    public byte[][] getMap() {
        return null;
    }

    public abstract int getId();

    public abstract int getX();

    public abstract int getY();

    public abstract int getWidth();

    public abstract int getHeight();

    public abstract int getTile(int x, int y);

    public abstract int getOriginalTile(int x, int y);

    public abstract boolean isInMap(Point pt);

    public abstract boolean isInMap(int x, int y);

    public abstract boolean isPassable(Point pt);

    public abstract boolean isPassable(int x, int y);

    public abstract boolean isPassable(Point pt, int heading);

    public abstract boolean isPassable(int x, int y, int heading);

    public abstract boolean ismPassable(int x, int y, int heading);

    public abstract void setPassable(Point pt, boolean isPassable);

    public abstract void setPassable(int x, int y, boolean isPassable);

    public abstract boolean isSafetyZone(Point pt);

    public abstract boolean isSafetyZone(int x, int y);

    public abstract boolean isCombatZone(Point pt);

    public abstract boolean isCombatZone(int x, int y);

    public abstract boolean isNormalZone(Point pt);

    public abstract boolean isNormalZone(int x, int y);

    public abstract boolean isArrowPassable(Point pt);

    public abstract boolean isArrowPassable(int x, int y);

    public abstract boolean isArrowPassable(Point pt, int heading);

    public abstract boolean isArrowPassable(int x, int y, int heading);

    public abstract boolean isUnderwater();

    public abstract boolean isMarkAble();

    public abstract boolean isTeleportAble();

    public abstract void setTeleportAble(boolean teleportAble);

    public abstract boolean isEscapable();

    public abstract boolean isUseResurrection();

    public abstract boolean isUsePainWand();

    public abstract boolean isEnabledDeathPenalty();

    public abstract boolean isTakePets();

    public abstract boolean isRecallPets();

    public abstract boolean isUsableItem();

    public abstract boolean isUsableSkill();

    public abstract boolean isFishingZone(int x, int y);

    public abstract boolean isExistDoor(int x, int y);

    public abstract boolean isCloseZone(int x, int y);

    public abstract L1V1Map copyMap(int a);

    public abstract String toString(Point pt);

    public boolean isNull() {
        return false;
    }

    public int getBaseMapId() {
        return 0;
    }

    public boolean isAttackAble(int x, int y) {
        L1Location location = new L1Location();
        location.setX(x);
        location.setY(y);
        location.setMap(this);

        Boolean result = attackAbleMap.get(location);

        if (result == null) {
            result = true;
        }

        return result;
    }

    public void setAttackAble(int x, int y, boolean flag) {
        L1Location location = new L1Location();
        location.setX(x);
        location.setY(y);
        location.setMap(this);

        attackAbleMap.put(location, flag);
    }
}