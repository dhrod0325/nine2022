package ks.model;

import ks.model.map.L1Map;
import ks.model.map.L1WorldMap;
import ks.model.types.Rectangle;

public class L1MapArea extends Rectangle {
    private L1Map map;

    public L1MapArea(int left, int top, int right, int bottom, int mapId) {
        super(left, top, right, bottom);

        map = L1WorldMap.getInstance().getMap((short) mapId);
    }

    public L1Map getMap() {
        return map;
    }

    public void setMap(L1Map map) {
        this.map = map;
    }

    public int getMapId() {
        return map.getId();
    }

    public boolean contains(L1Location loc) {
        return (map.getId() == loc.getMap().getId()) && super.contains(loc);
    }
}
