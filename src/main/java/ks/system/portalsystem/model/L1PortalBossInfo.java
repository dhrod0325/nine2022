package ks.system.portalsystem.model;

public class L1PortalBossInfo {
    private int x;
    private int y;
    private short mapId;
    private int npcId;
    private int randomRange;

    public L1PortalBossInfo(int x, int y, short mapId, int npcId, int randomRange) {
        this.x = x;
        this.y = y;
        this.mapId = mapId;
        this.npcId = npcId;
        this.randomRange = randomRange;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public short getMapId() {
        return mapId;
    }

    public void setMapId(short mapId) {
        this.mapId = mapId;
    }

    public int getNpcId() {
        return npcId;
    }

    public void setNpcId(int npcId) {
        this.npcId = npcId;
    }

    public int getRandomRange() {
        return randomRange;
    }

    public void setRandomRange(int randomRange) {
        this.randomRange = randomRange;
    }
}
