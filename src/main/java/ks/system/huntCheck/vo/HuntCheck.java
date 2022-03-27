package ks.system.huntCheck.vo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HuntCheck {
    private int id;
    private int weaponId;
    private int weaponEnchant;
    private int ac;
    private int mr;
    private int mobId;
    private int mapId;
    private Integer charId;

    private int locX;
    private int locY;
    private String charName;
    private Date regDate;
    private int exp;

    private String searchStartDate;
    private List<HuntCheckItem> huntCheckItemList = new ArrayList<>();

    public String getSearchStartDate() {
        return searchStartDate;
    }

    public void setSearchStartDate(String searchStartDate) {
        this.searchStartDate = searchStartDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getWeaponId() {
        return weaponId;
    }

    public void setWeaponId(int weaponId) {
        this.weaponId = weaponId;
    }

    public int getWeaponEnchant() {
        return weaponEnchant;
    }

    public void setWeaponEnchant(int weaponEnchant) {
        this.weaponEnchant = weaponEnchant;
    }

    public int getAc() {
        return ac;
    }

    public void setAc(int ac) {
        this.ac = ac;
    }

    public int getMr() {
        return mr;
    }

    public void setMr(int mr) {
        this.mr = mr;
    }

    public int getMobId() {
        return mobId;
    }

    public void setMobId(int mobId) {
        this.mobId = mobId;
    }

    public int getMapId() {
        return mapId;
    }

    public void setMapId(int mapId) {
        this.mapId = mapId;
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

    public String getCharName() {
        return charName;
    }

    public void setCharName(String charName) {
        this.charName = charName;
    }

    public int getExp() {
        return exp;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }

    public Date getRegDate() {
        return regDate;
    }

    public void setRegDate(Date regDate) {
        this.regDate = regDate;
    }

    public List<HuntCheckItem> getHuntCheckItemList() {
        return huntCheckItemList;
    }

    public void setHuntCheckItemList(List<HuntCheckItem> huntCheckItemList) {
        this.huntCheckItemList = huntCheckItemList;
    }

    public Integer getCharId() {
        return charId;
    }

    public void setCharId(Integer charId) {
        this.charId = charId;
    }
}
