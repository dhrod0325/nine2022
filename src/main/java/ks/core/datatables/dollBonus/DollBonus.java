package ks.core.datatables.dollBonus;

import ks.util.common.random.RandomUtils;

public class DollBonus {
    private int itemId;
    private String itemName;
    private int enchantLevel;

    private int addHp;
    private int addMp;
    private int addExp;
    private int addDmg;
    private int addBowDmg;
    private int addHitUp;
    private int addBowHitUp;
    private int addSp;
    private int addMr;

    private double addPotionPer;

    private int dmg;
    private int reduction;
    private int hitUp;

    private int registStun;

    private int ac;

    private int addHpr;
    private int addMpr;

    private int pvpDmg;
    private int pvpReduction;

    private int str;
    private int dex;
    private int intel;
    private int cha;
    private int con;

    private int perDmgMin;
    private int perDmgMax;
    private int perDmgPer;
    private int perDmgEffect;
    private int perDmgTarget;
    private String perDmgNote;

    private int weight;

    private int abMpr;
    private int abMprTime;

    private int abHpr;
    private int abHprTime;

    private int stunHitUp;
    private int magicHit;

    public int getAbMpr() {
        return abMpr;
    }

    public void setAbMpr(int abMpr) {
        this.abMpr = abMpr;
    }

    public int getAbMprTime() {
        return abMprTime;
    }

    public void setAbMprTime(int abMprTime) {
        this.abMprTime = abMprTime;
    }

    public int getStr() {
        return str;
    }

    public void setStr(int str) {
        this.str = str;
    }

    public int getDex() {
        return dex;
    }

    public void setDex(int dex) {
        this.dex = dex;
    }

    public int getIntel() {
        return intel;
    }

    public void setIntel(int intel) {
        this.intel = intel;
    }

    public int getCha() {
        return cha;
    }

    public void setCha(int cha) {
        this.cha = cha;
    }

    public int getCon() {
        return con;
    }

    public void setCon(int con) {
        this.con = con;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public int getEnchantLevel() {
        return enchantLevel;
    }

    public void setEnchantLevel(int enchantLevel) {
        this.enchantLevel = enchantLevel;
    }

    public int getAddDmg() {
        return addDmg;
    }

    public void setAddDmg(int addDmg) {
        this.addDmg = addDmg;
    }

    public int getAddBowDmg() {
        return addBowDmg;
    }

    public void setAddBowDmg(int addBowDmg) {
        this.addBowDmg = addBowDmg;
    }

    public int getAddHitUp() {
        return addHitUp;
    }

    public void setAddHitUp(int addHitUp) {
        this.addHitUp = addHitUp;
    }

    public int getAddBowHitUp() {
        return addBowHitUp;
    }

    public void setAddBowHitUp(int addBowHitUp) {
        this.addBowHitUp = addBowHitUp;
    }

    public int getDmg() {
        return dmg;
    }

    public void setDmg(int dmg) {
        this.dmg = dmg;
    }

    public int getReduction() {
        return reduction;
    }

    public void setReduction(int reduction) {
        this.reduction = reduction;
    }

    public int getHitUp() {
        return hitUp;
    }

    public void setHitUp(int hitUp) {
        this.hitUp = hitUp;
    }

    public int getAddSp() {
        return addSp;
    }

    public void setAddSp(int addSp) {
        this.addSp = addSp;
    }

    public int getAddMr() {
        return addMr;
    }

    public void setAddMr(int addMr) {
        this.addMr = addMr;
    }

    public int getAddHp() {
        return addHp;
    }

    public void setAddHp(int addHp) {
        this.addHp = addHp;
    }

    public int getAddMp() {
        return addMp;
    }

    public void setAddMp(int addMp) {
        this.addMp = addMp;
    }

    public int getRegistStun() {
        return registStun;
    }

    public void setRegistStun(int registStun) {
        this.registStun = registStun;
    }

    public double getAddPotionPer() {
        return addPotionPer;
    }

    public void setAddPotionPer(double addPotionPer) {
        this.addPotionPer = addPotionPer;
    }

    public int getAc() {
        return ac;
    }

    public void setAc(int ac) {
        this.ac = ac;
    }

    public int getAddHpr() {
        return addHpr;
    }

    public void setAddHpr(int addHpr) {
        this.addHpr = addHpr;
    }

    public int getAddMpr() {
        return addMpr;
    }

    public void setAddMpr(int addMpr) {
        this.addMpr = addMpr;
    }

    public int getMagicHit() {
        return magicHit;
    }

    public void setMagicHit(int magicHit) {
        this.magicHit = magicHit;
    }

    public int getAddExp() {
        return addExp;
    }

    public void setAddExp(int addExp) {
        this.addExp = addExp;
    }

    public int getPvpDmg() {
        return pvpDmg;
    }

    public void setPvpDmg(int pvpDmg) {
        this.pvpDmg = pvpDmg;
    }

    public int getPvpReduction() {
        return pvpReduction;
    }

    public void setPvpReduction(int pvpReduction) {
        this.pvpReduction = pvpReduction;
    }

    public int getPerDmg() {
        return RandomUtils.nextInt(getPerDmgMin(), getPerDmgMax());
    }

    public int getPerDmgPer() {
        return perDmgPer;
    }

    public void setPerDmgPer(int perDmgPer) {
        this.perDmgPer = perDmgPer;
    }

    public int getPerDmgEffect() {
        return perDmgEffect;
    }

    public void setPerDmgEffect(int perDmgEffect) {
        this.perDmgEffect = perDmgEffect;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getAbHpr() {
        return abHpr;
    }

    public void setAbHpr(int abHpr) {
        this.abHpr = abHpr;
    }

    public int getAbHprTime() {
        return abHprTime;
    }

    public void setAbHprTime(int abHprTime) {
        this.abHprTime = abHprTime;
    }

    public int getPerDmgTarget() {
        return perDmgTarget;
    }

    public void setPerDmgTarget(int perDmgTarget) {
        this.perDmgTarget = perDmgTarget;
    }

    public int getStunHitUp() {
        return stunHitUp;
    }

    public void setStunHitUp(int stunHitUp) {
        this.stunHitUp = stunHitUp;
    }

    public int getPerDmgMin() {
        return perDmgMin;
    }

    public void setPerDmgMin(int perDmgMin) {
        this.perDmgMin = perDmgMin;
    }

    public int getPerDmgMax() {
        return perDmgMax;
    }

    public void setPerDmgMax(int perDmgMax) {
        this.perDmgMax = perDmgMax;
    }

    public String getPerDmgNote() {
        return perDmgNote;
    }

    public void setPerDmgNote(String perDmgNote) {
        this.perDmgNote = perDmgNote;
    }
}
