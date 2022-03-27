package ks.system.robot.model;

public class L1RobotHuntLocationWay {
    private int huntId;
    private int locX;
    private int locY;
    private int locMap;
    private int ord;

    public int getHuntId() {
        return huntId;
    }

    public void setHuntId(int huntId) {
        this.huntId = huntId;
    }

    public int getLocX() {
        return locX;
    }

    public void setLocX(int locX) {
        this.locX = locX;
    }

    public int getLocY() {
        return locY;
    }

    public void setLocY(int locY) {
        this.locY = locY;
    }

    public int getLocMap() {
        return locMap;
    }

    public void setLocMap(int locMap) {
        this.locMap = locMap;
    }

    public int getOrd() {
        return ord;
    }

    public void setOrd(int ord) {
        this.ord = ord;
    }

    public boolean isEqualsLocation(int x, int y, int map) {
        return locX == x && locY == y && locMap == map;
    }
}
