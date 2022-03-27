package ks.model;

import ks.util.common.random.RandomUtils;

public class L1WeaponAddDamage {
    private int weaponId;

    private int grade;
    private String type;
    private int safeEnchant;
    private int enchant;
    private int min;
    private int max;
    private int weaponType;

    private String note;

    public int getWeaponId() {
        return weaponId;
    }

    public void setWeaponId(int weaponId) {
        this.weaponId = weaponId;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public int getEnchant() {
        return enchant;
    }

    public void setEnchant(int enchant) {
        this.enchant = enchant;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    public int getValue() {
        return (int) RandomUtils.nextDouble(min, max);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getSafeEnchant() {
        return safeEnchant;
    }

    public void setSafeEnchant(int safeEnchant) {
        this.safeEnchant = safeEnchant;
    }

    public int getWeaponType() {
        return weaponType;
    }

    public void setWeaponType(int weaponType) {
        this.weaponType = weaponType;
    }
}
