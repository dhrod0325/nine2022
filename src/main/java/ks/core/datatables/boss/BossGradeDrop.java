package ks.core.datatables.boss;

import ks.util.common.random.RandomUtils;

public class BossGradeDrop {
    private int bossGrade;
    private int dropItemId;
    private String dropItemName;
    private int dropMin;
    private int dropMax;
    private int dropChance;

    public int getBossGrade() {
        return bossGrade;
    }

    public void setBossGrade(int bossGrade) {
        this.bossGrade = bossGrade;
    }

    public int getDropItemId() {
        return dropItemId;
    }

    public void setDropItemId(int dropItemId) {
        this.dropItemId = dropItemId;
    }

    public String getDropItemName() {
        return dropItemName;
    }

    public void setDropItemName(String dropItemName) {
        this.dropItemName = dropItemName;
    }

    public int getDropMin() {
        return dropMin;
    }

    public void setDropMin(int dropMin) {
        this.dropMin = dropMin;
    }

    public int getDropMax() {
        return dropMax;
    }

    public void setDropMax(int dropMax) {
        this.dropMax = dropMax;
    }

    public int getDropChance() {
        return dropChance;
    }

    public void setDropChance(int dropChance) {
        this.dropChance = dropChance;
    }

    public int getDropCount() {
        return RandomUtils.nextInt(getDropMin(), getDropMax());
    }
}
