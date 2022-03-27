package ks.model.instance;

import ks.app.config.prop.CodeConfig;
import ks.constants.*;
import ks.core.datatables.dollBonus.DollBonus;
import ks.core.datatables.dollBonus.DollBonusTable;
import ks.core.datatables.enchantBonus.EnchantBonus;
import ks.core.datatables.enchantBonus.EnchantBonusTable;
import ks.core.datatables.pet.PetTable;
import ks.core.datatables.weaponSkill.ItemSkill;
import ks.core.datatables.weaponSkill.ItemSkillTable;
import ks.model.*;
import ks.model.attack.utils.L1ArmorUtils;
import ks.model.attack.utils.L1WeaponUtils;
import ks.model.item.characterTrade.CharacterTradeDao;
import ks.model.item.characterTrade.CharacterTradeInfo;
import ks.model.pc.L1PcInstance;
import ks.packets.clientpackets.ClientBasePacket;
import ks.packets.serverpackets.S_OwnCharStatus;
import ks.packets.serverpackets.S_PacketBox;
import ks.scheduler.ItemTimerScheduler;
import ks.util.L1CommonUtils;
import ks.util.common.L1ItemOutputStream;
import ks.util.common.NumberUtils;
import ks.util.log.L1LogUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static ks.constants.L1Types.*;

public class L1ItemInstance extends L1Object {
    protected final Logger logger = LogManager.getLogger();

    private static final SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm", Locale.KOREA);

    private final L1ItemLastStatus lastStatus = new L1ItemLastStatus(this);

    private final L1Delay itemDelay = new L1Delay();
    private int count = 1;
    private int itemId;
    private L1Item item;
    private boolean isEquipped = false;
    private int enchantLevel = 0;
    private int attrEnchantLevel;
    private boolean identified = false;
    private int durability;
    private int chargeCount;
    private int remainingTime;
    private Timestamp lastUsed = null;
    private int bless = 1;
    private int lastWeight;
    private L1PcInstance pc;
    private boolean magicRunning = false;
    private Timestamp endTime = null;
    private boolean isPackage = false;
    private int clock;
    private int protection;
    private long ownerTime = 0;
    private long enchantTime = 0;
    private int nextReq;

    private CharacterTradeInfo characterTradeInfo;

    private int gfxId;
    private int acByMagic = 0;
    private int hitByMagic = 0;
    private int holyDmgByMagic = 0;
    private int dmgByMagic = 0;

    private L1PcInstance equipPc = null;
    private L1PcInstance owner;

    private boolean isNowLighting = false;

    private int secondId;
    private int roundId;
    private int ticketId = -1; // 티겟 번호
    private int dropMobId = 0;
    private boolean isWorking = false;
    private int skillIcon = 0;
    private int keyId;
    private int optionGrade;

    public L1ItemInstance() {
        clock = 0;
    }

    public L1ItemInstance(L1Item item, int count) {
        this();
        setItem(item);
        setCount(count);
    }

    public L1ItemInstance(L1Item item) {
        this(item, 1);
    }

    public int getOptionGrade() {
        return optionGrade;
    }

    public void setOptionGrade(int optionGrade) {
        this.optionGrade = optionGrade;
    }

    public int getClock() {
        return clock;
    }

    public void setClock(int clock) {
        this.clock = clock;
    }

    public int getProtection() {
        return protection;
    }

    public void setProtection(int protection) {
        this.protection = protection;
    }

    public void clickItem(L1Character cha, ClientBasePacket packet) {
    }

    public boolean isIdentified() {
        return identified;
    }

    public void setIdentified(boolean identified) {
        this.identified = identified;
    }

