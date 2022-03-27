package ks.system.portalsystem.model;

public class L1PortalLocation {
    private int x;
    private int y;
    private int map;
    private String name;

    public L1PortalLocation(int x, int y, int map, String name) {
        this.x = x;
        this.y = y;
        this.map = map;
        this.name = name;
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

    public int getMap() {
        return map;
    }

    public void setMap(int map) {
        this.map = map;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
