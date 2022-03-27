package ks.core.datatables;

import ks.model.map.L1WorldMap;
import ks.util.common.SqlUtils;

import java.util.ArrayList;
import java.util.List;

public class MapFixKeyTable {
    private static final MapFixKeyTable instance = new MapFixKeyTable();

    private final List<String> notPassKeys = new ArrayList<>();
    private final List<String> passKeys = new ArrayList<>();
    private final List<String> arrowNotPassKeys = new ArrayList<>();

    private MapFixKeyTable() {
        load();
    }

    public static MapFixKeyTable getInstance() {
        return instance;
    }

    public boolean isNotPass(String key) {
        return notPassKeys.contains(key);
    }

    public boolean isNotArrowPass(String key) {
        return arrowNotPassKeys.contains(key);
    }

    public void initMaps() {
        L1WorldMap worldMap = L1WorldMap.getInstance();

        short[] enableMapIdList = new short[]{
                78, 79, 80, 480
        };

        for (short mapId : enableMapIdList) {
            worldMap.getMap(mapId).setTeleportAble(true);
        }
    }

    public void load() {
        L1WorldMap worldMap = L1WorldMap.getInstance();

        notPassKeys.clear();
        passKeys.clear();

        SqlUtils.query("SELECT * FROM map_fix_key", (rs, i) -> {
            int srcX = rs.getInt("locX");
            int srcY = rs.getInt("locY");
            int srcMapId = rs.getInt("mapId");
            int type = rs.getInt("type");

            String key = String.valueOf(srcMapId) + srcX + srcY;

            if (type == 0) {
                notPassKeys.add(key);
            } else if (type == 1) {
                passKeys.add(key);
            } else if (type == 2) {
                arrowNotPassKeys.add(key);
            }

            if (type == 0) {
                worldMap.getMap((short) srcMapId).setPassable(srcX, srcY, false);
            } else if (type == 1) {
                worldMap.getMap((short) srcMapId).setPassable(srcX, srcY, true);
            }

            return null;
        });

        initMaps();
    }

    public void storeLocFix(int locX, int locY, int mapId, int type) {
        SqlUtils.update("INSERT INTO map_fix_key SET locX=?, locY=?, mapId=?, type=? ON DUPLICATE KEY UPDATE type=?",
                locX,
                locY,
                mapId,
                type,
                type
        );

        String key = String.valueOf(mapId) + locX + locY;
        notPassKeys.add(key);
    }
}
