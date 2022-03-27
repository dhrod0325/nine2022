package ks.model;

import ks.app.config.prop.CodeConfig;
import ks.util.L1CommonUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static ks.constants.L1Types.*;

public abstract class L1Item implements Serializable {
    private int pickupMent;
    private int magicHitUp;
    private int revival;
    private int revivalPer;
    private int revivalMent;
    private String etc1;
    private String etc2;
    private int type2; // 0=L1EtcItem, 1=L1Weapon, 2=L1Armor
    private int itemId;
    private String name;
    private String nameId;
    private int type;

    // ■■■■■■ L1EtcItem, L1Weapon, L1Armor 에 공통되는 항목 ■■■■■■
    private int type1;
    private int material;
    private int weight;
    private int gfxId;
    private int groundGfxId;
    private int minLevel;
    private int itemDescId;
    private int maxLevel;
    private int bless = 1;
    private boolean tradAble;
    private boolean deleteAble;
    private boolean cantDurability;
    private boolean saveAtOnce;
    private int dmgSmall = 0;
    private int dmgLarge = 0;
    private int safeEnchant = 0;
    private boolean useRoyal = false;
    private boolean useKnight = false;
    private boolean useElf = false;
    private boolean useMage = false;
    private boolean useDarkElf = false;
    private boolean useDragonKnight = false;
    private boolean useBlackWizard = false;
    private boolean useHighPet = false;
    private byte addStr = 0;
    private byte addDex = 0;
    private byte addCon = 0;
    private byte addInt = 0;
    private byte addWis = 0;
    private byte addCha = 0;
    private int addHp = 0;
    private int addMp = 0;
    private int addHpr = 0;
    private int addMpr = 0;
    private int addSp = 0;
    private int mDef = 0;
    private boolean hasteItem = false;
    private int maxUseTime = 0;
    private int useType;
    private int foodVolume;
    private boolean warehouse;
    private int logCheckItem = 0;
    private int itemGrade;
    private int grade; // ● 장신구 단계
    private int price; // ● 가격
    private int deleteSecond;
    private int ignoreReduction;
    private int registElf;
    private int dropSound;

    private int pvpReduction;
    private int pvpDamage;
    private int addStun;
    private int addEr;
    private boolean purchaseAble = false;

    private String color;

    private String statusMsg;
    private int criticalPer;
    private int bowCriticalPer;

    public L1Item() {
    }

    public int getAddStun() {
        return addStun;
    }

    public void setAddStun(int addStun) {
        this.addStun = addStun;
    }

    public boolean isPurchaseAble() {
        return purchaseAble;
    }

    public void setPurchaseAble(boolean purchaseAble) {
        this.purchaseAble = purchaseAble;
    }

    public String getEtc1() {
        return etc1;
    }

    public void setEtc1(String etc1) {
        this.etc1 = etc1;
    }

    public String getEtc2() {
        return etc2;
    }