    public String getName() {
        return item.getName();
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public boolean isEquipped() {
        return isEquipped;
    }

    public void setEquipped(boolean equipped) {
        isEquipped = equipped;
    }

    public L1Item getItem() {
        return item;
    }

    public void setItem(L1Item item) {
        this.item = item;
        itemId = item.getItemId();
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public boolean isStackable() {
        return item.isStackable();
    }

    @Override
    public void onAction(L1PcInstance player) {
    }

    public int getEnchantLevel() {
        return enchantLevel;
    }

    public void setEnchantLevel(int enchantLevel) {
        this.enchantLevel = enchantLevel;
    }

    public int getAttrEnchantLevel() {
        return attrEnchantLevel;
    }

    public void setAttrEnchantLevel(int attrenchantLevel) {
        this.attrEnchantLevel = attrenchantLevel;
    }

    public int getGfxId() {
        if (gfxId == 0) {
            return item.getGfxId();
        } else {
            return gfxId;
        }
    }

    public void setGfxId(int gfxId) {
        this.gfxId = gfxId;
    }

    public int getDurability() {
        return durability;
    }

    public void setDurability(int i) {
        if (i < 0) {
            i = 0;
        }

        if (i > 127) {
            i = 127;
        }

        durability = i;
    }

    public int getChargeCount() {
        return chargeCount;
    }

    public void setChargeCount(int i) {
        chargeCount = i;
    }

    public int getRemainingTime() {
        return remainingTime;
    }

    public void setRemainingTime(int i) {
        remainingTime = i;
    }

    public Timestamp getLastUsed() {
        return lastUsed;
    }

    public void setLastUsed(Timestamp t) {
        lastUsed = t;
    }

    public int getBless() {
        return bless;
    }

    public boolean isBless() {
        return getBless() == 0;
    }

    public void setBless(int i) {
        bless = i;
    }

    public int getLastWeight() {
        return lastWeight;
    }

    public void setLastWeight(int weight) {
        lastWeight = weight;
    }

    public Timestamp getEndTime() {
        return endTime;
    }

    public void setEndTime(Timestamp t) {
        endTime = t;
    }

    public boolean isPackage() {
        return isPackage;
    }

    public void setPackage(boolean _isPackage) {
        this.isPackage = _isPackage;
    }

    public double getAddPotionPer() {
        double result = 0;

        EnchantBonus e = EnchantBonusTable.getInstance().getEnchantBonus(this);

        if (e != null) {
            result += e.getAddPotionPer();
        }

        result += L1Acc.calcPotionPer(this, enchantLevel);

        if (result < 0) {
            result = 0;
        }

        return result;
    }

    public int getMr() {
        int mr = item.getmDef();
        int itemId = getItemId();

        if (NumberUtils.contains(itemId,
                20117, 20011, 20110, 120011,
                76796,
                55000100,
                55000101,
                55000102,
                55000103
        ) || L1CommonUtils.isRindArmor(this.itemId)) {
            mr += getEnchantLevel();
        } else if (NumberUtils.contains(itemId, 20056, 120056, 220056, 5000056, 55000084, 55000054, 55000085)) {
            mr += getEnchantLevel() * 2;
        } else if (NumberUtils.contains(itemId, 20078, 20079, 20049, 20050, 55000104)) {
            mr += getEnchantLevel() * 3;
        }

        mr += L1EarRing.calcMr(itemId, enchantLevel, bless);
        mr += L1Ring.calcMr(itemId, enchantLevel, bless);
        mr += L1Acc.calcMr(this, enchantLevel);

        EnchantBonus e = EnchantBonusTable.getInstance().getEnchantBonus(this);

        if (e != null) {
            mr += e.getAddMr();
        }

        if (mr < 0) {
            mr = 0;
        }

        return mr;
    }

    public int getWeight() {
        if (item.getWeight() == 0) {
            return 0;
        } else {
            int w = 0;

            for (int i = 0; i < getCount(); i++) {
                w += item.getWeight() / 1000;
            }

            return w;
        }
    }

    public L1ItemLastStatus getLastStatus() {
        return lastStatus;
    }

    public int getRecordingColumns() {
        int column = 0;
        if (getClock() != lastStatus.clock) {
            column += L1PcInventory.COL_CLOCK;
        }
        if (getCount() != lastStatus.count) {
            column += L1PcInventory.COL_COUNT;
        }
        if (getItemId() != lastStatus.itemId) {
            column += L1PcInventory.COL_ITEMID;
        }
        if (isEquipped() != lastStatus.isEquipped) {
            column += L1PcInventory.COL_EQUIPPED;
        }
        if (getEnchantLevel() != lastStatus.enchantLevel) {
            column += L1PcInventory.COL_ENCHANTLVL;
        }
        if (getDurability() != lastStatus.durability) {
            column += L1PcInventory.COL_DURABILITY;
        }
        if (getChargeCount() != lastStatus.chargeCount) {
            column += L1PcInventory.COL_CHARGE_COUNT;
        }
        if (getLastUsed() != lastStatus.lastUsed) {
            column += L1PcInventory.COL_DELAY_EFFECT;
        }
        if (isIdentified() != lastStatus.isIdentified) {
            column += L1PcInventory.COL_IS_ID;
        }
        if (getRemainingTime() != lastStatus.remainingTime) {
            column += L1PcInventory.COL_REMAINING_TIME;
        }
        if (getBless() != lastStatus.bless) {
            column += L1PcInventory.COL_BLESS;
        }
        if (getAttrEnchantLevel() != lastStatus.attrenchantLevel) {
            column += L1PcInventory.COL_ATTRENCHANTLVL;
        }
        if (getProtection() != lastStatus.protection) { //추가
            column += L1PcInventory.COL_PROTEC;
        }
        if (getEnchantLevel() != lastStatus.enchantLevel) {
            column += L1PcInventory.COL_PANDORA;
        }
        if (getEnchantLevel() != lastStatus.optionGrade) {
            column += L1PcInventory.COL_OPTION;
        }
        if (getEnchantLevel() != lastStatus.optionGrade) {
            column += L1PcInventory.COL_OPTION;
        }

        return column;
    }

    public String getNumberedViewName(int count) {
        return getNumberedViewName(getNumberedName(count));
    }

    public String getNumberedViewName(String numberedName) {
        StringBuilder name = new StringBuilder(numberedName);

        try {
            int itemType2 = item.getType2();
            int itemId = item.getItemId();

            if (itemId == 40314 || itemId == 40316) {
                L1Pet pet = PetTable.getInstance().getTemplate(getId());

                if (pet != null) {
                    name.append("[Lv.").append(pet.getLevel()).append(" ").append(pet.getName()).append("]");
                }
            }

            if (item.getType2() == 0 && item.getType() == 2) { // light
                if (isNowLighting()) {
                    name.append(" ($10)");
                }
                if (itemId == 40001 || itemId == 40002) {
                    if (getRemainingTime() <= 0) {
                        name.append(" ($11)");
                    }
                }
            }

            if (getEndTime() != null) {
                long endTime = getEndTime().getTime();
                String endTimeStr = sdf.format(endTime);

                if (item.getItemId() == L1ItemId.TEBEOSIRIS_KEY) {

                } else if (item.getItemId() == L1ItemId.TIKAL_KEY) {
                } else if (endTime > 0) {
                    name.append(" [").append(endTimeStr).append("]");
                }
            }

            if (itemId == 6000069) {
                if (characterTradeInfo == null) {
                    characterTradeInfo = CharacterTradeDao.getInstance().getInfo(getId());
                }

                if (characterTradeInfo != null) {
                    name.append(String.format(" (" + characterTradeInfo.getTargetName() + " LV:%d)", characterTradeInfo.getTargetPc().getLevel()));
                    name.append(" [").append(characterTradeInfo.getItemObjectId()).append("]");
                }
            }

            if (isEquipped()) {
                if (itemType2 == 1) {
                    name.append(" ($9)");
                } else if (itemType2 == 2 && !item.isUseHighPet()) {
                    name.append(" ($117)");
                }
            }

            if (pc != null) {
                if (itemId == 60001301) {
                    if ("true".equals(pc.getDataMap().get(L1DataMapKey.PC_ATTACK))) {
                        name.append(" ($117)");
                    }
                } else if (pc.getInventory().getArrowId() == itemId) {
                    name.append(" ($117)");
                }
            }
        } catch (Exception e) {
            logger.error(e);
        }

        return name.toString();
    }

    public String getLogName(int cnt) {
        return getNumberedName(cnt);
    }

    public String getLogName() {
        return getNumberedName(count);
    }

    public String getViewName() {
        return getNumberedViewName(count);
    }

    public String getViewName2() {
        return getNumberedViewName(getNumberedName2(count));
    }

    public String getNumberedName(int count) {
        return getString(count);
    }

    public String getNumberedName2(int count) {
        return getString(count);
    }

    private String getString(int count) {
        StringBuilder name = new StringBuilder();

        String color = item.getColor();

        if (!StringUtils.isEmpty(color)) {
            name.append("\\").append(color);
        }

        if (isIdentified()) {
            if (item.getType2() == 1 || item.getType2() == 2) {
                String attrName = L1CommonUtils.getAttrNameKr(getAttrEnchantLevel());

                name.append(attrName);

                if (getEnchantLevel() >= 0) {
                    if (!StringUtils.isEmpty(attrName)) {
                        name.append(" ");
                    }

                    name.append("+").append(getEnchantLevel()).append(" ");
                } else if (getEnchantLevel() < 0) {
                    name.append(getEnchantLevel()).append(" ");
                }
            }
        }

        name.append(item.getName());

        if (isIdentified()) {
            if (item.getItemId() == 20383) {
                name.append(" (").append(getChargeCount()).append(")");
            }

            if (item.getMaxUseTime() > 0 && item.getType2() != 0) {
                name.append(" [").append(getRemainingTime()).append("]");
            }

            if (getProtection() == 1) {
                name.append("(보호중)");
            }
        }

        if (item.getMaxChargeCount() > 0) {
            name.append(" (").append(getChargeCount()).append(")");
        }

        if (count > 1) {
            name.append(" (").append(count).append(")");
        }

        return name.toString();
    }

    public byte[] getStatusBytes() {
        int itemType2 = item.getType2();
        int itemId = getItemId();

        L1ItemOutputStream os = new L1ItemOutputStream();

        if (itemType2 == 0) {
            switch (item.getType()) {
                case 2:
                    os.writeLightRange(item.getLightRange());
                    break;
                case 0:
                case 15:
                    os.writeDamage(item.getDmgSmall(), item.getDmgLarge());
                    break;
            }

            os.writeMaterial(item.getMaterial(), getWeight());

            switch (itemId) {
                case 40314:
                case 40316:
                    L1Pet pet = PetTable.getInstance().getTemplate(getId());
                    if (pet != null) {
                        os.writePetInfo(pet.getType().getType1(), pet.getType().getType2(), pet.getLevel(), pet.getHp());
                    }
                    break;
                case 9800:
                    os.writeDmgUp(2);
                    os.writeHitUp(1);
                    os.writeAddHpRegen(2);
                    os.writeAddMpRegen(2);
                    os.writeAddReduction(2);
                    os.writeAddExp(2);
                    os.writeRegistAll(10);
                    break;
                case 9801:
                    os.writeBowDmgUp(2);
                    os.writeBowDmgUp(1);
                    os.writeAddHpRegen(2);
                    os.writeAddMpRegen(2);
                    os.writeAddReduction(2);
                    os.writeAddExp(2);
                    os.writeRegistAll(10);
                    break;
                case 9802:
                    os.writeAddSp(2);
                    os.writeAddReduction(2);
                    os.writeAddHpRegen(2);
                    os.writeAddMpRegen(3);
                    os.writeRegistAll(10);
                    os.writeAddExp(2);
                    break;
                case 9803:
                    os.writeAddExp(4);
                    os.writeAddReduction(2);
                    break;
                case 437004:
                case 439117:
                    os.writeDmgUp(3);
                    os.writeHitUp(3);
                    os.writeBowDmgUp(3);
                    os.writeBowHitUp(3);
                    os.writeAddSp(3);
                    break;
                case 437003:
                    os.writeAddMaxMP(40);
                    os.writeAddMpRegen(4);
                    break;
                case 437002:
                    os.writeAddMaxHp(50);
                    os.writeAddHpRegen(4);
                    break;
                case 41288:
                case 41280:
                    os.writeAddAc(1);
                    break;
                case 41286:
                case 41278:
                    os.writeAddMaxHp(30);
                    break;
                case 41289: // 환상 과일 샐러드
                case 41281: // 과일 샐러드
                    os.writeAddMpRegen(20);
                    break;
                case 41290: // 환상 과일 탕수육
                case 41282: // 과일 탕수육
                    os.writeAddHpRegen(3);
                    break;
                case 41285: // 환상 괴물 눈 스테이크
                case 41277: // 괴물 눈 스테이크
                    os.writeRegistEarth(10);
                    os.writeRegistFire(10);
                    os.writeRegistWater(10);
                    os.writeRegistWind(10);
                    break;
                case 41291: // 환상 멧돼지 꼬치 구이
                case 41283: // 멧돼지 꼬치 구이
                    os.writeAddMr(5);
                    break;
                case 41292: // 환상 버섯스프
                case 41284: // 버섯스프
                    os.writeAddExp(1);
                    break;
                case 41287: // 환상 씨호떡
                case 41279: // 씨호떡
                    os.writeAddMpRegen(3);
                    break;
                case 49063: // 환상 거미 다리 꼬치 구이
                case 49055: // 거미 다리 꼬치 구이
                    os.writeAddSp(1);
                    break;
                case 49061: // 환상 스콜피온 구이
                case 49053: // 스콜피온 구이
                    os.writeAddHpRegen(2);
                    os.writeAddMpRegen(2);
                    break;
                case 49058: // 환상 악어 스테이크
                case 49050: // 악어 스테이크
                    os.writeAddMaxHp(30);
                    os.writeAddMaxHp(30);
                    break;
                case 49062: // 환상 일렉카둠 스튜
                case 49054: // 일렉카둠 스튜
                    os.writeAddMr(10);
                    break;
                case 49057: // 환상 캐비어 카나페
                case 49049: // 캐비어 카나페
                    os.writeDmgUp(1);
                    os.writeHitUp(1);
                    break;
                case 49064: // 환상 크랩살스프
                case 49056: // 크랩살스프
                    os.writeAddExp(2);
                    break;
                case 49060: // 환상 키위 패롯 구이
                case 49052: // 키위 패롯 구이
                    os.writeBowDmgUp(1);
                    os.writeBowHitUp(1);
                    break;
                case 49059: // 환상 터틀 드래곤 과자
                case 49051: // 터틀 드래곤 과자
                    os.writeAddAc(2);
                    break;
                case 436018: // 환상 그리폰 구이
                case 436010: // 그리폰 구이
                    os.writeAddMaxHp(50);
                    os.writeAddMaxHp(50);
                    break;
                case 436020: // 환상 대왕거북 구이
                case 436012: // 대왕거북 구이
                    os.writeAddAc(3);
                    break;
                case 436022: // 환상 드레이크 구이
                case 436014: // 드레이크 구이
                    os.writeAddSp(2);
                    os.writeAddMpRegen(2);
                    break;
                case 436021: // 환상 레서 드래곤 날개 꼬치
                case 436013: // 레서 드래곤 날개 꼬치
                    os.writeAddMr(15);
                    os.writeRegistEarth(10);
                    os.writeRegistFire(10);
                    os.writeRegistWater(10);
                    os.writeRegistWind(10);
                    break;
                case 436024: // 환상 바실리스크 알 스프
                case 436016: // 바실리스크 알 스프
                    os.writeAddExp(10);
                    break;
                case 436023: // 환상 심해어 스튜
                case 436015: // 심해어 스튜
                    os.writeAddMaxHp(30);
                    os.writeAddHpRegen(2);
                    break;
                case 436019: // 환상 코카트리스 스테이크
                case 436011: // 코카트리스 스테이크
                    os.writeHitUp(2);
                    os.writeDmgUp(1);
                    break;
                case 436017: // 환상 크러스트시안 집게발 구이
                case 436009: // 크러스트시안 집게발 구이
                    os.writeBowHitUp(2);
                    os.writeBowDmgUp(1);
                    break;
            }

            if (getName().startsWith("마법인형 :")) {
                int step = L1CommonUtils.getDollStep(itemId);

                if (step > 0) {
                    os.writeAddMsg("등급 : " + step + "단계");
                }

                DollBonus e = DollBonusTable.getInstance().getEnchantBonus(this);

                if (e != null) {
                    if (e.getReduction() > 0) {
                        os.writeAddReduction(e.getReduction());
                    }

                    if (!StringUtils.isEmpty(e.getPerDmgNote())) {
                        os.writeAddMsg(e.getPerDmgNote());
                    }

                    if (e.getWeight() > 0) {
                        os.writeAddWeightReduction(e.getWeight());
                    }

                    if (e.getAc() < 0) {
                        os.writeAddMsg("AC " + (-e.getAc()) + "+0");
                    }

                    if (e.getAddHp() > 0) {
                        os.writeAddMaxHp(e.getAddHp());
                    }

                    if (e.getAddMp() > 0) {
                        os.writeAddMaxMP(e.getAddMp());
                    }

                    if (e.getAbMpr() > 0) {
                        os.writeAddMsg("MP회복 : +" + e.getAbMpr() + "(" + e.getAbMprTime() + "초)");
                    }
                    if (e.getAbHpr() > 0) {
                        os.writeAddMsg("HP회복 : +" + e.getAbHpr() + "(" + e.getAbHprTime() + "초)");
                    }

                    if (e.getAddBowDmg() > 0) {
                        os.writeBowDmgUp(e.getAddBowDmg());
                    }

                    if (e.getAddBowHitUp() > 0) {
                        os.writeBowHitUp(e.getAddBowHitUp());
                    }

                    if (e.getAddDmg() > 0) {
                        os.writeDmgUp(e.getAddDmg());
                    }

                    if (e.getAddHitUp() > 0) {
                        os.writeHitUp(e.getAddHitUp());
                    }
                    if (e.getAddSp() > 0) {
                        os.writeAddSp(e.getAddSp());
                    }
                    if (e.getAddExp() > 0) {
                        os.writeAddExp(e.getAddExp());
                    }

                    if (e.getRegistStun() > 0) {
                        os.writeRegistStun(e.getRegistStun());
                    }

                    if (e.getStunHitUp() > 0) {
                        os.writeAddStunHit(e.getStunHitUp());
                    }

                    if (e.getPvpDmg() > 0) {
                        os.writeAddPvpDamage(e.getPvpDmg());
                    }

                    if (e.getPvpReduction() > 0) {
                        os.writeAddPvpReduction(e.getPvpReduction());
                    }

                    if (e.getMagicHit() > 0) {
                        os.writeAddMagicHitUp(e.getMagicHit());
                    }
                }
            }

            if (getAddStunHit() > 0) {
                os.writeAddStunHit(getAddStunHit());
            }

            if (L1CommonUtils.isFantasyFood(itemId)) {
                os.writeAddReduction(5);
            }
        } else if (itemType2 == 1 || itemType2 == 2) {
            if (item.isWeapon()) {
                os.writeWeaponInfo(item.getDmgSmall(), item.getDmgLarge(), item.getMaterial());
            } else if (item.isArmor()) {
                os.writeAcType2(getAc() + getEnchantLevel(), item.getMaterial(), item.getGrade());
            } else {
                os.writeAcType2(getAc(), item.getMaterial(), item.getGrade());
            }

            os.writeD(getWeight());

            if (item.isWeapon()) {
                if (getEnchantLevel() != 0 && !(itemType2 == 2 && getItem().getGrade() >= 0)) {
                    os.writeEnchantLevel(getEnchantLevel());
                }
            } else if (item.isArmor()) {
                os.writeEnchantLevel(getEnchantLevel());
            }

            if (getDurability() != 0) {
                os.writeDurability(durability);
            }

            if (item.isArmorAndRing()) {
                if (getHitUpByArmor() != 0) {
                    os.writeHitUp(getHitUpByArmor());
                }

                if (getDmgUpByArmor() != 0) {
                    os.writeDmgUp(getDmgUpByArmor());
                }

                if (getBowHitUpByArmor() != 0) {
                    os.writeBowHitUp(getBowHitUpByArmor());
                }

                if (getBowDmgUpByArmor() != 0) {
                    os.writeBowDmgUp(getBowDmgUpByArmor());
                }

                if (getDamageReductionByArmor() > 0) {
                    os.writeAddReduction(getDamageReductionByArmor());
                }

                if (getAddDmgUpByArmor() > 0) {
                    os.writeAddDamage(getAddDmgUpByArmor());
                }

                if (getAddHitUpByArmor() > 0) {
                    os.writeAddHitUp(getAddHitUpByArmor());
                }


                L1ArmorUtils.EarRingInfo earRingInfo = L1ArmorUtils.getEarRingInfo(this);

                if (earRingInfo != null) {
                    if (earRingInfo.getDamage() > 0) {
                        os.writeAddMsg("적용 대미지 : +" + earRingInfo.getDamage());
                    }

                    if (earRingInfo.getPer() > 0) {
                        os.writeAddMsg("발동 확률 : " + earRingInfo.getPer() + "%");
                    }
                }
            } else if (item.isWeapon()) {
                if (getHitUp() > 0) {
                    os.writeHitUp(getHitUp());
                }

                if (getDmgUp() > 0) {
                    os.writeDmgUp(getDmgUp());
                }

                if (getBowHitup() > 0) {
                    os.writeBowHitUp(getBowHitup());
                }

                if (getBowDmgUp() > 0) {
                    os.writeBowDmgUp(getBowDmgUp());
                }

                if (getAddDmgUp() > 0) {
                    os.writeAddDamage(getAddDmgUp());
                }
            }

            if (getPvpDamage() > 0) {
                os.writeAddPvpDamage(getPvpDamage());
            }

            if (getPvpReduction() > 0) {
                os.writeAddPvpReduction(getPvpReduction());
            }

            if (itemId == 126 || itemId == 127 || itemId == 412002 || itemId == 4500091 || itemId == 4500111 || itemId == 450011 || itemId == 450012 || itemId == 450013 || itemId == 134 || itemId == 45000623) {
                os.writeMpDrain();
            }

            if (itemId == 412001 || itemId == 112001 || itemId == 450008 || itemId == 450009 || itemId == 450010 || itemId == 4500081 || itemId == 4500101) {
                os.writeHpDrain();
            }

            if (getAddStr() != 0) {
                os.writeAddStr(getAddStr());
            }

            if (item.getAddDex() != 0) {
                os.writeAddDex(item.getAddDex());
            }

            if (item.getAddCon() != 0) {
                os.writeAddCon(item.getAddCon());
            }

            if (item.getAddWis() != 0) {
                os.writeAddWis(item.getAddWis());
            }

            if (getAddInt() != 0) {
                os.writeAddInt(getAddInt());
            }

            if (item.getAddCha() != 0) {
                os.writeAddCha(item.getAddCha());
            }

            if (getAddHpr() != 0) {
                os.writeAddHpRegen(getAddHpr());
            }
            if (getAddMpr() != 0) {
                os.writeAddMpRegen(getAddMpr());
            }

            if (getAddMp() != 0) {
                os.writeAddMaxMP(getAddMp());
            }

            if (getAddHp() > 0) {
                os.writeAddMaxHp(getAddHp());
            }

            if (getMr() != 0) {
                os.writeAddMr(getMr());
            }

            if (getAddSp() != 0) {
                os.writeAddSp(getAddSp());
            }

            if (getAddMagicHitUp() > 0) {
                os.writeAddMagicHitUp(getAddMagicHitUp());
            }

            if (item.isHasteItem()) {
                os.writeHaste();
            }

            if (item.getDefenseFire() != 0) {
                os.writeRegistFire(item.getDefenseFire());
            }

            if (item.getDefenseWater() != 0) {
                os.writeRegistWater(item.getDefenseWater());
            }

            if (item.getDefenseWind() != 0) {
                os.writeRegistWind(item.getDefenseWind());
            }

            if (item.getDefenseEarth() != 0) {
                os.writeRegistEarth(item.getDefenseEarth());
            }

            if (item.getRegistFreeze() > 0) {
                os.writeRegistInfo(item.getRegistFreeze(), 1);
            }

            if (item.getRegistStone() > 0) {
                os.writeRegistInfo(item.getRegistStone(), 2);
            }

            if (item.getRegistSleep() > 0) {
                os.writeRegistInfo(item.getRegistSleep(), 3);
            }

            if (item.getRegistBlind() > 0) {
                os.writeRegistInfo(item.getRegistBlind(), 4);
            }

            if (getRegistStun() > 0) {
                os.writeRegistStun(getRegistStun());
            }

            if (item.getRegistSustAin() > 0) {
                os.writeRegistInfo(item.getRegistSustAin(), 6);
            }

            if (getIgnoreReduction() > 0) {
                os.writeAddMsg("대미지감소 무시 : +" + getIgnoreReduction());
            }

            if (getAddEr() > 0) {
                os.writeAddMsg("ER : +" + getAddEr());
            }

            if (getRegistElf() > 0) {
                os.writeAddMsg("정령 내성 : +" + getRegistElf());
            }

            if (getAddPotionPer() > 0) {
                os.writeAddMsg("물약회복률 : +" + (int) getAddPotionPer() + "%");
            }

            if (item.getSafeEnchant() >= 0) {
                os.writeSafeEnchant(item.getSafeEnchant());
            } else if (item.getSafeEnchant() == -1) {
                os.writeAddMsg("안전인챈 : 인챈불가");
            }

            if (getMagicPercent() > 0) {
                os.writeAddMagicChance(getMagicPercent() + "%");
            }

            if (itemType2 == 1) {
                if (getId() != 0) {
                    int dmg = L1WeaponUtils.getWeaponAttrDamage(attrEnchantLevel);
                    if (dmg > 0) {
                        os.writeAddAttrDamage(dmg);
                    }
                }
            }

            if (getWeightReduction() > 0) {
                os.writeAddWeightReduction(getWeightReduction());
            }

            if (getAddStunHit() > 0) {
                os.writeAddStunHit(getAddStunHit());
            }

            if (getBowCriticalPer() > 0) {
                os.writeAddMsg("원거리치명타 : +" + getBowCriticalPer() + "%");
            }

            if (getCriticalPer() > 0) {
                os.writeAddMsg("치명타 : +" + getCriticalPer() + "%");
            }

            if (item.isArmor()) {
                int optionGrade = getOptionGrade();

                if (optionGrade > 0)
                    os.writeAddMsg(L1Options.optionMsgArmor(optionGrade));
            } else if (item.isWeapon()) {
                int optionGrade = getOptionGrade();

                if (optionGrade > 0)
                    os.writeAddMsg(L1Options.optionMsgWeapon(optionGrade));
            }

            if (getNextReq() == 1) {
                os.writeAddMsg("이월 아이템");
            }

            if (getAddExp() > 0) {
                os.writeAddExp(getAddExp());
            }

            os.writeClass(item);
        }

        for (String msg : item.getStatusMsgs()) {
            os.writeAddMsg(msg);
        }

        List<String> customAddMsg = getCustomAddMsg();

        if (!customAddMsg.isEmpty()) {
            //os.writeAddMsg("불가:" + StringUtils.join(customAddMsg, ","));
        }

        return os.getBytes();
    }

    public List<String> getCustomAddMsg() {
        List<String> ableList = new ArrayList<>();

        if (!item.isTradeAble()) {
            ableList.add("교환");
        }

        if (!item.isDeleteAble()) {
            ableList.add("삭제");
        }

        if (!item.isWarehouse()) {
            ableList.add("창고");
        }

        return ableList;
    }

    public int getRegistElf() {
        int result = item.getRegistElf();

        EnchantBonus e = EnchantBonusTable.getInstance().getEnchantBonus(this);

        if (e != null) {
            result += e.getRegistElf();
        }

        return result;
    }

    public int getAddStr() {
        int result = item.getAddStr();

        EnchantBonus e = EnchantBonusTable.getInstance().getEnchantBonus(this);

        if (e != null) {
            result += e.getStr();
        }

        return result;
    }

    public int getAddInt() {
        int result = item.getAddInt();

        EnchantBonus e = EnchantBonusTable.getInstance().getEnchantBonus(this);

        if (e != null) {
            result += e.getIntel();
        }

        return result;
    }

    public int getAcByMagic() {
        return acByMagic;
    }

    public void setAcByMagic(int i) {
        acByMagic = i;
    }

    public int getDmgByMagic() {
        return dmgByMagic;
    }

    public void setDmgByMagic(int i) {
        dmgByMagic = i;
    }

    public int getHolyDmgByMagic() {
        return holyDmgByMagic;
    }

    public void setHolyDmgByMagic(int i) {
        holyDmgByMagic = i;
    }

    public int getHitByMagic() {
        return hitByMagic;
    }

    public void setHitByMagic(int i) {
        hitByMagic = i;
    }

    public void removeArmorEnchant(L1PcInstance pc) {
        if (pc == null)
            return;

        setSkillArmorEnchant(pc, 0);
    }

    public void setSkillArmorEnchant(L1PcInstance pc, int skillTime) {
        if (L1CommonUtils.isArmor(this)) {
            this.pc = pc;

            if (isMagicRunning()) {
                if (isEquipped()) {
                    pc.getAC().addAc(3);
                    pc.sendPackets(new S_OwnCharStatus(pc));
                    pc.sendPackets(new S_PacketBox(L1PacketBoxType.UNLIMITED_ICON1, getSkillIcon(), false));
                }

                setAcByMagic(0);
                setEnchantTime(0);
                setMagicRunning(false);
                setSkillIcon(0);

                ItemTimerScheduler.getInstance().removeEnchant(this);
            }

            if (skillTime > 0) {
                setSkillIcon(394);

                if (isEquipped()) {
                    pc.getAC().addAc(-3);
                    pc.sendPackets(new S_OwnCharStatus(pc));
                    pc.sendPackets(new S_PacketBox(L1PacketBoxType.UNLIMITED_ICON1, getSkillIcon(), true));
                }

                long time = skillTime + System.currentTimeMillis();
                setAcByMagic(3);
                setEnchantTime(time);
                setMagicRunning(true);
                ItemTimerScheduler.getInstance().addEnchant(this);

                logger.debug("[인챈트 아머 종료시간] : {}", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(time)));
            }
        }
    }

    public long getMagicRemainingTime() {
        return enchantTime - System.currentTimeMillis();
    }

    public void removeWeaponEnchant(L1PcInstance pc) {
        if (pc == null)
            return;

        setSkillWeaponEnchant(pc, 0, 0);
    }

    public void setSkillWeaponEnchant(L1PcInstance pc, int skillId, int skillTime) {
        if (item.getType2() != 1) {
            return;
        }

        logger.debug("skillId:{},skillTime:{}", skillId, skillTime);

        if (isMagicRunning()) {
            setDmgByMagic(0);
            setHolyDmgByMagic(0);
            setHitByMagic(0);
            setMagicRunning(false);

            pc.sendPackets(new S_PacketBox(L1PacketBoxType.UNLIMITED_ICON1, getSkillIcon(), false));
            setSkillIcon(0);

            ItemTimerScheduler.getInstance().removeEnchant(this);
        }

        if (skillTime > 0) {
            switch (skillId) {
                case L1SkillId.HOLY_WEAPON:
                    setSkillIcon(12);
                    setHolyDmgByMagic(1);
                    setHitByMagic(1);
                    break;
                case L1SkillId.ENCHANT_WEAPON:
                    setSkillIcon(379);
                    setDmgByMagic(2);
                    break;
                case L1SkillId.BLESS_WEAPON:
                    setSkillIcon(13);
                    setDmgByMagic(2);
                    setHitByMagic(2);
                    break;
                case L1SkillId.SHADOW_FANG:
                    setSkillIcon(392);
                    setDmgByMagic(5);
                    break;
                default:
                    break;
            }

            if (getSkillIcon() > 0) {
                if (isEquipped()) {
                    logger.debug("send limited:{}", getSkillIcon());
                    pc.sendPackets(new S_PacketBox(L1PacketBoxType.UNLIMITED_ICON1, getSkillIcon(), true));
                }
            }

            setPc(pc);
            setMagicRunning(true);

            long time = skillTime + System.currentTimeMillis();
            L1LogUtils.gmLog(pc, "인챈트웨폰 종료시간 : {}", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(time)));

            setEnchantTime(time);
            ItemTimerScheduler.getInstance().addEnchant(this);
        }
    }

