package ks.system.lastabard;

public class LastabardTime {
    private final int mapId;

    private final int relatedDoor;

    private final int deadline;

    public LastabardTime(int mapId, int startTime, int delayTime, int relatedDoor) {
        this.mapId = mapId;
        this.deadline = startTime + delayTime;
        this.relatedDoor = relatedDoor;
    }

    public boolean isTimeOver(int currentTime) {
        return deadline < currentTime;
    }

    public int getRemainingTime(int currentTime) {
        return deadline - currentTime;
    }

    public int getMapId() {
        return mapId;
    }

    public int getRelatedDoor() {
        return relatedDoor;
    }
}
