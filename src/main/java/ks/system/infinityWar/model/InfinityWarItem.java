package ks.system.infinityWar.model;

public class InfinityWarItem {
    private int infinityId;
    private int round;
    private int itemId;
    private String itemName;
    private int count;
    private int spawnCount;

    public int getInfinityId() {
        return infinityId;
    }

    public void setInfinityId(int infinityId) {
        this.infinityId = infinityId;
    }

    public int getRound() {
        return round;
    }

    public void setRound(int round) {
        this.round = round;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getSpawnCount() {
        return spawnCount;
    }

    public void setSpawnCount(int spawnCount) {
        this.spawnCount = spawnCount;
    }
}
