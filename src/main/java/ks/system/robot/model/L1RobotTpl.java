package ks.system.robot.model;

public class L1RobotTpl {
    private int id;
    private String name;
    private String title;

    private int lvl;
    private int clan;

    private int weaponId;
    private int enchant;
    private int bless;

    private int con;
    private int str;
    private int dex;
    private int wis;
    private int cha;
    private int int1;

    private int baseCon;
    private int baseStr;
    private int baseDex;
    private int baseWis;
    private int baseInt;
    private int baseCha;

    private int hp;
    private int mp;
    private int mr;
    private int ac;
    private int er;
    private int sp;

    private int classType;
    private int locX;
    private int locY;
    private int locMap;
    private int lawful;
    private int dollItemId;

    private int addDmg;
    private int addHitUp;

    private int robotType;

    private int huntId;

    private L1RobotHuntLocation huntLocation = new L1RobotHuntLocation();

    public L1RobotHuntLocation getHuntLocation() {
        return huntLocation;
    }

    public void setHuntLocation(L1RobotHuntLocation huntLocation) {
        this.huntLocation = huntLocation;
    }

    public int getRobotType() {
        return robotType;
    }

    public void setRobotType(int robotType) {
        this.robotType = robotType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getLvl() {
        return lvl;
    }

    public void setLvl(int lvl) {
        this.lvl = lvl;
    }

    public int getClan() {
        return clan;
    }

    public void setClan(int clan) {
        this.clan = clan;
    }

    public int getWeaponId() {
        return weaponId;
    }

    public void setWeaponId(int weaponId) {
        this.weaponId = weaponId;
    }

    public int getEnchant() {
        return enchant;
    }

    public void setEnchant(int enchant) {
        this.enchant = enchant;
    }

    public int getBless() {
        return bless;
    }

    public void setBless(int bless) {
        this.bless = bless;
    }

    public int getCon() {
        return con;
    }

    public void setCon(int con) {
        this.con = con;
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

    public int getWis() {
        return wis;
    }

    public void setWis(int wis) {
        this.wis = wis;
    }

    public int getCha() {
        return cha;
    }

    public void setCha(int cha) {
        this.cha = cha;
    }

    public int getInt1() {
        return int1;
    }

    public void setInt1(int int1) {
        this.int1 = int1;
    }

    public int getBaseCon() {
        return baseCon;
    }

    public void setBaseCon(int baseCon) {
        this.baseCon = baseCon;
    }

    public int getBaseStr() {
        return baseStr;
    }

    public void setBaseStr(int baseStr) {
        this.baseStr = baseStr;
    }

    public int getBaseDex() {
        return baseDex;
    }

    public void setBaseDex(int baseDex) {
        this.baseDex = baseDex;
    }

    public int getBaseWis() {
        return baseWis;
    }

    public void setBaseWis(int baseWis) {
        this.baseWis = baseWis;
    }

    public int getBaseInt() {
        return baseInt;
    }

    public void setBaseInt(int baseInt) {
        this.baseInt = baseInt;
    }

    public int getBaseCha() {
        return baseCha;
    }

    public void setBaseCha(int baseCha) {
        this.baseCha = baseCha;
    }

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public int getMp() {
        return mp;
    }

    public void setMp(int mp) {
        this.mp = mp;
    }

    public int getMr() {
        return mr;
    }

    public void setMr(int mr) {
        this.mr = mr;
    }

    public int getAc() {
        return ac;
    }

    public void setAc(int ac) {
        this.ac = ac;
    }

    public int getEr() {
        return er;
    }

    public void setEr(int er) {
        this.er = er;
    }

    public int getSp() {
        return sp;
    }

    public void setSp(int sp) {
        this.sp = sp;
    }

    public int getClassType() {
        return classType;
    }

    public void setClassType(int classType) {
        this.classType = classType;
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

    public int getLocMap() {
        return locMap;
    }

    public void setLocMap(int locMap) {
        this.locMap = locMap;
    }

    public int getLawful() {
        return lawful;
    }

    public void setLawful(int lawful) {
        this.lawful = lawful;
    }

    public int getDollItemId() {
        return dollItemId;
    }

    public void setDollItemId(int dollItemId) {
        this.dollItemId = dollItemId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getHuntId() {
        return huntId;
    }

    public void setHuntId(int huntId) {
        this.huntId = huntId;
    }

    public int getAddDmg() {
        return addDmg;
    }

    public void setAddDmg(int addDmg) {
        this.addDmg = addDmg;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAddHitUp() {
        return addHitUp;
    }

    public void setAddHitUp(int addHitUp) {
        this.addHitUp = addHitUp;
    }
}
