package ks.system.timeDungeon;

import java.util.ArrayList;
import java.util.List;

public class L1TimeDungeon {
    private int mapId;
    private String mapName;
    private int maxMinute;
    private String mapGroup;

    public String getMapGroup() {
        return mapGroup;
    }

    public void setMapGroup(String mapGroup) {
        this.mapGroup = mapGroup;
    }

    public int getMapId() {
        return mapId;
    }

    public void setMapId(int mapId) {
        this.mapId = mapId;
    }

    public String getMapName() {
        return mapName;
    }

    public void setMapName(String mapName) {
        this.mapName = mapName;
    }

    public int getMaxMinute() {
        return maxMinute;
    }

    public void setMaxMinute(int maxMinute) {
        this.maxMinute = maxMinute;
    }

    public List<Integer> getMaps() {
        List<Integer> result = new ArrayList<>();
        result.add(mapId);

        String[] strs = mapGroup.split(",");

        for (String s : strs) {
            result.add(Integer.valueOf(s));
        }

        return result;
    }
}
