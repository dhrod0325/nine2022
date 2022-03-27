package ks.model;

public class L1Npc extends L1Object implements Cloneable {

    private int npcId;
    private String name;
    private int isDurability;
    private String title;
    private String impl;
    private int level;
    private int hp;
    private int mp;
    private int ac;
    private byte str;
    private byte con;
    private byte dex;
    private byte wis;
    private byte _int;
    private int mr;
    private int exp;
    private int lawful;
    private String size;
    /**
     * 1 바람
     * 2 불
     * 4 땅
     * 8 물
     */
    private int weakAttr;
    private int ranged;
    private boolean agrososc;
    private boolean agrocoi;
    private boolean tameable;
    private int passispeed;
    private int atkspeed;
    private boolean agro;
    private int gfxid;
    private String nameId;
    private int undead;
    private int poisonAtk;
    private int paralysisAtk;
    private int family;
    private int agroFamily;
    private int agroGfxId1;
    private int agroGfxId2;
    private boolean picupItem;
    private int digestItem;
    private boolean braveSpeed;
    private int hprInterval;
    private int hpr;
    private int mprInterval;
    private int mpr;
    private boolean teleport;
    private int randomLevel;
    private int randomHp;
    private int randomMp;
    private int randomAc;
    private int randomExp;
    private int randomLawful;
    private int damageReduction;
    private boolean hard;
    private boolean doppel;
    private boolean tu;
    private boolean erase;
    private int bowActId = 0;
    private int karma;
    private int transformId;
    private int transformGfxId;
    private int atkMagicSpeed;
    private int subMagicSpeed;
    private int lightSize;
    private boolean amountFixed;
    private boolean changeHead;
    private boolean isCantResurrect;
    private int damage;

    public L1Npc() {
    }

    @Override
    public L1Npc clone() {
        try {
            return (L1Npc) (super.clone());
        } catch (CloneNotSupportedException e) {
            throw (new InternalError(e.getMessage()));
        }
    }

    public int getNpcId() {
        return npcId;
    }

    public void setNpcId(int i) {
        npcId = i;
    }

    public String getName() {
        return name;
    }

    public void setName(String s) {
        name = s;
    }

    public int getIsDurability() {
        return isDurability;
    }

