package ks.model;

import java.util.Calendar;

public class L1Castle {
    private final int id;
    private final String name;
    private Calendar warTime;
    private int publicMoney;
    private int warBase;
    private int security;

    public L1Castle(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Calendar getWarTime() {
        return warTime;
    }

    public void setWarTime(Calendar i) {
        warTime = i;
    }

    public int getTaxRate() {
        return 0;
    }

    public void setTaxRate(int i) {
    }

    public int getPublicMoney() {
        return publicMoney;
    }

    public void setPublicMoney(int i) {
        publicMoney = i;
    }

    public int getPublicReadyMoney() {
        return 0;
    }

    public void setPublicReadyMoney(int i) {
    }

    public int getShowMoney() {
        return 0;
    }

    public void setShowMoney(int i) {
    }

    public int getWarBaseTime() {
        return warBase;
    }

    public void setWarBaseTime(int i) {
        warBase = i;
    }

    public int getCastleSecurity() {
        return security;
    }

    public void setCastleSecurity(int i) {
        security = i;
    }
}