    public void startItemOwnerTimer(L1PcInstance pc) {
        setItemOwner(pc);
        ownerTime = (1000 * 10) + System.currentTimeMillis();
        ItemTimerScheduler.getInstance().addOwner(this);
    }

    public void startEquipmentTimer(L1PcInstance pc) {
        if (getRemainingTime() > 0) {
            equipPc = pc;
            ItemTimerScheduler.getInstance().addEquip(this);
        }
    }

    public void stopEquipmentTimer() {
        if (getRemainingTime() > 0) {
            equipPc = null;
            ItemTimerScheduler.getInstance().removeEquip(this);
        }
    }

    public void cancelEquipmentTimer() {
        equipPc = null;
        ItemTimerScheduler.getInstance().removeEquip(this);
    }

    public L1PcInstance getItemOwner() {
        return owner;
    }

    public void setItemOwner(L1PcInstance pc) {
        owner = pc;
    }

    public boolean isNowLighting() {
        return isNowLighting;
    }

    public void setNowLighting(boolean flag) {
        isNowLighting = flag;
    }

    public int getSecondId() {
        return secondId;
    }

    public void setSecondId(int i) {
        secondId = i;
    }

    public int getRoundId() {
        return roundId;
    }

    public void setRoundId(int i) {
        roundId = i;
    }

