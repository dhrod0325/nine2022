package ks.model.map;

import ks.constants.L1ActionCodes;
import ks.core.datatables.DoorSpawnTable;
import ks.core.datatables.MapFixKeyTable;
import ks.model.instance.L1DoorInstance;
import ks.model.types.Point;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class L1V1Map extends L1Map {
    private final Logger logger = LogManager.getLogger();

    private static final byte BITFLAG_IS_IMPASSABLE = (byte) 128;

    private int mapId;
    private int worldTopLeftX;
    private int worldTopLeftY;
    private int worldBottomRightX;
    private int worldBottomRightY;

    private byte[][] map;

    private boolean isUnderwater;
    private boolean isMarkAble;
    private boolean teleportAble;
    private boolean escapable;
    private boolean useResurrection;
    private boolean isUsePainWand;
    private boolean isEnabledDeathPenalty;
    private boolean isTakePets;
    private boolean isRecallPets;
    private boolean isUsableItem;
    private boolean isUsableSkill;

    public L1V1Map(int mapId, byte[][] map, int worldTopLeftX,
                   int worldTopLeftY, boolean underwater, boolean markAble,
                   boolean teleportAble, boolean escapable, boolean useResurrection,
                   boolean usePainWand, boolean enabledDeathPenalty, boolean takePets,
                   boolean recallPets, boolean usableItem, boolean usableSkill) {
        try {
            this.mapId = mapId;
            this.map = map;
            this.worldTopLeftX = worldTopLeftX;
            this.worldTopLeftY = worldTopLeftY;
            this.worldBottomRightX = worldTopLeftX + map.length - 1;
            this.worldBottomRightY = worldTopLeftY + map[0].length - 1;

            this.isUnderwater = underwater;
            this.isMarkAble = markAble;
            this.teleportAble = teleportAble;//
            this.escapable = escapable;
            this.useResurrection = useResurrection;
            this.isUsePainWand = usePainWand;
            this.isEnabledDeathPenalty = enabledDeathPenalty;
            this.isTakePets = takePets;
            this.isRecallPets = recallPets;
            this.isUsableItem = usableItem;
            this.isUsableSkill = usableSkill;
        } catch (Exception e) {
            logger.error("오류", e);
        }
    }

    public L1V1Map(L1V1Map map) {
        this.mapId = map.mapId;
        this.map = new byte[map.map.length][];

        for (int i = 0; i < map.map.length; i++) {
            this.map[i] = map.map[i].clone();
        }

        worldTopLeftX = map.worldTopLeftX;
        worldTopLeftY = map.worldTopLeftY;
        worldBottomRightX = map.worldBottomRightX;
        worldBottomRightY = map.worldBottomRightY;
    }

    @Override
    public byte[][] getMap() {
        return map;
    }

    public L1V1Map clone(int id) {
        L1V1Map map = new L1V1Map(this);

        map.mapId = id;
        map.isUnderwater = isUnderwater;
        map.isMarkAble = isMarkAble;
        map.teleportAble = teleportAble;
        map.escapable = escapable;
        map.useResurrection = useResurrection;
        map.isUsePainWand = isUsePainWand;
        map.isEnabledDeathPenalty = isEnabledDeathPenalty;
        map.isTakePets = isTakePets;
        map.isRecallPets = isRecallPets;
        map.isUsableItem = isUsableItem;
        map.isUsableSkill = isUsableSkill;

        return map;
    }

    private int accessTile(int x, int y) {
        if (!isInMap(x, y)) {
            return 0;
        }

        return map[x - worldTopLeftX][y - worldTopLeftY];
    }

    private int accessOriginalTile(int x, int y) {
        return accessTile(x, y) & (~BITFLAG_IS_IMPASSABLE);
    }

    private void setTile(int x, int y, int tile) {
        if (!isInMap(x, y)) {
            return;
        }

        map[x - worldTopLeftX][y - worldTopLeftY] = (byte) tile;
    }

    public byte[][] getRawTiles() {
        return map;
    }

    @Override
    public L1V1Map copyMap(int newMapId) {
        return clone(newMapId);
    }

    @Override
    public int getId() {
        return mapId;
    }

    @Override
    public int getX() {
        return worldTopLeftX;
    }

    @Override
    public int getY() {
        return worldTopLeftY;
    }

    @Override
    public int getWidth() {
        return worldBottomRightX - worldTopLeftX + 1;
    }

    @Override
    public int getHeight() {
        return worldBottomRightY - worldTopLeftY + 1;
    }

    @Override
    public int getTile(int x, int y) {
        short tile = map[x - worldTopLeftX][y - worldTopLeftY];

        if (0 != (tile & BITFLAG_IS_IMPASSABLE)) {
            return 300;
        }

        return accessOriginalTile(x, y);
    }

    public int getWorldTopLeftX() {
        return worldTopLeftX;
    }

    public int getWorldTopLeftY() {
        return worldTopLeftY;
    }

    @Override
    public int getOriginalTile(int x, int y) {
        return accessOriginalTile(x, y);
    }

    @Override
    public boolean isInMap(Point pt) {
        return isInMap(pt.getX(), pt.getY());
    }

    @Override
    public boolean isInMap(int x, int y) {
        if (mapId == 4 && (x < 32520 || y < 32070 || (y < 32190 && x < 33950))) {
            return false;
        }

        return (worldTopLeftX <= x && x <= worldBottomRightX && worldTopLeftY <= y && y <= worldBottomRightY);
    }

    @Override
    public boolean isPassable(Point pt) {
        return isPassable(pt.getX(), pt.getY());
    }

    @Override
    public boolean isPassable(int x, int y) {
        return isPassable(x, y - 1, 4) || isPassable(x + 1, y, 6) || isPassable(x, y + 1, 0) || isPassable(x - 1, y, 2);
    }

    @Override
    public boolean isPassable(Point pt, int heading) {
        return isPassable(pt.getX(), pt.getY(), heading);
    }

    @Override
    public boolean ismPassable(int x, int y, int heading) {
        switch (heading) {
            case 0:
                return (accessTile(x, y) & 8) > 0;
            case 1:
                return ((accessTile(x, y) & 8) > 0 && (accessTile(x, y - 1) & 4) > 0)
                        || ((accessTile(x, y) & 4) > 0 && (accessTile(x + 1, y) & 8) > 0);
            case 2:
                return (accessTile(x, y) & 4) > 0;
            case 3:
                return ((accessTile(x, y + 1) & 8) > 0 && (accessTile(x, y + 1) & 4) > 0)
                        || ((accessTile(x, y) & 4) > 0 && (accessTile(x + 1, y + 1) & 8) > 0);
            case 4:
                return (accessTile(x, y + 1) & 8) > 0;
            case 5:
                return ((accessTile(x, y + 1) & 8) > 0 && (accessTile(x - 1, y + 1) & 4) > 0)
                        || ((accessTile(x - 1, y) & 4) > 0 && (accessTile(x - 1,
                        y + 1) & 8) > 0);
            case 6:
                return (accessTile(x - 1, y) & 4) > 0;
            case 7:
                return ((accessTile(x, y) & 8) > 0 && (accessTile(x - 1, y - 1) & 4) > 0)
                        || ((accessTile(x - 1, y) & 4) > 0 && (accessTile(x - 1, y) & 8) > 0);
        }
        return false;
    }

    @Override
    public boolean isPassable(int x, int y, int heading) {
        int tile1 = accessTile(x, y);
        int tile2;

        switch (heading) {
            case 0:
                tile2 = accessTile(x, y - 1);
                break;
            case 1:
                tile2 = accessTile(x + 1, y - 1);
                break;
            case 2:
                tile2 = accessTile(x + 1, y);
                break;
            case 3:
                tile2 = accessTile(x + 1, y + 1);
                break;
            case 4:
                tile2 = accessTile(x, y + 1);
                break;
            case 5:
                tile2 = accessTile(x - 1, y + 1);
                break;
            case 6:
                tile2 = accessTile(x - 1, y);
                break;
            case 7:
                tile2 = accessTile(x - 1, y - 1);
                break;
            default:
                return false;
        }

        if (x >= 33433 && x <= 33438 && y == 32792 && mapId == 4) {
            return false;
        }

        if (x == 33428 && y == 32792 && mapId == 4) {
            return false;
        }

        if ((tile2 & BITFLAG_IS_IMPASSABLE) == BITFLAG_IS_IMPASSABLE) {
            return false;
        }

        switch (heading) {
            case 0: {
                return (tile1 & 0x02) == 0x02;
            }
            case 1: {
                int tile3 = accessTile(x, y - 1);
                int tile4 = accessTile(x + 1, y);
                return (tile3 & 0x01) == 0x01 || (tile4 & 0x02) == 0x02;
            }
            case 2: {
                return (tile1 & 0x01) == 0x01;
            }
            case 3: {
                int tile3 = accessTile(x, y + 1);
                return (tile3 & 0x01) == 0x01;
            }
            case 4: {
                return (tile2 & 0x02) == 0x02;
            }
            case 5: {
                return (tile2 & 0x01) == 0x01 || (tile2 & 0x02) == 0x02;
            }
            case 6: {
                return (tile2 & 0x01) == 0x01;
            }
            case 7: {
                int tile3 = accessTile(x - 1, y);
                return (tile3 & 0x02) == 0x02;
            }
            default:
                break;
        }

        return false;
    }

    @Override
    public void setPassable(Point pt, boolean isPassable) {
        setPassable(pt.getX(), pt.getY(), isPassable);
    }

    @Override
    public void setPassable(int x, int y, boolean isPassable) {
        if (isPassable) {
            setTile(x, y, (short) (accessTile(x, y) & (~BITFLAG_IS_IMPASSABLE)));
        } else {
            setTile(x, y, (short) (accessTile(x, y) | BITFLAG_IS_IMPASSABLE));
        }
    }

    @Override
    public boolean isSafetyZone(Point pt) {
        return isSafetyZone(pt.getX(), pt.getY());
    }

    @Override
    public boolean isSafetyZone(int x, int y) {
        int tile = accessOriginalTile(x, y);

        return (tile & 0x30) == 0x10;
    }

    @Override
    public boolean isCombatZone(Point pt) {
        return isCombatZone(pt.getX(), pt.getY());
    }

    @Override
    public boolean isCombatZone(int x, int y) {
        int tile = accessOriginalTile(x, y);

        return (tile & 0x30) == 0x20;
    }

    @Override
    public boolean isNormalZone(Point pt) {
        return isNormalZone(pt.getX(), pt.getY());
    }

    @Override
    public boolean isNormalZone(int x, int y) {
        int tile = accessOriginalTile(x, y);
        return (tile & 0x30) == 0x00;
    }

    @Override
    public boolean isArrowPassable(Point pt) {
        return isArrowPassable(pt.getX(), pt.getY());
    }

    @Override
    public boolean isArrowPassable(int x, int y) {
        return (accessOriginalTile(x, y) & 0x0e) != 0;
    }

    @Override
    public boolean isArrowPassable(Point pt, int heading) {
        return isArrowPassable(pt.getX(), pt.getY(), heading);
    }

    @Override
    public boolean isArrowPassable(int x, int y, int heading) {
        int tile1 = accessTile(x, y);
        int tile2;

        int newX;
        int newY;

        switch (heading) {
            case 0:
                tile2 = accessTile(x, y - 1);
                newX = x;
                newY = y - 1;
                break;
            case 1:
                tile2 = accessTile(x + 1, y - 1);
                newX = x + 1;
                newY = y - 1;
                break;
            case 2:
                tile2 = accessTile(x + 1, y);
                newX = x + 1;
                newY = y;
                break;
            case 3:
                tile2 = accessTile(x + 1, y + 1);
                newX = x + 1;
                newY = y + 1;
                break;
            case 4:
                tile2 = accessTile(x, y + 1);
                newX = x;
                newY = y + 1;
                break;
            case 5:
                tile2 = accessTile(x - 1, y + 1);
                newX = x - 1;
                newY = y + 1;
                break;
            case 6:
                tile2 = accessTile(x - 1, y);
                newX = x - 1;
                newY = y;
                break;
            case 7:
                tile2 = accessTile(x - 1, y - 1);
                newX = x - 1;
                newY = y - 1;
                break;
            default:
                return false;
        }

        if (isExistDoor(newX, newY)) {
            return false;
        }

        String key = String.valueOf(mapId) + newX + newY;


        switch (heading) {
            case 0: {
                return (tile1 & 0x08) == 0x08;
            }
            case 1: {
                int tile3 = accessTile(x, y - 1);
                int tile4 = accessTile(x + 1, y);
                return (tile3 & 0x04) == 0x04 || (tile4 & 0x08) == 0x08;
            }
            case 2: {
                return (tile1 & 0x04) == 0x04;
            }
            case 3: {
                int tile3 = accessTile(x, y + 1);
                return (tile3 & 0x04) == 0x04;
            }
            case 4: {
                return (tile2 & 0x08) == 0x08;
            }
            case 5: {
                return (tile2 & 0x04) == 0x04 || (tile2 & 0x08) == 0x08;
            }
            case 6: {
                return (tile2 & 0x04) == 0x04;
            }
            case 7: {
                int tile3 = accessTile(x - 1, y);
                return (tile3 & 0x08) == 0x08;
            }

            default:
                break;
        }

        return false;
    }

    @Override
    public boolean isUnderwater() {
        return isUnderwater;
    }

    @Override
    public boolean isMarkAble() {
        return isMarkAble;
    }

    @Override
    public boolean isTeleportAble() {
        return teleportAble;
    }

    @Override
    public void setTeleportAble(boolean teleportAble) {
        this.teleportAble = teleportAble;
    }

    @Override
    public boolean isEscapable() {
        return escapable;
    }

    @Override
    public boolean isUseResurrection() {
        return useResurrection;
    }

    @Override
    public boolean isUsePainWand() {
        return isUsePainWand;
    }

    @Override
    public boolean isEnabledDeathPenalty() {
        return isEnabledDeathPenalty;
    }

    @Override
    public boolean isTakePets() {
        return isTakePets;
    }

    @Override
    public boolean isRecallPets() {
        return isRecallPets;
    }

    @Override
    public boolean isUsableItem() {
        return isUsableItem;
    }

    @Override
    public boolean isUsableSkill() {
        return isUsableSkill;
    }

    @Override
    public boolean isFishingZone(int x, int y) {
        return accessOriginalTile(x, y) == 28;
    }

    @Override
    public boolean isExistDoor(int x, int y) {
        for (L1DoorInstance door : DoorSpawnTable.getInstance().getDoorList()) {
            if (mapId != door.getMapId()) {
                continue;
            }
            if (door.getOpenStatus() == L1ActionCodes.ACTION_Open) {
                continue;
            }
            if (door.isDead()) {
                continue;
            }

            int leftEdgeLocation = door.getLeftEdgeLocation();
            int rightEdgeLocation = door.getRightEdgeLocation();

            int size = rightEdgeLocation - leftEdgeLocation;

            if (size == 0) {
                if (x == door.getX() && y == door.getY()) {
                    return true;
                }
            } else {
                if (door.getDirection() == 0) {
                    for (int doorX = leftEdgeLocation; doorX <= rightEdgeLocation; doorX++) {
                        if (x == doorX && y == door.getY()) {
                            return true;
                        }
                    }
                } else {


                    for (int doorY = leftEdgeLocation; doorY <= rightEdgeLocation; doorY++) {
                        if (x == door.getX() && y == doorY) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean isCloseZone(int locX, int locY) {
        int tile = accessOriginalTile(locX, locY);

        if (tile == 12 || (tile & 0x30) == tile) {
            String key = String.valueOf(mapId) + locX + locY;
            return MapFixKeyTable.getInstance().isNotPass(key);
        }

        return false;
    }

    @Override
    public String toString(Point pt) {
        return "" + getOriginalTile(pt.getX(), pt.getY());
    }
}
