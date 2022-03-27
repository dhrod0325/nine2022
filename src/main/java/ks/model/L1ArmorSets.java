package ks.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class L1ArmorSets {
    private int id;
    private String sets;
    private int polyId;
    private int ac;
    private int hp;
    private int mp;
    private int hpr;
    private int mpr;
    private int mr;
    private int str;
    private int dex;
    private int con;
    private int wis;
    private int cha;
    private int intl;
    private int sp;
    private int shortHitup;
    private int shortDmgup;
    private int longHitup;
    private int longDmgup;

    public int getId() {
        return id;
    }

    public void setId(int i) {
        id = i;
    }

    public String getSets() {
        return sets;
    }

    public void setSets(String s) {
        sets = s;
    }

    public List<Integer> getSetList() {
        List<Integer> result = new ArrayList<>();

        Arrays.stream(sets.split(",")).forEach(s -> result.add(Integer.parseInt(s)));

        return result;
    }

    public int getPolyId() {
        return polyId;
    }

    public void setPolyId(int i) {
        polyId = i;
    }

    public int getAc() {
        return ac;
    }

    public void setAc(int i) {
        ac = i;
    }

    public int getHp() {
        return hp;
    }

    public void setHp(int i) {
        hp = i;
    }

    public int getMp() {
        return mp;
    }

    public void setMp(int i) {
        mp = i;
    }

    public int getHpr() {
        return hpr;
    }

    public void setHpr(int i) {
        hpr = i;
    }

    public int getMpr() {
        return mpr;
    }

    public void setMpr(int i) {
        mpr = i;
    }

    public int getMr() {
        return mr;
    }

    public void setMr(int i) {
        mr = i;
    }

    public int getStr() {
        return str;
    }

    public void setStr(int i) {
        str = i;
    }

    public int getDex() {
        return dex;
    }

    public void setDex(int i) {
        dex = i;
    }

    public int getCon() {
        return con;
    }

    public void setCon(int i) {
        con = i;
    }

    public int getWis() {
        return wis;
    }

    public void setWis(int i) {
        wis = i;
    }

    public int getCha() {
        return cha;
    }

    public void setCha(int i) {
        cha = i;
    }

    public int getIntl() {
        return intl;
    }

    public void setIntl(int i) {
        intl = i;
    }

    public int getSp() {
        return sp;
    }

    public void setSp(int i) {
        sp = i;
    }

    public int getShortHitup() {
        return shortHitup;
    }

    public void setShortHitup(int i) {
        shortHitup = i;
    }

    public int getShortDmgup() {
        return shortDmgup;
    }

    public void setShortDmgup(int i) {
        shortDmgup = i;
    }

    public int getLongHitup() {
        return longHitup;
    }

    public void setLongHitup(int i) {
        longHitup = i;
    }

    public int getLongDmgup() {
        return longDmgup;
    }

    public void setLongDmgup(int i) {
        longDmgup = i;
    }
}