    public int getTicketId() {
        return ticketId;
    }

    public void setTicketId(int i) {
        ticketId = i;
    }

    public int getDropMobId() {
        return dropMobId;
    }

    public void setDropMobId(int i) {
        dropMobId = i;
    }

    public boolean isWorking() {
        return isWorking;
    }

    public void setWorking(boolean flag) {
        isWorking = flag;
    }

    public L1Delay getItemDelay() {
        return itemDelay;
    }

    public int getSkillIcon() {
        return skillIcon;
    }

    public void setSkillIcon(int i) {
        skillIcon = i;
    }

    public int getAddDmgUpByArmor() {
        int result = 0;

        result += L1Ring.calcAddDmgUp(itemId, enchantLevel, bless);
        result += L1EarRing.calcAddDmgUp(itemId, enchantLevel, bless);

        EnchantBonus e = EnchantBonusTable.getInstance().getEnchantBonus(this);

        if (e != null) {
            result += e.getDmg();
        }

        return result;
    }

    public int getAddHitUpByArmor() {
        if (!item.isArmorAndRing()) {
            return 0;
        }

        int result = 0;

        result += L1Ring.calcAddHitUp(itemId, enchantLevel, bless);
        result += L1EarRing.calcAddHitUp(itemId, enchantLevel, bless);

        EnchantBonus e = EnchantBonusTable.getInstance().getEnchantBonus(this);

        if (e != null) {
            result += e.getHitUp();
        }

        return result;
    }

