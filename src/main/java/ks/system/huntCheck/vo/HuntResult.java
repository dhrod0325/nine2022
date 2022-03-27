package ks.system.huntCheck.vo;

import java.util.Date;

public class HuntResult {
    private int id;
    private String charName;
    private Date regDate;
    private String mapName;
    private int mapId;
    private int itemId;
    private int resolvePriceSum;
    private int count;
    private int exp;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCharName() {
        return charName;
    }

    public void setCharName(String charName) {
        this.charName = charName;
    }

    public Date getRegDate() {
        return regDate;
    }

    public void setRegDate(Date regDate) {
        this.regDate = regDate;
    }

    public String getMapName() {
        return mapName;
    }

    public void setMapName(String mapName) {
        this.mapName = mapName;
    }

    public int getMapId() {
        return mapId;
    }

    public void setMapId(int mapId) {
        this.mapId = mapId;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public int getResolvePriceSum() {
        return resolvePriceSum;
    }

    public void setResolvePriceSum(int resolvePriceSum) {
        this.resolvePriceSum = resolvePriceSum;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getExp() {
        return exp;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }

    @Override
    public String toString() {
        return "HuntResult{" +
                "id=" + id +
                ", charName='" + charName + '\'' +
                ", regDate=" + regDate +
                ", mapName='" + mapName + '\'' +
                ", mapId=" + mapId +
                ", itemId=" + itemId +
                ", resolvePriceSum=" + resolvePriceSum +
                ", count=" + count +
                ", exp=" + exp +
                '}';
    }
}
