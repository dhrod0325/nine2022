package ks.model.bookMark;

import org.apache.commons.lang3.StringUtils;

public class L1BookMark {
    private int id;
    private int ord;
    private int charId;
    private String name;
    private int locX;
    private int locY;
    private short mapId;
    private int speedId;
    private int numId;
    private int tempId;

    private int randomX;
    private int randomY;
    private String color;

    public int getSpeedId() {
        return speedId;
    }

    public void setSpeedId(int i) {
        speedId = i;
    }

    public int getId() {
        return id;
    }

    public void setId(int i) {
        id = i;
    }

    public int getCharId() {
        return charId;
    }

    public void setCharId(int i) {
        charId = i;
    }

    public int getNumId() {
        return numId;
    }

    public void setNumId(int i) {
        numId = i;
    }

    public int getTempId() {
        return tempId;
    }

    public void setTempId(int i) {
        tempId = i;
    }

    public String getName() {
        return name;
    }

    public String getBuildName(int num) {
        StringBuilder sb = new StringBuilder();

        if (!StringUtils.isEmpty(color)) {
            sb.append(color);
        }

        if (num != 0) {
            sb.append(String.format("[%s]", StringUtils.leftPad(num + "", 3, "0")));
        }

        sb.append(name);

        return sb.toString();
    }

    public void setName(String s) {
        name = s;
    }

    public int getLocX() {
        return locX;
    }

    public void setLocX(int i) {
        locX = i;
    }

    public int getLocY() {
        return locY;
    }

    public void setLocY(int i) {
        locY = i;
    }

    public short getMapId() {
        return mapId;
    }

    public void setMapId(short i) {
        mapId = i;
    }

    public int getRandomX() {
        return randomX;
    }

    public void setRandomX(int randomX) {
        this.randomX = randomX;
    }

    public int getRandomY() {
        return randomY;
    }

    public void setRandomY(int randomY) {
        this.randomY = randomY;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getOrd() {
        return ord;
    }

    public void setOrd(int ord) {
        this.ord = ord;
    }
}