    public void setEtc2(String etc2) {
        this.etc2 = etc2;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNameId() {
        return nameId;
    }

    public void setNameId(String nameid) {
        nameId = nameid;
    }

    /**
     * 아이템의 종류를 돌려준다.<br>
     *
     * @return <p>
     * [etcitem]<br>
     * 0:arrow, 1:wand, 2:light, 3:gem, 4:totem, 5:firecracker, 6:potion,
     * 7:food, 8:scroll, 9:questitem, 10:spellbook, 11:petitem, 12:other,
     * 13:material, 14:event, 15:sting
     * </p>
     * <p>
     * [weapon]<br>
     * 1:sword, 2:dagger, 3:tohandsword, 4:bow, 5:spear, 6:blunt, 7:staff,
     * 8:throwingknife, 9:arrow, 10:gauntlet, 11:claw, 12:edoryu, 13:singlebow,
     * 14:singlespear, 15:tohandblunt, 16:tohandstaff, 17:kiringku 18chainsword
     * </p>
     * <p>
     * [armor]<br>
     * 1:helm, 2:armor, 3:T, 4:cloak, 5:glove, 6:boots, 7:shield, 8:amulet,
     * 9:ring, 10:belt, 11:ring2, 12:earring 13:garder 14:rune
     */
    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    // ■■■■■■ L1EtcItem, L1Weapon 에 공통되는 항목 ■■■■■■

    /**
     * @return 0 if L1EtcItem, 1 if L1Weapon, 2 if L1Armor
     */
    public int getType2() {
        return type2;
    }

    public void setType2(int type) {
        type2 = type;
    }

    public boolean isEtc() {
        return type2 == 0;
    }

    public boolean isAccessorie() {
        return type >= 8 && type <= 12 && type2 == 2;
    }

    public boolean isArmor() {
        boolean check1 = type >= 1 && type <= 7;
        boolean check2 = type == 13;

        return (check1 || check2) && type2 == 2;
    }

    public boolean isArmorAndRing() {
        return type >= 1 && type <= 14 && type2 == 2;
    }

    // ■■■■■■ L1EtcItem, L1Armor 에 공통되는 항목 ■■■■■■

    // ■■■■■■ L1Weapon, L1Armor 에 공통되는 항목 ■■■■■■

    public boolean isWeapon() {
        return type >= 1 && type <= 18 && type2 == 1;
    }

    /**
     * 아이템의 종류를 돌려준다.<br>
     *
     * @return <p>
     * [weapon]<br>
     * sword:4,
     * dagger:46,
     * tohandsword:50,
     * bow:20,
     * blunt:11,
     * spear:24,
     * staff:40,
     * throwingknife:2922,
     * arrow:0,
     * gauntlet:62,
     * claw:58,
     * edoryu:54,
     * singlebow:20,
     * singlespear:24,
     * tohandblunt:11,
     * tohandstaff:40,
     * kiringku:58,
     * chainsword:24
     * </p>
     */
    public int getType1() {
        return type1;
    }

    public void setType1(int type1) {
        this.type1 = type1;
    }

    /**
     * 아이템의 소재를 돌려준다
     *
     * @return 0:none 1:액체 2:web 3:식물성 4:동물성 5:지 6:포 7:피 8:목 9:골 10:룡의 린 11:철
     * 12:강철 13:동 14:은 15:금 16:플라티나 17:미스릴 18:브락크미스릴 19:유리 20:보석 21:광물
     * 22:오리하르콘
     */
    public int getMaterial() {
        return material;
    }

    public void setMaterial(int material) {
        this.material = material;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getGfxId() {
        return gfxId;
    }

    public void setGfxId(int gfxId) {
        this.gfxId = gfxId;
    }

    public int getGroundGfxId() {
        return groundGfxId;
    }

    public void setGroundGfxId(int groundGfxId) {
        this.groundGfxId = groundGfxId;
    }

    /**
     * 감정시에 표시되는 ItemDesc.tbl의 메세지 ID를 돌려준다.
     */
    public int getItemDescId() {
        return itemDescId;
    }

    public void setItemDescId(int descId) {
        itemDescId = descId;
    }

    public int getMinLevel() {
        return minLevel;
    }

    public void setMinLevel(int level) {
        minLevel = level;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public void setMaxLevel(int maxlvl) {
        maxLevel = maxlvl;
    }

    public int getBless() {
        return bless;
    }

    public void setBless(int i) {
        bless = i;
    }

    public boolean isTradeAble() {
        return tradAble;
    }

    public void setTradAble(boolean flag) {
        tradAble = flag;
    }

    public boolean isDeleteAble() {
        return deleteAble;
    }

    public void setDeleteAble(boolean flag) {
        deleteAble = flag;
    }

    public boolean isCantDurability() {
        return cantDurability;
    }

    public void setCantDurability(boolean cantDurability) {
        this.cantDurability = cantDurability;
    }

    /**
     * 아이템의 개수가 변화했을 때에 곧바로 DB에 기입해야할 것인가를 돌려준다.
     */
    public boolean isToBeSavedAtOnce() {
        return saveAtOnce;
    }

    public void setToBeSavedAtOnce(boolean flag) {
        saveAtOnce = flag;
    }

    public int getDmgSmall() {
        return dmgSmall;
    }

    public void setDmgSmall(int dmgSmall) {
        this.dmgSmall = dmgSmall;
    }

    public int getDmgLarge() {
        return dmgLarge;
    }

    public void setDmgLarge(int dmgLarge) {
        this.dmgLarge = dmgLarge;
    }

    public int getSafeEnchant() {
        return safeEnchant;
    }

    public void set_safeenchant(int safeenchant) {
        safeEnchant = safeenchant;
    }

    public boolean isUseRoyal() {
        return useRoyal;
    }

    public void setUseRoyal(boolean flag) {
        useRoyal = flag;
    }

    public boolean isUseKnight() {
        return useKnight;
    }

    public void setUseKnight(boolean flag) {
        useKnight = flag;
    }

    public boolean isUseElf() {
        return useElf;
    }

    public void setUseElf(boolean flag) {
        useElf = flag;
    }

    public boolean isUseMage() {
        return useMage;
    }

    public void setUseMage(boolean flag) {
        useMage = flag;
    }

    public boolean isUseDarkElf() {
        return useDarkElf;
    }

    public void setUseDarkElf(boolean flag) {
        useDarkElf = flag;
    }

    public boolean isUseDragonKnight() {
        return useDragonKnight;
    }

    public void setUseDragonKnight(boolean flag) {
        useDragonKnight = flag;
    }

    public boolean isUseBlackWizard() {
        return useBlackWizard;
    }

    public void setUseBlackWizard(boolean flag) {
        useBlackWizard = flag;
    }

    public boolean isUseHighPet() {
        return useHighPet;
    }

    public void setUseHighPet(boolean flag) {
        useHighPet = flag;
    }

    public byte getAddStr() {
        return addStr;
    }

    public void setAddStr(byte addstr) {
        addStr = addstr;
    }

    public byte getAddDex() {
        return addDex;
    }

    public void setAddDex(byte adddex) {
        addDex = adddex;
    }

    public byte getAddCon() {
        return addCon;
    }

    public void setAddCon(byte addcon) {
        addCon = addcon;
    }

    public byte getAddInt() {
        return addInt;
    }

    public void setAddInt(byte addint) {
        addInt = addint;
    }

    public byte getAddWis() {
        return addWis;
    }

    public void setAddWis(byte addwis) {
        addWis = addwis;
    }

    public byte getAddCha() {
        return addCha;
    }

    public void setAddCha(byte addcha) {
        addCha = addcha;
    }

    public int getAddHp() {
        return addHp;
    }

    public void setAddHp(int addhp) {
        addHp = addhp;
    }

    public int getAddMp() {
        return addMp;
    }

    public void setAddMp(int addmp) {
        addMp = addmp;
    }

    public int getAddHpr() {
        return addHpr;
    }

    public void setAddHpr(int addhpr) {
        addHpr = addhpr;
    }

    public int getAddMpr() {
        return addMpr;
    }

    public void setAddMpr(int addmpr) {
        addMpr = addmpr;
    }

    public int getAddSp() {
        return addSp;
    }

    public void setAddSp(int addsp) {
        addSp = addsp;
    }

    public int getmDef() {
        return mDef;
    }

    public void setmDef(int i) {
        this.mDef = i;
    }

    public boolean isHasteItem() {
        return hasteItem;
    }

    public void setHasteItem(boolean flag) {
        hasteItem = flag;
    }

    public int getMaxUseTime() {
        return maxUseTime;
    }

    public void setMaxUseTime(int i) {
        maxUseTime = i;
    }

    public int getUseType() {
        return useType;
    }

    public void setUseType(int useType) {
        this.useType = useType;
    }

    public int getFoodVolume() {
        return foodVolume;
    }

    public void setFoodVolume(int volume) {
        foodVolume = volume;
    }

    public boolean isWarehouse() {
        return warehouse;
    }

    public void setWarehouse(boolean warehouse) {
        this.warehouse = warehouse;
    }

    /**
     * 램프등의 아이템으로 설정되어 있는 밝음을 돌려준다.
     */
    public int getLightRange() {
        if (itemId == 40001) {
            return 11;
        } else if (itemId == 40002) {
            return 14;
        } else if (itemId == 40004) {
            return 14;
        } else if (itemId == 40005) {
            return 8;
        } else {
            return 0;
        }
    }

    public int getLightFuel() {
        if (itemId == 40001) {
            return 6000;
        } else if (itemId == 40002) {
            return 12000;
        } else if (itemId == 40003) {
            return 12000;
        } else if (itemId == 40004) {
            return 0;
        } else if (itemId == 40005) {
            return 600;
        } else {
            return 0;
        }
    }

    // ■■■■■■ L1EtcItem 로 오버라이드(override) 하는 항목 ■■■■■■
    public boolean isStackable() {
        return false;
    }

    public int getLocx() {
        return 0;
    }

    public int getLocY() {
        return 0;
    }

    public short getMapid() {
        return 0;
    }

    public int getDelayId() {
        return 0;
    }

    public int getDelayTime() {
        return 0;
    }

    public int getMaxChargeCount() {
        return 0;
    }

    public int getDelayEffect() {
        return 0;
    }

    public int getlogcheckitem() {
        return logCheckItem;
    }

    public void setlogcheckitem(int i) {
        logCheckItem = i;
    }

    public int getRange() {
        return 0;
    }

    public int getHitModifier() {
        return 0;
    }

    public int getDmgModifier() {
        return 0;
    }

    public int getaddDmg() {
        return 0;
    }

    public int getDoubleDmgChance() {
        return 0;
    }

    public int getMagicDmgModifier() {
        return 0;
    }

    public int getCanbeDmg() {
        return 0;
    }

    public boolean isTwoHandedWeapon() {
        return false;
    }

    // ■■■■■■ L1Armor 로 오버라이드(override) 하는 항목 ■■■■■■
    public int getAc() {
        return 0;
    }

    public int getDamageReduction() {
        return 0;
    }

    public int getWeightReduction() {
        return 0;
    }

    public int getDmgUp() {
        if (!isBow())
            return getDmgModifier();

        return 0;
    }

    public int getHitUp() {
        if (!isBow()) {
            return getHitModifier();
        }

        return 0;
    }

    public int getBowHitUp() {
        if (isBow()) {
            return getHitModifier();
        }

        return 0;
    }

    public int getBowDmgUp() {
        if (isBow()) {
            return getDmgModifier();
        }

        return 0;
    }

    public boolean isBow() {
        return getType1() == TYPE1_WEAPON_TYPE_SINGLE_BOW || getType1() == TYPE1_WEAPON_TYPE_GAUNTLET || getType1() == TYPE1_WEAPON_TYPE_BOW;
    }

    public int getDefenseWater() {
        return 0;
    }

    public int getDefenseFire() {
        return 0;
    }

    public int getDefenseEarth() {
        return 0;
    }

    public int getDefenseWind() {
        return 0;
    }

    public int getRegistStun() {
        return 0;
    }

    public int getRegistStone() {
        return 0;
    }

    public int getRegistSleep() {
        return 0;
    }

    public int getRegistFreeze() {
        return 0;
    }

    public int getRegistSustAin() {
        return 0;
    }

    public int getRegistBlind() {
        return 0;
    }

    public int getItemGrade() {
        return itemGrade;
    }

    public void setItemGrade(int itemGrade) {
        this.itemGrade = itemGrade;
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getRevival() {
        return revival;
    }

    public void setRevival(int revival) {
        this.revival = revival;
    }

    public int getRevivalPer() {
        return revivalPer;
    }

    public void setRevivalPer(int revivalPer) {
        this.revivalPer = revivalPer;
    }

    public int getPickupMent() {
        return pickupMent;
    }

    public void setPickupMent(int pickupMent) {
        this.pickupMent = pickupMent;
    }

    public int getMagicHitUp() {
        return magicHitUp;
    }

    public void setMagicHitUp(int magicHitUp) {
        this.magicHitUp = magicHitUp;
    }

    public int getDeleteSecond() {
        return deleteSecond;
    }

    public void setDeleteSecond(int deleteSecond) {
        this.deleteSecond = deleteSecond;
    }

    public int getRegistElf() {
        return 0;
    }

    public void setRegistElf(int registElf) {
        this.registElf = registElf;
    }

    public boolean isStaff() {
        return getType() == 7 || getType() == 16;
    }

    public int getIgnoreReduction() {
        return ignoreReduction;
    }

    public void setIgnoreReduction(int ignoreReduction) {
        this.ignoreReduction = ignoreReduction;
    }

    public int getPvpReduction() {
        return pvpReduction;
    }

    public void setPvpReduction(int pvpReduction) {
        this.pvpReduction = pvpReduction;
    }

    public int getPvpDamage() {
        return pvpDamage;
    }

    public void setPvpDamage(int pvpDamage) {
        this.pvpDamage = pvpDamage;
    }

    public int getAddEr() {
        return addEr;
    }

    public void setAddEr(int addEr) {
        this.addEr = addEr;
    }

    public int getCriticalPer() {
        return criticalPer;
    }

    public void setCriticalPer(int criticalPer) {
        this.criticalPer = criticalPer;
    }

    public int getBowCriticalPer() {
        return bowCriticalPer;
    }

    public void setBowCriticalPer(int bowCriticalPer) {
        this.bowCriticalPer = bowCriticalPer;
    }

    public String getStatusMsg() {
        return statusMsg;
    }

    public void setStatusMsg(String statusMsg) {
        this.statusMsg = statusMsg;
    }

    public List<String> getStatusMsgs() {
        if (StringUtils.isEmpty(statusMsg)) {
            return Collections.emptyList();
        }

        String[] s = statusMsg.split(",");

        return new ArrayList<>(Arrays.asList(s));
    }

    public String getColor() {
        if (StringUtils.isEmpty(color) && CodeConfig.USE_ITEM_COLOR_BY_GRADE) {
            color = L1CommonUtils.getGradeByColor(getGrade());
        }

        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getDropSound() {
        return dropSound;
    }

    public int getRevivalMent() {
        return revivalMent;
    }

    public void setRevivalMent(int revivalMent) {
        this.revivalMent = revivalMent;
    }

    public void setDropSound(int dropSound) {
        this.dropSound = dropSound;
    }

    public int getMagicCatalystType() {
        int type = 0;

        switch (getItemId()) {
            case 40318: // 마력의 돌
                type = 166; // 재료에 의한 아이콘 패키지
                break;
            case 40319: // 정령 옥
                type = 569;
                break;
            case 40321: // 흑요석
                type = 837;
                break;
            case 430006: // 유그 드라 열매
                type = 3674;
                break;
            case 430007: // 각인의 뼈 조각
                type = 3605;
                break;
            case 430008: // 속성석
                type = 3606;
                break;
        }

        return type;
    }

}