    public int getDmgUpByArmor() {
        if (!item.isArmorAndRing()) {
            return 0;
        }

        int result = item.getDmgUp();

        result += L1Acc.calcAddDmgUpAndBowDmgUp(this, enchantLevel);

        EnchantBonus e = EnchantBonusTable.getInstance().getEnchantBonus(this);

        if (e != null) {
            result += e.getAddDmg();
        }

        return result;
    }

    public int getBowDmgUpByArmor() {
        if (!item.isArmorAndRing()) {
            return 0;
        }

        int result = item.getBowDmgUp();

        result += L1Acc.calcAddDmgUpAndBowDmgUp(this, enchantLevel);

        EnchantBonus e = EnchantBonusTable.getInstance().getEnchantBonus(this);

        if (e != null) {
            result += e.getAddBowDmg();
        }

        return result;
    }

    public int getDamageReductionByArmor() {
        if (!item.isArmorAndRing()) {
            return 0;
        }

        int result = item.getDamageReduction();

        boolean isBless = (getBless() == 0 || getBless() >= 128);

        if (isBless && item.getGrade() != 10 && item.getGrade() != 50 && getItem().isArmor()) {
            result += CodeConfig.BLESS_ADD_REDUC;
        }

        result += L1EarRing.calcAddReduction(itemId, enchantLevel, bless);
        result += L1Acc.calcAddReduction(this, enchantLevel);

        EnchantBonus e = EnchantBonusTable.getInstance().getEnchantBonus(this);

        if (e != null) {
            result += e.getReduction();
        }

        return result;
    }