    public void setIsDurability(int isDurability) {
        this.isDurability = isDurability;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImpl() {
        return impl;
    }

    public void setImpl(String s) {
        impl = s;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int i) {
        level = i;
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

    public int getAc() {
        return ac;
    }

    public void setAc(int i) {
        ac = i;
    }

    public byte getStr() {
        return str;
    }

    public void setStr(byte i) {
        str = i;
    }

    public byte getCon() {
        return con;
    }

    public void setCon(byte i) {
        con = i;
    }

    public byte getDex() {
        return dex;
    }

    public void setDex(byte i) {
        dex = i;
    }

    public byte getWis() {
        return wis;
    }

    public void setWis(byte i) {
        wis = i;
    }

    public byte get_int() {
        return _int;
    }

    public void set_int(byte i) {
        _int = i;
    }

    public int getMr() {
        return mr;
    }

    public void setMr(int i) {
        mr = i;
    }

    public int getExp() {
        return exp;
    }

    public void setExp(int i) {
        exp = i;
    }

    public int getLawful() {
        return lawful;
    }

    public void setLawful(int i) {
        lawful = i;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String s) {
        size = s;
    }

    public int getWeakAttr() {
        return weakAttr;
    }

    public void setWeakAttr(int i) {
        weakAttr = i;
    }

    public int getRanged() {
        return ranged;
    }

    public void setRanged(int i) {
        ranged = i;
    }

    public boolean isAgrososc() {
        return agrososc;
    }

    public void setAgrososc(boolean flag) {
        agrososc = flag;
    }

    public boolean isAgrocoi() {
        return agrocoi;
    }

    public void setAgrocoi(boolean flag) {
        agrocoi = flag;
    }

    public boolean isTamable() {
        return tameable;
    }

    public void setTamable(boolean flag) {
        tameable = flag;
    }

    public int getPassispeed() {
        return passispeed;
    }

    public void setPassispeed(int i) {
        passispeed = i;
    }

    public int getAtkspeed() {
        return atkspeed;
    }

    public void setAtkspeed(int i) {
        atkspeed = i;
    }

    public boolean isAgro() {
        return agro;
    }

    public void setAgro(boolean flag) {
        agro = flag;
    }

    public int getGfxid() {
        return gfxid;
    }

    public void setGfxid(int i) {
        gfxid = i;
    }

    public String getNameId() {
        return nameId;
    }

    public void setNameId(String s) {
        nameId = s;
    }

    public int getUndead() {
        return undead;
    }

    public void setUndead(int i) {
        undead = i;
    }

    public int getPoisonAtk() {
        return poisonAtk;
    }

    public void setPoisonAtk(int i) {
        poisonAtk = i;
    }

    public int getParalysisAtk() {
        return paralysisAtk;
    }

    public void setParalysisAtk(int i) {
        paralysisAtk = i;
    }

    public int getFamily() {
        return family;
    }

    public void setFamily(int i) {
        family = i;
    }

    public int getAgroFamily() {
        return agroFamily;
    }

    public void setAgroFamily(int i) {
        agroFamily = i;
    }

    public int isAgroGfxId1() {
        return agroGfxId1;
    }

    public void setAgroGfxId1(int i) {
        agroGfxId1 = i;
    }

    public int isAgroGfxId2() {
        return agroGfxId2;
    }

    public void setAgroGfxId2(int i) {
        agroGfxId2 = i;
    }

    public boolean isPicupItem() {
        return picupItem;
    }

    public void setPicupItem(boolean flag) {
        picupItem = flag;
    }

    public int getDigestItem() {
        return digestItem;
    }

    public void setDigestItem(int i) {
        digestItem = i;
    }

    public boolean isBraveSpeed() {
        return braveSpeed;
    }

    public void setBraveSpeed(boolean flag) {
        braveSpeed = flag;
    }

    public int getHprInterval() {
        return hprInterval;
    }

    public void setHprInterval(int i) {
        hprInterval = i;
    }

    public int getHpr() {
        return hpr;
    }

    public void setHpr(int i) {
        hpr = i;
    }

    public int getMprInterval() {
        return mprInterval;
    }

    public void setMprInterval(int i) {
        mprInterval = i;
    }

    public int getMpr() {
        return mpr;
    }

    public void setMpr(int i) {
        mpr = i;
    }

    public boolean isTeleport() {
        return teleport;
    }

    public void setTeleport(boolean flag) {
        teleport = flag;
    }

    public int getRandomLevel() {
        return randomLevel;
    }

    public void setRandomLevel(int i) {
        randomLevel = i;
    }

    public int getRandomHp() {
        return randomHp;
    }

    public void setRandomHp(int i) {
        randomHp = i;
    }

    public int getRandomMp() {
        return randomMp;
    }

    public void setRandomMp(int i) {
        randomMp = i;
    }

    public int getRandomAc() {
        return randomAc;
    }

    public void setRandomAc(int i) {
        randomAc = i;
    }

    public int getRandomExp() {
        return randomExp;
    }

    public void setRandomExp(int i) {
        randomExp = i;
    }

    public int getRandomLawful() {
        return randomLawful;
    }

    public void setRandomLawful(int i) {
        randomLawful = i;
    }

    public int getDamageReduction() {
        return damageReduction;
    }

    public void setDamageReduction(int i) {
        damageReduction = i;
    }

    public boolean isHard() {
        return hard;
    }

    public void setHard(boolean flag) {
        hard = flag;
    }

    public boolean isDoppel() {
        return doppel;
    }

    public void setDoppel(boolean flag) {
        doppel = flag;
    }

    public boolean getIsTU() {
        return tu;
    }

    public void setIsTU(boolean i) {
        tu = i;
    }

    public boolean getIsErase() {
        return erase;
    }

    public void setIsErase(boolean i) {
        erase = i;
    }

    public int getBowActId() {
        return bowActId;
    }

    public void setBowActId(int i) {
        bowActId = i;
    }

    public int getKarma() {
        return karma;
    }

    public void setKarma(int i) {
        karma = i;
    }

    public int getTransformId() {
        return transformId;
    }

    public void setTransformId(int transformId) {
        this.transformId = transformId;
    }

    public int getTransformGfxId() {
        return transformGfxId;
    }

    public void setTransformGfxId(int i) {
        transformGfxId = i;
    }

    public int getAtkMagicSpeed() {
        return atkMagicSpeed;
    }

    public void setAtkMagicSpeed(int atkMagicSpeed) {
        this.atkMagicSpeed = atkMagicSpeed;
    }

    public int getSubMagicSpeed() {
        return subMagicSpeed;
    }

    public void setSubMagicSpeed(int subMagicSpeed) {
        this.subMagicSpeed = subMagicSpeed;
    }

    public int getLightSize() {
        return lightSize;
    }

    public void setLightSize(int lightSize) {
        this.lightSize = lightSize;
    }

    public boolean isAmountFixed() {
        return amountFixed;
    }

    public void setAmountFixed(boolean fixed) {
        amountFixed = fixed;
    }

    public boolean getChangeHead() {
        return changeHead;
    }

    public void setChangeHead(boolean changeHead) {
        this.changeHead = changeHead;
    }

    public boolean isCantResurrect() {
        return isCantResurrect;
    }

    public void setCantResurrect(boolean isCantResurrect) {
        this.isCantResurrect = isCantResurrect;
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }
}
