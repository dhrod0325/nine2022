package ks.system.robot.model;

import java.util.ArrayList;
import java.util.List;

public class L1RobotHuntLocation {
    private int id;
    private int locX;
    private int locY;
    private int locMap;
    private String note;

    private List<L1RobotHuntLocationWay> wayList = new ArrayList<>();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public List<L1RobotHuntLocationWay> getWayList() {
        return wayList;
    }

    public void setWayList(List<L1RobotHuntLocationWay> wayList) {
        this.wayList = wayList;
    }
}