    public int getIgnoreReduction() {
        int result = item.getIgnoreReduction();

        EnchantBonus e = EnchantBonusTable.getInstance().getEnchantBonus(this);

        if (e != null) {
            result += e.getIgnoreReduction();
        }

        return result;
    }

    public int getHitUpByArmor() {
        if (!item.isArmorAndRing()) {
            return 0;
        }

        int result = item.getHitUp();

        EnchantBonus e = EnchantBonusTable.getInstance().getEnchantBonus(this);

        if (e != null) {
            result += e.getAddHitUp();
        }

        return result;
    }

    public int getBowHitUpByArmor() {
        int result = item.getBowHitUp();

        EnchantBonus e = EnchantBonusTable.getInstance().getEnchantBonus(this);

        if (e != null) {
            result += e.getAddBowHitUp();
        }

        return result;
    }

    public int getAddHp() {
        int result = item.getAddHp();

        result += L1Ring.calcAddHp(itemId, enchantLevel, bless);
        result += L1EarRing.calcAddHp(itemId, enchantLevel, bless);
        result += L1Acc.calcAddHp(this, enchantLevel);
        boolean isBless = getBless() == 0 || getBless() >= 128;

        if (isBless && getItem().isAccessorie()) {
            if (item.getGrade() == 10) {
                switch (item.getItemId()) {
                    case 20279:
                    case 420009:
                    case 420008:
                        result += CodeConfig.BLESS_ADD_HP;
                        break;
                }
            } else if (item.getGrade() != 50) {
                result += CodeConfig.BLESS_ADD_HP;
            }
        }

        EnchantBonus e = EnchantBonusTable.getInstance().getEnchantBonus(this);

        if (e != null) {
            result += e.getAddHp();
        }

        return result;
    }

