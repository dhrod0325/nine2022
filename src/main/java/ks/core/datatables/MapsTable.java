package ks.core.datatables;

import ks.app.LineageAppContext;
import ks.app.aop.LogTime;
import ks.util.common.SqlUtils;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class MapsTable {
    private final Map<Integer, MapData> maps = new HashMap<>();

    public static MapsTable getInstance() {
        return LineageAppContext.getBean(MapsTable.class);
    }

    @LogTime
    public void load() {
        maps.clear();

        List<MapData> list = selectList();

        for (MapData data : list) {
            maps.put(data.mapId, data);
        }
    }

    public List<MapData> selectList() {
        return SqlUtils.query("SELECT * FROM mapids", (rs, i) -> {
            MapData data = new MapData();

            data.mapName = rs.getString("locationname");
            data.mapId = rs.getInt("mapid");
            data.startX = rs.getInt("startX");
            data.endX = rs.getInt("endX");
            data.startY = rs.getInt("startY");
            data.endY = rs.getInt("endY");
            data.monster_amount = rs.getDouble("monster_amount");

            data.isUnderwater = rs.getBoolean("underwater");
            data.markable = rs.getBoolean("markable");
            data.teleportable = rs.getBoolean("teleportable");
            data.escapable = rs.getBoolean("escapable");
            data.isUseResurrection = rs.getBoolean("resurrection");
            data.isUsePainwand = rs.getBoolean("painwand");
            data.isEnabledDeathPenalty = rs.getBoolean("penalty");
            data.isTakePets = rs.getBoolean("take_pets");
            data.isRecallPets = rs.getBoolean("recall_pets");
            data.isUsableItem = rs.getBoolean("usable_item");
            data.isUsableSkill = rs.getBoolean("usable_skill");

            data.minLev = rs.getInt("min_lev");
            data.maxLev = rs.getInt("max_lev");
            data.isHunt = rs.getBoolean("hunt");

            data.adenaDropRate = rs.getDouble("adena_drop_rate");
            data.dropRate = rs.getDouble("drop_rate");
            data.expRate = rs.getDouble("exp_rate");

            return data;
        });
    }

    public MapData getMapData(int mapId) {
        return maps.get(mapId);
    }

    public String getMapName(int mapId) {
        MapData map = maps.get(mapId);
        if (map == null) {
            return null;
        }
        return maps.get(mapId).mapName;
    }

    public Map<Integer, MapData> getMaps() {
        return maps;
    }

    public double getMonsterAmount(int mapId) {
        MapData map = maps.get(mapId);
        if (map == null) {
            return 0;
        }
        return map.monster_amount;
    }

    public double getDropRate(int mapId) {
        MapData map = maps.get(mapId);

        if (map == null) {
            return 0;
        }
        return map.dropRate;
    }

    public double getExpRate(int mapId) {
        MapData map = maps.get(mapId);

        if (map == null) {
            return 0;
        }

        return map.expRate;
    }

    public double getAdenaDropRate(int mapId) {
        MapData map = maps.get(mapId);

        if (map == null) {
            return 0;
        }

        return map.adenaDropRate;
    }

    public boolean isUnderwater(int mapId) {
        MapData map = maps.get(mapId);
        if (map == null) {
            return false;
        }

        return maps.get(mapId).isUnderwater;
    }

    public boolean isMarkAble(int mapId) {
        MapData map = maps.get(mapId);

        if (map == null) {
            return false;
        }
        return maps.get(mapId).markable;
    }

    public boolean isTeleportAble(int mapId) {
        MapData map = maps.get(mapId);
        if (map == null) {
            return false;
        }
        return maps.get(mapId).teleportable;
    }

    public boolean isEscapeAble(int mapId) {
        MapData map = maps.get(mapId);
        if (map == null) {
            return false;
        }
        return maps.get(mapId).escapable;
    }

    public boolean isUseResurrection(int mapId) {
        MapData map = maps.get(mapId);
        if (map == null) {
            return false;
        }
        return maps.get(mapId).isUseResurrection;
    }

    public boolean isUsePainWand(int mapId) {
        MapData map = maps.get(mapId);
        if (map == null) {
            return false;
        }
        return maps.get(mapId).isUsePainwand;
    }

    public boolean isEnabledDeathPenalty(int mapId) {
        MapData map = maps.get(mapId);
        if (map == null) {
            return false;
        }
        return maps.get(mapId).isEnabledDeathPenalty;
    }

    public boolean isTakePets(int mapId) {
        MapData map = maps.get(mapId);
        if (map == null) {
            return false;
        }
        return maps.get(mapId).isTakePets;
    }

    public boolean isRecallPets(int mapId) {
        MapData map = maps.get(mapId);
        if (map == null) {
            return false;
        }
        return maps.get(mapId).isRecallPets;
    }

    public boolean isUseAbleItem(int mapId) {
        MapData map = maps.get(mapId);
        if (map == null) {
            return false;
        }
        return maps.get(mapId).isUsableItem;
    }

    public boolean isUseAbleSkill(int mapId) {
        MapData map = maps.get(mapId);
        if (map == null) {
            return false;
        }
        return maps.get(mapId).isUsableSkill;
    }

    public List<Integer> huntMapList() {
        List<Integer> result = new ArrayList<>();

        Collection<MapData> v = maps.values();

        for (MapData d : v) {
            if (d.isHunt) {
                result.add(d.mapId);
            }
        }

        return result;
    }

    public static class MapData {
        public int mapId;
        public String mapName = null;
        public int startX = 0;
        public int endX = 0;
        public int startY = 0;
        public int endY = 0;
        public double monster_amount = 1;
        public boolean isUnderwater = false;
        public boolean markable = false;
        public boolean teleportable = false;
        public boolean escapable = false;
        public boolean isUseResurrection = false;
        public boolean isUsePainwand = false;
        public boolean isEnabledDeathPenalty = false;
        public boolean isTakePets = false;
        public boolean isRecallPets = false;
        public boolean isUsableItem = false;
        public boolean isUsableSkill = false;
        public int minLev = 0;
        public int maxLev = 0;
        public boolean isHunt = false;
        public double dropRate = 1;
        public double adenaDropRate = 1;
        public double expRate = 1;
    }
}
