package ks.model;

import java.util.Objects;

public class L1Drop {
    private int mobId;
    private int itemId;
    private int min;
    private int max;
    private int chance;
    private int chanceRiper;
    private String itemName;
    private String mobName;

    public L1Drop(int mobId, int itemId, int min, int max, int chance) {
        this.mobId = mobId;
        this.itemId = itemId;
        this.min = min;
        this.max = max;
        this.chance = chance;
    }

    public L1Drop() {
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public int getChance() {
        return chance;
    }

    public void setChance(int chance) {
        this.chance = chance;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getMobId() {
        return mobId;
    }

    public void setMobId(int mobId) {
        this.mobId = mobId;
    }

    public String getMobName() {
        return mobName;
    }

    public void setMobName(String mobName) {
        this.mobName = mobName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        L1Drop l1Drop = (L1Drop) o;
        return mobId == l1Drop.mobId && itemId == l1Drop.itemId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(mobId, itemId);
    }
}