    public int getAddMp() {
        int result = item.getAddMp();

        result += L1Ring.calcAddMp(itemId, enchantLevel, bless);
        result += L1EarRing.calcAddMp(itemId, enchantLevel, bless);
        result += L1Acc.calcAddMp(this, enchantLevel);

        EnchantBonus e = EnchantBonusTable.getInstance().getEnchantBonus(this);

        if (e != null) {
            result += e.getAddMp();
        }

        return result;
    }

    public int getAddSp() {
        int sp = item.getAddSp();

        int result = 0;

        if (item.isStaff()) {
            if (isBless()) {
                result += CodeConfig.BLESS_ADD_SP;
            }

            result += L1WeaponUtils.getWeaponAttrLevelGrade(attrEnchantLevel);
        }

        result += L1Ring.calcAddSp(itemId, enchantLevel, bless);
        result += L1EarRing.calcAddSp(itemId, enchantLevel, bless);
        result += L1Acc.calcAddSp(this, enchantLevel);

        EnchantBonus e = EnchantBonusTable.getInstance().getEnchantBonus(this);

        if (e != null) {
            result += e.getAddSp();
        }

        return sp + result;
    }

    public int getAddExp() {
        int result = 0;

        EnchantBonus e = EnchantBonusTable.getInstance().getEnchantBonus(this);

        if (e != null) {
            result += e.getAddExp();
        }

        return result;
    }

    public int getWeightReduction() {
        int result = item.getWeightReduction();
        EnchantBonus e = EnchantBonusTable.getInstance().getEnchantBonus(this);

        if (e != null) {
            result += e.getWeightReduction();
        }

        return result;
    }

    public int getAddMpr() {
        int result = item.getAddMpr();

        result += L1Ring.calcMpr(itemId, enchantLevel, bless);
        result += L1EarRing.calcMpr(itemId, enchantLevel, bless);

        EnchantBonus e = EnchantBonusTable.getInstance().getEnchantBonus(this);

        if (e != null) {
            result += e.getAddMpr();
        }

        return result;
    }

    public int getAddHpr() {
        int result = item.getAddHpr();

        result += L1Ring.calcHpr(itemId, enchantLevel, bless);
        result += L1EarRing.calcHpr(itemId, enchantLevel, bless);

        EnchantBonus e = EnchantBonusTable.getInstance().getEnchantBonus(this);

        if (e != null) {
            result += e.getAddHpr();
        }

        return result;
    }

    public int getHitUp() {
        return item.getHitUp();
    }

    public int getBowHitup() {
        return item.getBowHitUp();
    }

    public int getDmgUp() {
        return item.getDmgUp();
    }

    public int getBowDmgUp() {

        return item.getBowDmgUp();
    }

