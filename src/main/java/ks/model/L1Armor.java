package ks.model;

public class L1Armor extends L1Item {

    private int ac = 0;
    private int damageReduction = 0;
    private int weightReduction = 0;
    private int hitup = 0; // ● 근접무기 명중률
    private int dmgup = 0; // ● 근접무기 추타율
    private int bowHitup = 0; // ● 활의 명중율
    private int bowDmgup = 0; // ● 활의 추타율
    private int defenseWater = 0;
    private int defenseWind = 0;
    private int defenseFire = 0;
    private int defenseEarth = 0;
    private int registStun = 0;
    private int registStone = 0;
    private int registSleep = 0;
    private int registFreeze = 0;
    private int registSustain = 0;
    private int registBlind = 0;
    private int registElf;

    public L1Armor() {
    }

    @Override
    public int getAc() {
        return ac;
    }

    public void setAc(int i) {
        this.ac = i;
    }

    @Override
    public int getDamageReduction() {
        return damageReduction;
    }

    public void setDamageReduction(int i) {
        damageReduction = i;
    }

    @Override
    public int getWeightReduction() {
        return weightReduction;
    }

    public void setWeightReduction(int i) {
        weightReduction = i;
    }

    @Override
    public int getHitUp() {
        return hitup;
    }

    public void setHitup(int i) {
        hitup = i;
    }

    @Override
    public int getDmgUp() {
        return dmgup;
    }

    public void setDmgup(int i) {
        dmgup = i;
    }

    @Override
    public int getBowHitUp() {
        return bowHitup;
    }

    public void setBowHitup(int i) {
        bowHitup = i;
    }

    @Override
    public int getBowDmgUp() {
        return bowDmgup;
    }

    public void setBowDmgup(int i) {
        bowDmgup = i;
    }

    @Override
    public int getDefenseWater() {
        return this.defenseWater;
    }

    public void setDefenseWater(int i) {
        defenseWater = i;
    }

    @Override
    public int getDefenseWind() {
        return this.defenseWind;
    }

    public void setDefenseWind(int i) {
        defenseWind = i;
    }

    @Override
    public int getDefenseFire() {
        return this.defenseFire;
    }

    public void setDefenseFire(int i) {
        defenseFire = i;
    }

    @Override
    public int getDefenseEarth() {
        return this.defenseEarth;
    }

    public void setDefenseEarth(int i) {
        defenseEarth = i;
    }

    @Override
    public int getRegistStun() {
        return this.registStun;
    }

    public void setRegistStun(int i) {
        registStun = i;
    }

    @Override
    public int getRegistStone() {
        return this.registStone;
    }

    public void setRegistStone(int i) {
        registStone = i;
    }

    @Override
    public int getRegistSleep() {
        return this.registSleep;
    }

    public void setRegistSleep(int i) {
        registSleep = i;
    }

    @Override
    public int getRegistFreeze() {
        return this.registFreeze;
    }

    public void setRegistFreeze(int i) {
        registFreeze = i;
    }

    @Override
    public int getRegistSustAin() {
        return this.registSustain;
    }

    public void setRegistSustain(int i) {
        registSustain = i;
    }

    @Override
    public int getRegistBlind() {
        return this.registBlind;
    }

    public void setRegistBlind(int i) {
        registBlind = i;
    }

    @Override
    public int getRegistElf() {
        return registElf;
    }

    public void setRegistElf(int registElf) {
        this.registElf = registElf;
    }
}