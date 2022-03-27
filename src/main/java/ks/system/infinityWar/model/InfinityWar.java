package ks.system.infinityWar.model;

import ks.app.LineageAppContext;
import ks.model.L1Location;
import ks.model.map.L1Map;
import ks.model.map.L1WorldMap;
import ks.system.infinityWar.system.InfinityWarSystem;

import java.text.SimpleDateFormat;
import java.util.*;

public class InfinityWar {
    private int id;
    private String name;
    private int mapId;
    private int x1;
    private int y1;
    private int x2;
    private int y2;
    private int maxPlayer;
    private int maxPattern;

    private final List<InfinityWarItem> items = new ArrayList<>();
    private List<InfinityWarSpawn> spawnList = new ArrayList<>();
    private List<Integer> npc = new ArrayList<>();

    private List<String> times = new ArrayList<>();

    private final InfinityWarSystem warSystem;

    public InfinityWar() {
        warSystem = LineageAppContext.getBean(InfinityWarSystem.class);
        warSystem.setWar(this);
    }

    public List<InfinityWarSpawn> getSpawnList() {
        return spawnList;
    }

    public List<InfinityWarSpawn> getSpawnListByRound(int pattern, int round) {
        List<InfinityWarSpawn> result = new ArrayList<>();

        for (InfinityWarSpawn spawn : spawnList) {
            if (spawn.getGroupId() == round
                    && spawn.getPattern() == pattern) {
                result.add(spawn);
            }
        }

        return result;
    }

    public List<InfinityWarItem> getItemsByRound(int round) {
        List<InfinityWarItem> result = new ArrayList<>();

        for (InfinityWarItem item : items) {
            if (item.getRound() == round) {
                result.add(item);
            }
        }

        return result;
    }

    public void setSpawnList(List<InfinityWarSpawn> spawnList) {
        this.spawnList = spawnList;
    }

    public InfinityWarSystem getWarSystem() {
        return warSystem;
    }

    public List<Integer> getNpc() {
        return npc;
    }

    public void setNpc(List<Integer> npc) {
        this.npc = npc;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMapId() {
        return mapId;
    }

    public void setMapId(int mapId) {
        this.mapId = mapId;
    }

    public int getX1() {
        return x1;
    }

    public void setX1(int x1) {
        this.x1 = x1;
    }

    public int getY1() {
        return y1;
    }

    public void setY1(int y1) {
        this.y1 = y1;
    }

    public int getX2() {
        return x2;
    }

    public void setX2(int x2) {
        this.x2 = x2;
    }

    public int getY2() {
        return y2;
    }

    public void setY2(int y2) {
        this.y2 = y2;
    }

    public int getMaxPlayer() {
        return maxPlayer;
    }

    public void setMaxPlayer(int maxPlayer) {
        this.maxPlayer = maxPlayer;
    }

    public List<String> getTimes() {
        return times;
    }

    public void setTimes(List<String> times) {
        this.times = times;
    }

    public String nextTime() {
        SortedSet<String> set = new TreeSet<>(times);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        SortedSet<String> sortedSet = set.tailSet(sdf.format(new Date()));

        if (sortedSet.isEmpty()) {
            sortedSet = set;
        }
        return sortedSet.first();
    }

    public L1Location getLocation() {
        int x = (x2 + x1) / 2;
        int y = (y2 + y1) / 2;

        return new L1Location(x, y, mapId);
    }

    public int getMaxPattern() {
        return maxPattern;
    }

    public void setMaxPattern(int maxPattern) {
        this.maxPattern = maxPattern;
    }

    public List<InfinityWarItem> getItems() {
        return items;
    }

    private int minLvl;
    private int maxLvl;
    private int enterRoyal;
    private int enterKnight;
    private int enterMage;
    private int enterElf;
    private int enterDarkelf;
    private int enterDragonknight;
    private int enterBlackwizard;
    private int enterMale;
    private int enterFemale;
    private int usePot;
    private int hprBonus;
    private int mprBonus;

    public int getMinLvl() {
        return minLvl;
    }

    public void setMinLvl(int minLvl) {
        this.minLvl = minLvl;
    }

    public int getMaxLvl() {
        return maxLvl;
    }

    public L1Map getMap() {
        return L1WorldMap.getInstance().getMap((short) mapId);
    }

    public void setMaxLvl(int maxLvl) {
        this.maxLvl = maxLvl;
    }

    public int getEnterRoyal() {
        return enterRoyal;
    }

    public void setEnterRoyal(int enterRoyal) {
        this.enterRoyal = enterRoyal;
    }

    public int getEnterKnight() {
        return enterKnight;
    }

    public void setEnterKnight(int enterKnight) {
        this.enterKnight = enterKnight;
    }

    public int getEnterMage() {
        return enterMage;
    }

    public void setEnterMage(int enterMage) {
        this.enterMage = enterMage;
    }

    public int getEnterElf() {
        return enterElf;
    }

    public void setEnterElf(int enterElf) {
        this.enterElf = enterElf;
    }

    public int getEnterDarkelf() {
        return enterDarkelf;
    }

    public void setEnterDarkelf(int enterDarkelf) {
        this.enterDarkelf = enterDarkelf;
    }

    public int getEnterDragonknight() {
        return enterDragonknight;
    }

    public void setEnterDragonknight(int enterDragonknight) {
        this.enterDragonknight = enterDragonknight;
    }

    public int getEnterBlackwizard() {
        return enterBlackwizard;
    }

    public void setEnterBlackwizard(int enterBlackwizard) {
        this.enterBlackwizard = enterBlackwizard;
    }

    public int getEnterMale() {
        return enterMale;
    }

    public void setEnterMale(int enterMale) {
        this.enterMale = enterMale;
    }

    public int getEnterFemale() {
        return enterFemale;
    }

    public void setEnterFemale(int enterFemale) {
        this.enterFemale = enterFemale;
    }

    public int getUsePot() {
        return usePot;
    }

    public void setUsePot(int usePot) {
        this.usePot = usePot;
    }

    public int getHprBonus() {
        return hprBonus;
    }

    public void setHprBonus(int hprBonus) {
        this.hprBonus = hprBonus;
    }

    public int getMprBonus() {
        return mprBonus;
    }

    public void setMprBonus(int mprBonus) {
        this.mprBonus = mprBonus;
    }
}