    public int getAddDmgUp() {
        int result = 0;

        boolean isBless = (getBless() == 0 || getBless() >= 128);

        if (item.isWeapon() && isBless && !item.isStaff()) {
            result += CodeConfig.BLESS_ADD_DMG;
        }

        return result;
    }

    public int getAc() {
        int result = item.getAc();

        int itemType = item.getType();

        if (itemType >= 8 && itemType <= 12) {
            result -= getAcByMagic();
        } else {
            result -= getEnchantLevel();
            result -= getAcByMagic();
        }

        result -= L1Ring.calcAc(itemId, enchantLevel, bless);
        result -= L1EarRing.calcAc(itemId, enchantLevel, bless);
        result -= L1Acc.calcAc(this, enchantLevel);

        EnchantBonus e = EnchantBonusTable.getInstance().getEnchantBonus(this);

        if (e != null) {
            result += -e.getAc();
        }

        return result;
    }

    public int getRegistStun() {
        int result = item.getRegistStun();

        result += L1Ring.calcRegistStun(itemId, enchantLevel, bless);
        result += L1Acc.calcRegistStun(this, enchantLevel);

        EnchantBonus e = EnchantBonusTable.getInstance().getEnchantBonus(this);

        if (e != null) {
            result += e.getRegistStun();
        }

        return result;
    }

    public int getAddMagicHitUp() {
        int result = item.getMagicHitUp();

        result += L1Ring.calcMagicHitUp(itemId, enchantLevel, bless);
        result += L1EarRing.calcAddMagicHitup(itemId, enchantLevel, bless);

        EnchantBonus e = EnchantBonusTable.getInstance().getEnchantBonus(this);

        if (e != null) {
            result += e.getMagicHit();
        }

        return result;
    }

    public int getPvpDamage() {
        int result = item.getPvpDamage();

        result += L1Ring.calcPvpDamage(itemId, enchantLevel, bless);
        result += L1Acc.calcPvpDamage(this, enchantLevel);

        EnchantBonus e = EnchantBonusTable.getInstance().getEnchantBonus(this);

        if (e != null) {
            result += e.getPvpDmg();
        }
        return result;
    }

    public int getPvpReduction() {
        int result = item.getPvpReduction();

        result += L1Acc.calcPvpReduction(this, enchantLevel);

        EnchantBonus e = EnchantBonusTable.getInstance().getEnchantBonus(this);

        if (e != null) {
            result += e.getPvpReduction();
        }
        return result;
    }

    public int getAddStunHit() {
        int result = item.getAddStun();

        EnchantBonus e = EnchantBonusTable.getInstance().getEnchantBonus(this);

        if (e != null) {
            result += e.getStunHitUp();
        }

        return result;
    }

    public int getAddCon() {
        int result = item.getAddCon();

        EnchantBonus e = EnchantBonusTable.getInstance().getEnchantBonus(this);

        if (e != null) {
            result += e.getCon();
        }

        return result;
    }

    public int getAddEr() {
        int result = item.getAddEr();

        EnchantBonus e = EnchantBonusTable.getInstance().getEnchantBonus(this);

        if (e != null) {
            result += e.getAddEr();
        }

        return result;
    }

    public int getAddDex() {
        int result = item.getAddDex();

        EnchantBonus e = EnchantBonusTable.getInstance().getEnchantBonus(this);

        if (e != null) {
            result += e.getDex();
        }

        return result;
    }

    public int getCriticalPer() {
        int result = item.getCriticalPer();

        EnchantBonus e = EnchantBonusTable.getInstance().getEnchantBonus(this);

        if (e != null) {
            result += e.getCriticalPer();
        }

        return result;
    }

    public int getBowCriticalPer() {
        int result = item.getBowCriticalPer();

        EnchantBonus e = EnchantBonusTable.getInstance().getEnchantBonus(this);

        if (e != null) {
            result += e.getBowCriticalPer();
        }

        return result;
    }

    public int getDoublePer() {
        int result = item.getDoubleDmgChance();

        EnchantBonus e = EnchantBonusTable.getInstance().getEnchantBonus(this);

        if (e != null) {
            result += e.getAddDouble();
        }


        return result;
    }

    public int getAddWis() {
        return item.getAddWis();
    }

    public boolean isMagicRunning() {
        return magicRunning;
    }

    public void setMagicRunning(boolean magicRunning) {
        this.magicRunning = magicRunning;
    }

    public L1PcInstance getEquipPc() {
        return equipPc;
    }

    public void setEquipPc(L1PcInstance equipPc) {
        this.equipPc = equipPc;
    }

    public L1PcInstance getPc() {
        return pc;
    }

    public void setPc(L1PcInstance pc) {
        this.pc = pc;
    }

    public long getOwnerTime() {
        return ownerTime;
    }

    public void setOwnerTime(long ownerTime) {
        this.ownerTime = ownerTime;
    }

    public long getEnchantTime() {
        return enchantTime;
    }

    public void setEnchantTime(long enchantTime) {
        this.enchantTime = enchantTime;
    }

    public int getKeyId() {
        return keyId;
    }

    public void setKeyId(int keyId) {
        this.keyId = keyId;
    }

    public int getNextReq() {
        return nextReq;
    }

    public void setNextReq(int nextReq) {
        this.nextReq = nextReq;
    }

    public int getMagicPercent() {
        return getMagicPercent(getItemId(), getEnchantLevel());
    }

    private int getMagicPercent(int itemId, int enchantLevel) {
        ItemSkill v = ItemSkillTable.getInstance().find(item.getItemId());

        if (v != null) {
            int prob = v.getProbability();

            if (enchantLevel >= v.getEnchantProbabilityStart()) {
                prob += ((enchantLevel + 1) - v.getEnchantProbabilityStart()) * v.getEnchantProbability();
            }

            return prob;
        }

        switch (itemId) {
            case 205:
                return enchantLevel;
            case 54:
            case 58:
            case 58000:
            case 76:
            case 415010:
            case 415011:
            case 415012:
            case 415013:
                return (enchantLevel * 2);
            case 121:
            case 124:
                return (enchantLevel * 2) + 15;
        }

        if (L1CommonUtils.isDragonArmor(itemId)) {
            int result = enchantLevel * 3;

            if (enchantLevel == 6) {
                result -= 3;
            }

            return result;
        }

        return 0;
    }

    public boolean isBow() {
        return item.getType1() == TYPE1_WEAPON_TYPE_SINGLE_BOW || item.getType1() == TYPE1_WEAPON_TYPE_BOW;
    }

    public boolean isGuntlet() {
        return item.getType1() == TYPE1_WEAPON_TYPE_GAUNTLET;
    }
}
