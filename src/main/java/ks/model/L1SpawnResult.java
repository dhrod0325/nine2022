package ks.model;

public class L1SpawnResult {
    private int mapId;
    private int totalCount;
    private int totalAdenaCount;


    public int getTotalSpawnCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getTotalAdenaCount() {
        return totalAdenaCount;
    }

    public void setTotalAdenaCount(int totalAdenaCount) {
        this.totalAdenaCount = totalAdenaCount;
    }

    public int getMapId() {
        return mapId;
    }

    public void setMapId(int mapId) {
        this.mapId = mapId;
    }
}
