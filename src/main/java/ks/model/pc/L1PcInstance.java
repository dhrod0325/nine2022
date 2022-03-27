package ks.model.pc;

import ks.app.LineageAppContext;
import ks.app.config.prop.CodeConfig;
import ks.app.config.prop.ServerConfig;
import ks.app.event.L1QuitGameEvent;
import ks.commands.common.CommonCommands;
import ks.commands.gm.GmCommands;
import ks.commands.gm.command.executor.L1HpBar;
import ks.constants.*;
import ks.core.datatables.GetBackRestartTable;
import ks.core.datatables.MapsTable;
import ks.core.datatables.SkillsTable;
import ks.core.datatables.account.Account;
import ks.core.datatables.account.AccountTable;
import ks.core.datatables.buff.CharBuff;
import ks.core.datatables.buff.CharBuffTable;
import ks.core.datatables.clan.ClanTable;
import ks.core.datatables.exclude.CharacterExclude;
import ks.core.datatables.exclude.CharacterExcludeTable;
import ks.core.datatables.exp.ExpTable;
import ks.core.datatables.favPoly.FavPoly;
import ks.core.datatables.favPoly.FavPolyTable;
import ks.core.datatables.getback.GetBackTable;
import ks.core.datatables.pc.CharacterTable;
import ks.core.datatables.pet.PetTable;
import ks.core.datatables.polyImg.PolyImgTable;
import ks.core.network.L1Client;
import ks.model.*;
import ks.model.attack.physics.L1AttackRun;
import ks.model.attack.utils.L1MagicUtils;
import ks.model.bookMark.L1BookMarkTable;
import ks.model.cooking.L1CookingUtils;
import ks.model.doll.L1DollCombine;
import ks.model.instance.*;
import ks.model.instance.extend.DrainHpMpAble;
import ks.model.map.L1Map;
import ks.model.noDelay.NoDelayCheck;
import ks.model.pc.buff.ClanRankBuff;
import ks.model.pc.buff.RankBuff;
import ks.model.pc.hackCheck.pierce.L1PierceCheck;
import ks.model.pc.hackCheck.speedHack.L1AcceleratorCheck;
import ks.model.pc.scheduler.L1AutoCheckScheduler;
import ks.model.pc.timeDungeon.L1TimeDungeon;
import ks.model.skill.L1SkillUse;
import ks.model.skill.utils.L1SkillUtils;
import ks.model.warehouse.ClanWarehouse;
import ks.model.warehouse.WarehouseManager;
import ks.packets.clientpackets.C_MailBox;
import ks.packets.serverpackets.*;
import ks.scheduler.*;
import ks.scheduler.timer.gametime.GameTimeScheduler;
import ks.system.autoPotion.L1AutoPotion;
import ks.system.grangKin.GrangKainTable;
import ks.system.huntCheck.vo.Hunt;
import ks.util.*;
import ks.util.common.SqlUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

import static ks.constants.L1SkillIcon.채금;
import static ks.constants.L1SkillId.*;

public class L1PcInstance extends L1Character implements DrainHpMpAble {
    private static final Logger logger = LogManager.getLogger();

    public static final int REGEN_STATE_ATTACK = 1;
    public static final int REGEN_STATE_MOVE = 2;

    private final L1SearchShopItem searchShopItem = new L1SearchShopItem(this);
    private final L1TimeDungeon timeDungeon = new L1TimeDungeon(this);
    private final List<String> cmaList = new ArrayList<>();
    private final L1Karma karma = new L1Karma();
    private final L1PcInventory inventory = new L1PcInventory(this);
    private final L1Inventory tradeWindow = new L1Inventory();
    private final L1Ment ment = new L1Ment();

    private final L1Pagination pagination = new L1Pagination();
    private final NoDelayCheck noDelayCheck = new NoDelayCheck();
    private final List<L1PrivateShopSell> sellList = new ArrayList<>();
    private final List<L1PrivateShopBuy> buyList = new ArrayList<>();
    private final Map<String, String> dataMap = new HashMap<>();
    private final Map<Integer, L1NpcInstance> petList = new ConcurrentHashMap<>();
    private final Map<Integer, L1DollInstance> dollList = new ConcurrentHashMap<>();
    private final Map<Integer, L1FollowerInstance> followerList = new HashMap<>();
    private final L1PierceCheck pierceCheck = new L1PierceCheck(this);
    private final L1ExcludingList excludingList = new L1ExcludingList();
    private final L1AcceleratorCheck acceleratorChecker = new L1AcceleratorCheck(this);
    private final List<Integer> skillList = new ArrayList<>();
    private final L1EquipmentSlot equipSlot = new L1EquipmentSlot(this);
    private final L1Quest quest = new L1Quest(this);
    private final L1PcSpeedHackCheck speedHack = new L1PcSpeedHackCheck();
    private final L1PcBookMark bookMark = new L1PcBookMark();
    private final L1AutoPotion autoPotion = new L1AutoPotion(this);
    private final L1PcExpManager pcExpManager = new L1PcExpManager(this);
    private final L1PcHpMpRegen hpMpRegen = new L1PcHpMpRegen(this);
    private final L1DragonArmorChange dragonArmorChange = new L1DragonArmorChange(this);
    private final L1DollCombine dollCombine = new L1DollCombine(this);
    private final L1AutoCheckScheduler autoCheckScheduler = new L1AutoCheckScheduler(this);
    private final L1PcStateMap stateMap = new L1PcStateMap();
    private final L1ChatCheck chatCheck = new L1ChatCheck(this);
    private final List<Integer> masterPolyIdList = new ArrayList<>();
    private final List<FavPoly> favPolyList = new ArrayList<>(3);

    public boolean gmCommandClanMark = false;

    public int lawfulSP = 0;
    public int lawfulAttack = 0;
    public String autoKingBuffState;
    int oldRank = 0;

    private final Map<String, Object> etcMap = new HashMap<>();

    private int expBonus;
    private L1Client client;
    private int currentDollId = 0;
    private boolean hpRegenByDoll = false;
    private boolean mpRegenByDoll = false;
    private boolean usingDoll;
    private String lastChat;
    private int drainHp;
    private int drainMp;
    private boolean tripleAction = false;
    private boolean autoHunt = false;
    private String accountName;
    private int classId;
    private int type;
    private int exp;
    private int age;
    private int markCount;
    private int battleKillCount = 0;
    private int battleDeathCount = 0;
    private short accessLevel = 0;
    private boolean world;
    private boolean dmgScarecrow = false;
    private short baseMaxHp = 0;
    private short baseMaxMp = 0;
    private int hitUpByArmor = 0; // 방어용 기구에 의한 근접무기 명중율
    private int bowHitUpByArmor = 0; // 방어용 기구에 의한 활의 명중율
    private int dmgUpByArmor = 0; // 방어용 기구에 의한 근접무기 추타율
    private int bowDmgUpByArmor = 0; // 방어용 기구에 의한 활의 추타율
    private int addDmgUpByArmor = 0;
    private int addPvpDmgUp = 0;
    private int pkCount;
    private int fishingX = 0;
    private int fishingY = 0;
    private int clanId;
    private String clanName;
    private int clanRank;
    private byte sex;
    private int returnStat;
    private short hpr = 0;
    private short trueHpr = 0;
    private short mpr = 0;
    private short trueMpr = 0;
    private int advenHp;
    private int advenMp;
    private int highLevel;
    private boolean showTradeChat = true;
    private boolean canWhisper = true;
    private boolean fishing = false;
    private boolean showWorldChat = true;
    private boolean gm;
    private boolean gmInvis;
    private boolean teleport = false;
    private boolean gres = false;
    private boolean pinkName = false;
    private boolean banned;
    private boolean gresValid;
    private boolean tradeOk;
    private int nbapoLevel;
    private int obapoLevel;
    private int bapodmg;
    private int invisDelayCounter = 0;
    private Timestamp lastPk;
    private Timestamp deleteTime;
    private int weightReduction = 0;
    private int hasteItemEquipped = 0;
    private int damageReductionByArmor = 0;
    private int teleportY = 0;
    private int teleportX = 0;
    private short teleportMapId = 0;
    private int teleportHeading = 0;
    private int tempCharGfxAtDead;
    private int fightId;
    private int contribution;
    private int elfAttr;
    private int homeTownId;
    private int expRes;
    private int onlineStatus;
    private int food;
    private int hellTime;
    private int partnerId;
    private int monsterKill = 0;
    private int dessertId = 0;
    private int callClanId;
    private int callClanHeading;
    private int currentWeapon = 0;
    private int cookingId = 0;
    private int partyID;
    private int tradeID;
    private int tempID;
    private int birthday = 0;
    private boolean tradingInPrivateShop = false;
    private int partnersPrivateShopItemCount = 0;
    private boolean statReturnCheck = false;
    private int hpcurPoint = 4;
    private int mpcurPoint = 4;
    private int ainState = 0;
    private long dollHPRegenTime = 0;
    private long dollMPRegenTime = 0;
    private long invisDelayTime = 0;
    private int dragonPerlSpeed;
    private boolean markShow;
    private int lastSaveSlot;
    private L1Party party;
    private L1ChatParty chatParty;
    private boolean autoDragonDiamond;
    private boolean autoDragonPerl;
    private boolean castleIn = false;
    private String sealingPW;
    private int specialSize; // 특수창고
    private L1ParalysisStatus paralysisStatus = new L1ParalysisStatus(0, false);
    private boolean isAutoKingBuff = false;
    private int uhodoPercent = 0;
    private int dg = 0;
    private int huntCount;
    private int huntPrice;
    private String reasonTohunt;
    private int addMagicHitUp;
    private boolean summonMonster = false;
    private boolean shapeChange = false;
    private boolean archShapeChange = false;
    private boolean archPolyType = true;
    private int elfGrave;
    private int ivoryTimer;
    private int dreamTimer;
    private boolean desShapeChange = false;
    private Timestamp logoutTime;
    private int chaTra;
    private int ainhasad;
    private int enchantItemId = 0;
    private final Map<Integer, Hunt> huntCheckMap = new HashMap<>();

    private ScheduledFuture<?> autoUpdateFuture;
    private List<L1Drop> itemDropSearchList = new ArrayList<>();
    private int addHitUpByArmor;
    private L1DamageCheck damageCheck = new L1DamageCheck();
    private int addStunHit;
    private double addPotionPer;
    private int addPvpReduction;
    private int addEr;
    private boolean equipServerRune;

    private int criticalPer;
    private int bowCriticalPer;
    private int magicCriticalPer;

    private final Attack attack = new Attack(this);
    private int trueTargetLeaderId;

    public Map<Integer, Hunt> getHuntCheckMap() {
        return huntCheckMap;
    }

    public Attack getAttack() {
        return attack;
    }

    public static L1PcInstance load(String charName) {
        return CharacterTable.getInstance().loadCharacter(charName);
    }

    public L1SearchShopItem getSearchShopItem() {
        return searchShopItem;
    }

    public L1Pagination getPagination() {
        return pagination;
    }

    public Map<String, String> getDataMap() {
        return dataMap;
    }

    public L1PierceCheck getPierceCheck() {
        return pierceCheck;
    }

    public int getLastSaveSlot() {
        return lastSaveSlot;
    }

    public void setLastSaveSlot(int lastSaveSlot) {
        this.lastSaveSlot = lastSaveSlot;
    }

    public boolean isAutoHunt() {
        return autoHunt;
    }

    public void setAutoHunt(boolean autoHunt) {
        this.autoHunt = autoHunt;
    }

    public boolean isAutoDragonPerl() {
        return autoDragonPerl;
    }

    public void setAutoDragonPerl(boolean autoDragonPerl) {
        this.autoDragonPerl = autoDragonPerl;
    }

    public boolean isAutoDragonDiamond() {
        return autoDragonDiamond;
    }

    public void setAutoDragonDiamond(boolean autoDragonDiamond) {
        this.autoDragonDiamond = autoDragonDiamond;
    }

    public boolean isWorld() {
        return world;
    }

    public void setWorld(boolean world) {
        this.world = world;
    }

    public String getLastChat() {
        return lastChat;
    }

    public void setLastChat(String lastChat) {
        this.lastChat = lastChat;
    }

    public int getMarkCount() {
        return markCount;
    }

    public void setMarkCount(int i) {
        markCount = i;
    }

    public boolean getScarecrow() {
        return dmgScarecrow;
    }

    public void setScarecrow(boolean C) {
        dmgScarecrow = C;
    }

    public void setBapodmg(int i) {
        bapodmg = i;
    }

    public int getNBapoLevel() {
        return nbapoLevel;
    }

    public void setNBapoLevel(int i) {
        nbapoLevel = i;
    }

    public int getOBapoLevel() {
        return obapoLevel;
    }

    public void setOBapoLevel(int i) {
        obapoLevel = i;
    }

    public int getMonsterKill() {
        return monsterKill;
    }

    public void setMonsterKill(int i) {
        monsterKill = i;
        sendPackets(new S_OwnCharStatus(this));
    }

    public L1PcStateMap getStateMap() {
        return stateMap;
    }

    public NoDelayCheck getNoDelayCheck() {
        return noDelayCheck;
    }

    public int getBirthDay() {
        return birthday;
    }

    public void setBirthDay(int time) {
        birthday = time;
    }

    public void setSkillMastery(int skillid) {
        if (!skillList.contains(skillid)) {
            skillList.add(skillid);
        }
    }

    public void removeSkillMastery(int skillId) {
        try {
            skillList.removeIf(integer -> integer == skillId);
        } catch (Exception e) {
            logger.error("오류", e);
        }
    }

    public boolean isSkillMastery(int skillId) {
        return skillList.contains(skillId);
    }

    public void clearSkillMastery() {
        skillList.clear();
    }

    public short getHpr() {
        return hpr;
    }

    public void addHpr(int i) {
        trueHpr += i;
        hpr = (short) Math.max(0, trueHpr);
    }

    public short getMpr() {
        return mpr;
    }

    public void addMpr(int i) {
        trueMpr += i;
        mpr = (short) Math.max(0, trueMpr);
    }

    public void startHpRegenerationByDoll() {
        if (!isUsingDoll())
            return;

        if (!hpRegenByDoll) {
            hpRegenByDoll = true;

            setDollHpRegenTime((getCurrentDoll().getAbHprTime() * 1000L) + System.currentTimeMillis());
            DollHpMpRegenScheduler.getInstance().registerHpRegen(this);
        }
    }

    public void startMpRegenerationByDoll() {
        if (!isUsingDoll())
            return;

        if (!mpRegenByDoll) {
            mpRegenByDoll = true;

            setDollMpRegenTime((getCurrentDoll().getAbMprTime() * 1000L) + System.currentTimeMillis());
            DollHpMpRegenScheduler.getInstance().registerMpRegen(this);
        }
    }

    public void stopHpRegenerationByDoll() {
        if (hpRegenByDoll) {
            hpRegenByDoll = false;
            DollHpMpRegenScheduler.getInstance().removeHp(this);
        }
    }

    public void stopMpRegenerationByDoll() {
        if (mpRegenByDoll) {
            mpRegenByDoll = false;
            DollHpMpRegenScheduler.getInstance().removeMp(this);
        }
    }

    public void stopEtcMonitor() {
    }

    public void stopEquipmentTimer() {
        List<L1ItemInstance> allItems = this.getInventory().getItems();
        for (L1ItemInstance item : allItems) {
            if (item == null)
                continue;
            if (item.isEquipped() && item.getRemainingTime() > 0) {
                item.stopEquipmentTimer();
            }
        }
    }

    public void checkChangeExp() {
        int level = ExpTable.getInstance().getLevelByExp(getExp());
        int charLevel = CharacterTable.getInstance().selectPcLevel(getId());

        if (charLevel == 0) {
            return;
        }

        int gap = level - charLevel;

        if (gap == 0) {
            sendPackets(new S_Exp(this));
            return;
        }

        if (gap > 0) {
            getPcExpManager().levelUp(gap);
        } else {
            getPcExpManager().levelDown(gap);
        }
    }

    public void onChangeExp() {
        int level = ExpTable.getInstance().getLevelByExp(getExp());
        int charLevel = getLevel();
        int gap = level - charLevel;

        if (gap == 0) {
            sendPackets(new S_Exp(this));
            return;
        }

        if (gap > 0) {
            getPcExpManager().levelUp(gap);
        } else {
            getPcExpManager().levelDown(gap);
        }
    }

    public String getHuntName() {
        String name = getName();

        if (huntCount > 0) {
            return "[수배]" + name;
        } else {
            return name;
        }
    }

    @Override
    public void onPerceive(L1PcInstance pc) {
        pc.getNearObjects().addKnownObject(this);
        pc.sendPackets(new S_OtherCharPacks(this));

        if (isPinkName() || pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.USERSTATUS_ATTACK)) {
            pc.sendPackets(new S_PinkName(getId(), getSkillEffectTimerSet().getSkillEffectTimeSec(L1SkillId.STATUS_PINK_NAME)));
        }

        if (isInParty() && getParty().isMember(pc)) {
            pc.sendPackets(new S_HPMeter(this));
        }

        if (isFishing()) {
            pc.sendPackets(new S_Fishing(getId(), L1ActionCodes.ACTION_Fishing, fishingX, fishingY));
        }
    }

    public void updateObject() {
        List<L1Object> knownObjects = getNearObjects().getKnownObjects();

        for (L1Object known : knownObjects) {
            if (known == null) {
                continue;
            }

            if (known.getMapId() == L1Map.MAP_2D && !isGm()) {
                if (known instanceof L1PcInstance) {
                    continue;
                }
            }

            if (CodeConfig.PC_RECOGNIZE_RANGE == -1) {
                if (!getLocation().isInScreen(known.getLocation())) {
                    getNearObjects().removeKnownObject(known);
                    sendPackets(new S_RemoveObject(known));
                }
            } else {
                if (getLocation().getTileLineDistance(known.getLocation()) > CodeConfig.PC_RECOGNIZE_RANGE) {
                    getNearObjects().removeKnownObject(known);
                    sendPackets(new S_RemoveObject(known));
                }
            }
        }

        List<L1Object> visibleObjects = L1World.getInstance().getVisibleObjects(this, CodeConfig.PC_RECOGNIZE_RANGE);

        for (L1Object visible : visibleObjects) {
            if (visible == null) {
                return;
            }

            if (!getNearObjects().knownsObject(visible)) {
                if (visible.getMapId() == L1Map.MAP_2D) {
                    if (visible instanceof L1PcInstance) {
                        continue;
                    }
                }

                visible.onPerceive(this);
            } else {
                if (visible instanceof L1NpcInstance) {
                    L1NpcInstance npc = (L1NpcInstance) visible;

                    if (getLocation().isInScreen(npc.getLocation()) && npc.getHiddenStatus() != 0) {
                        npc.approachPlayer(this);
                    }
                }
            }

            if (visible instanceof L1PcInstance) {
                if ("true".equals(getDataMap().get(L1DataMapKey.PC_ATTACK))) {
                    sendPackets(new S_PinkName(visible.getId(), 30));
                }
            }

            if (getSkillEffectTimerSet().hasSkillEffect(L1SkillId.STATUS_GM_HPBAR) && L1HpBar.isHpBarTarget(visible)) {
                L1Character character = (L1Character) visible;
                sendPackets(new S_HPMeter(character));
            }
        }
    }

    public void sendVisualEffect() {
        if (getPoison() != null) {
            L1CommonUtils.sendPoisonStatus(this, getPoison().getEffectId());
        }

        if (getParalysis() != null) {
            L1CommonUtils.sendPoisonStatus(this, getParalysis().getEffectId());
        }
    }

    public void sendCastleMaster() {
        if (!isCrown())
            return;

        if (getClanId() != 0) {
            L1Clan clan = ClanTable.getInstance().getTemplate(getClanId());

            if (clan != null) {
                if (getId() == clan.getLeaderId()) {
                    if (clan.getCastleId() != 0) {
                        L1World.getInstance().broadcastPacketToAll(new S_CastleMaster(clan.getCastleId(), getId()));
                    }
                }
            }
        }
    }

    @Override
    public void setCurrentHp(int i) {
        if (getCurrentHp() == i) return;

        super.setCurrentHp(i);

        sendPackets(new S_HPUpdate(getCurrentHp(), getMaxHp()));

        if (isInParty()) {
            getParty().updateMiniHP(this);
        }
    }

    @Override
    public void setCurrentMp(int i) {
        if (getCurrentMp() == i)
            return;

        super.setCurrentMp(i);

        sendPackets(new S_MPUpdate(getCurrentMp(), getMaxMp()));
    }

    @Override
    public L1PcInventory getInventory() {
        return inventory;
    }

    public L1Inventory getTradeWindowInventory() {
        return tradeWindow;
    }

    public boolean isGmInvis() {
        return gmInvis;
    }

    public void setGmInvis(boolean flag) {
        gmInvis = flag;
    }

    public int getCurrentWeapon() {
        return currentWeapon;
    }

    public void setCurrentWeapon(int i) {
        currentWeapon = i;
    }

    public int getType() {
        return type;
    }

    public void setType(int i) {
        type = i;
    }

    public short getAccessLevel() {
        return accessLevel;
    }

    public void setAccessLevel(short i) {
        accessLevel = i;
    }

    public int getClassId() {
        return classId;
    }

    public void setClassId(int i) {
        classId = i;
    }

    public boolean getCastleIn() {
        return castleIn;
    }

    public void setCastleIn(boolean fiag) {
        castleIn = fiag;
    }

    @Override
    public synchronized int getExp() {
        return exp;
    }

    @Override
    public synchronized void setExp(int i) {
        exp = i;
    }

    public synchronized int getReturnStat() {
        return returnStat;
    }

    public synchronized void setReturnStat(int i) {
        returnStat = i;
    }

    public String getSealingPW() {
        return sealingPW;
    }

    public void setSealingPW(String s) {
        sealingPW = s;
    }

    private void notifyPlayersLogout(List<L1PcInstance> characters) {
        for (L1PcInstance player : characters) {
            if (player == null)
                continue;

            if (player.getNearObjects().knownsObject(this)) {
                player.getNearObjects().removeKnownObject(this);
                player.sendPackets(new S_RemoveObject(this));
            }
        }
    }

    private void notifyMonstersLogout(List<L1MonsterInstance> monsters) {
        for (L1MonsterInstance monster : monsters) {
            if (monster == null)
                continue;

            for (L1Character target : monster.getHateList().toTargetList()) {
                monster.targetRemove(target);
            }
        }
    }

    public void logout() {
        if (getAccount() != null) {
            AccountTable.getInstance().updateLastLogOut(getAccount());
            GrangKainTable.getInstance().updateTime(getAccount());
        }

        L1World world = L1World.getInstance();

        notifyPlayersLogout(getNearObjects().getKnownPlayers());
        notifyMonstersLogout(getNearObjects().getKnownMonsters());

        world.removeVisibleObject(this);
        world.removeObject(this);

        notifyPlayersLogout(world.getRecognizePlayer(this));

        inventory.clearItems();

        WarehouseManager w = WarehouseManager.getInstance();
        w.delPrivateWarehouse(accountName);
        w.delElfWarehouse(accountName);
        w.delExtraWarehouse(accountName);

        getNearObjects().removeAllKnownObjects();

        autoCheckScheduler.stop();
        stopHpRegenerationByDoll();
        stopMpRegenerationByDoll();
        stopEquipmentTimer();
        stopEtcMonitor();
        stopAutoUpdate();

        if (isCrown()) {
            BraveAvatarScheduler.getInstance().removeCrown(this);
        }
    }

    public L1Client getClient() {
        return client;
    }

    public void setClient(L1Client client) {
        this.client = client;
    }

    public boolean isInParty() {
        return getParty() != null;
    }

    public L1Party getParty() {
        return party;
    }

    public void setParty(L1Party p) {
        party = p;
    }

    public boolean isInChatParty() {
        return getChatParty() != null;
    }

    public L1ChatParty getChatParty() {
        return chatParty;
    }

    public void setChatParty(L1ChatParty cp) {
        chatParty = cp;
    }

    public int getPartyID() {
        return partyID;
    }

    public void setPartyID(int partyID) {
        this.partyID = partyID;
    }

    public int getTradeID() {
        return tradeID;
    }

    public void setTradeID(int tradeID) {
        this.tradeID = tradeID;
    }

    public boolean getTradeOk() {
        return tradeOk;
    }

    public void setTradeOk(boolean tradeOk) {
        this.tradeOk = tradeOk;
    }

    public int getTempID() {
        return tempID;
    }

    public void setTempID(int tempID) {
        this.tempID = tempID;
    }

    public boolean isTeleport() {
        return teleport;
    }

    public void setTeleport(boolean flag) {
        teleport = flag;
    }

    public boolean isGres() {
        return gres;
    }

    public void setGres(boolean flag) {
        gres = flag;
    }

    public boolean isPinkName() {
        return pinkName;
    }

    public void setPinkName(boolean flag) {
        pinkName = flag;
    }

    public List<L1PrivateShopSell> getSellList() {
        return sellList;
    }

    public List<L1PrivateShopBuy> getBuyList() {
        return buyList;
    }

    public int getSpecialSize() {
        return specialSize;
    } // 특수창고

    public void set_SpecialSize(int special_size) {
        specialSize = special_size;
    } // 특수창고

    public boolean isTradingInPrivateShop() {
        return tradingInPrivateShop;
    }

    public void setTradingInPrivateShop(boolean flag) {
        tradingInPrivateShop = flag;
    }

    public int getPartnersPrivateShopItemCount() {
        return partnersPrivateShopItemCount;
    }

    public void setPartnersPrivateShopItemCount(int i) {
        partnersPrivateShopItemCount = i;
    }

    public void sendGreenMessage(String msg) {
        sendPackets(new S_PacketBox(L1PacketBoxType.GREEN_MESSAGE, "[" + ServerConfig.SERVER_NAME + "] " + msg));
    }

    public void sendGreenMessageAndSystemMessage(String msg) {
        sendGreenMessage(msg);
        sendPackets(msg);
    }

    public void sendPackets(String s) {
        sendPackets(new S_SystemMessage(s));
    }

    public void sendPackets(ServerBasePacket sb) {
        sendPackets(sb, true);
    }

    public void sendPackets(ServerBasePacket sb, boolean clear) {
        if (client == null) {
            return;
        }

        try {
            if (sb instanceof S_Paralysis) {
                setParalysisStatus(new L1ParalysisStatus(((S_Paralysis) sb).getParalysisType(), ((S_Paralysis) sb).isParalysisFlag()));
            }

            client.sendPacket(sb, clear);
        } catch (Exception e) {
            logger.error(e);
        }
    }

    public L1ParalysisStatus getParalysisStatus() {
        return paralysisStatus;
    }

    public void setParalysisStatus(L1ParalysisStatus paralysisStatus) {
        this.paralysisStatus = paralysisStatus;
    }

    public boolean isAutoKingBuff() {
        return isAutoKingBuff;
    }

    public void setAutoKingBuff(boolean autoKingBuff) {
        isAutoKingBuff = autoKingBuff;
    }

    public void setAutoKingBuffState(String autoKingBuffState) {
        this.autoKingBuffState = autoKingBuffState;
    }

    @Override
    public void onAction(L1PcInstance attacker) {
        if (attacker == null) {
            return;
        }

        if (isTeleport()) {
            return;
        }

        if (isDead()) {
            return;
        }

        if (isCrown() && isAutoKingBuff()) {
            if (!getTimer().isTimeOver("kingBuff")) {
                attacker.sendPackets((getTimer().remainingMillis("kingBuff") / 1000 + 1) + "초 후에 군업을 사용할 수 있습니다");
                L1AttackRun motion = new L1AttackRun(attacker, this);
                motion.action();
                return;
            }

            getTimer().setWaitTime("kingBuff", 1000 * 10);

            if (!L1HouseLocation.isInHouse(getX(), getY(), getMapId()) && !L1CastleLocation.isInCastleInner(getMapId())) {
                sendPackets("자동군업은 아지트 내부에서만 사용할 수 있습니다");
                L1AttackRun motion = new L1AttackRun(attacker, this);
                motion.action();
                return;
            }

            if (attacker.isInParty()) {
                if (!attacker.getParty().isMember(this)) {
                    if (attacker.getParty().isVacancy()) {
                        attacker.getParty().addMember(this);
                    } else {
                        sendPackets(new S_ServerMessage(417));
                        return;
                    }
                }
            } else {
                L1Party party = new L1Party();
                party.addMember(attacker);
                party.addMember(this);
                attacker.sendPackets(new S_ServerMessage(424, getName())); // %0가
            }

            if ("마방".equalsIgnoreCase(autoKingBuffState)) {
                if (SkillsTable.getInstance().spellCheck(getId(), GLOWING_WEAPON)) {
                    L1Skills t = SkillsTable.getInstance().getTemplate(GLOWING_WEAPON);
                    L1SkillUse l1skilluse = new L1SkillUse(this, GLOWING_WEAPON, getId(), getX(), getY(), t.getBuffDuration());
                    l1skilluse.run();
                }
                if (SkillsTable.getInstance().spellCheck(getId(), BRAVE_MENTAL)) {
                    L1Skills t = SkillsTable.getInstance().getTemplate(BRAVE_MENTAL);
                    L1SkillUse l1skilluse = new L1SkillUse(this, BRAVE_MENTAL, getId(), getX(), getY(), t.getBuffDuration());
                    l1skilluse.run();
                }
            } else if ("물방".equalsIgnoreCase(autoKingBuffState)) {
                if (SkillsTable.getInstance().spellCheck(getId(), SHINING_SHILELD)) {
                    L1Skills t = SkillsTable.getInstance().getTemplate(SHINING_SHILELD);
                    L1SkillUse l1skilluse = new L1SkillUse(this, SHINING_SHILELD, getId(), getX(), getY(), t.getBuffDuration());
                    l1skilluse.run();
                }
                if (SkillsTable.getInstance().spellCheck(getId(), BRAVE_MENTAL)) {
                    L1Skills t = SkillsTable.getInstance().getTemplate(BRAVE_MENTAL);
                    L1SkillUse l1skilluse = new L1SkillUse(this, BRAVE_MENTAL, getId(), getX(), getY(), t.getBuffDuration());
                    l1skilluse.run();
                }
            } else if ("브레이브".equalsIgnoreCase(autoKingBuffState)) {
                if (SkillsTable.getInstance().spellCheck(getId(), BRAVE_MENTAL)) {
                    L1Skills t = SkillsTable.getInstance().getTemplate(BRAVE_MENTAL);
                    L1SkillUse l1skilluse = new L1SkillUse(this, BRAVE_MENTAL, getId(), getX(), getY(), t.getBuffDuration());

                    l1skilluse.run();
                }
            }

            getParty().leaveMember(this);

            L1AttackRun motion = new L1AttackRun(attacker, this);
            motion.action();

            return;
        }

        if (L1CharPosUtils.isSafeZone(this) || L1CharPosUtils.isSafeZone(attacker)) {
            L1AttackRun motion = new L1AttackRun(attacker, this);
            motion.action();
            return;
        }

        if (checkNonPvP()) {
            L1AttackRun motion = new L1AttackRun(attacker, this);
            motion.action();
            return;
        }

        if (getCurrentHp() > 0 && !isDead()) {
            attacker.delInvis();

            L1AttackRun attack = new L1AttackRun(attacker, this);
            attack.action();
            attack.commit();
        }
    }

    public boolean checkNonPvP() {
        return false;
    }

    public void setPetTarget(L1Character target) {
        Collection<L1NpcInstance> petList = getPetList().values();

        for (L1NpcInstance pet : petList) {
            if (pet == null)
                continue;
            if (pet instanceof L1PetInstance) {
                L1PetInstance pets = (L1PetInstance) pet;
                pets.setMasterTarget(target);
            } else if (pet instanceof L1SummonInstance) {
                L1SummonInstance summon = (L1SummonInstance) pet;
                summon.setMasterTarget(target);
            }
        }
    }

    public boolean isGiranVillage() {
        return L1CommonUtils.isGiranVillage(getX(), getY(), getMapId());
    }

    public void delInvis() {
        if (!isGmInvis()) {
            L1MagicUtils.stopInvisible(this);
        }
    }

    @Override
    public void receiveManaDamage(L1Character attacker, int mpDamage) {
        if (mpDamage > 0 && !isDead()) {
            delInvis();

            if (attacker instanceof L1PcInstance) {
                L1PinkName.onAction(this, attacker);
            }

            int newMp = getCurrentMp() - mpDamage;

            setCurrentMp(newMp);
        }
    }

    @Override
    public synchronized void receiveDamage(L1Character attacker, int damage) {
        if (!isDead()) {
            Logger pcLogger = GmCommands.getInstance().getPcLogger(getName());

            if (pcLogger != null) {
                pcLogger.info(String.format("charName : %s,attacker : %s, x:%d,y:%d,map:%d", getName(), attacker.getName(), attacker.getX(), attacker.getY(), attacker.getMapId()));
            }

            int currentHp = getCurrentHp();

            if (currentHp > 0) {
                if (attacker != this && !getNearObjects().knownsObject(attacker) && attacker.getMapId() == getMapId()) {
                    attacker.onPerceive(this);
                }

                if (damage > 0) {
                    delInvis();

                    L1SkillUtils.removeSleep(this);

                    if (attacker instanceof L1PcInstance) {
                        getTimer().setWaitTime("lastDamagedTime", CodeConfig.TELL_WAIT_TIME);
                    }

                    onDamaged(attacker);

                    if (attacker instanceof L1PcInstance) {
                        L1PcInstance pc = (L1PcInstance) attacker;

                        L1PinkName.onAction(this, pc);

                        pc.setPetTarget(this);
                    }

                    if (getSkillEffectTimerSet().hasSkillEffect(STATUS_CHAT_PROHIBITED)) {
                        damage *= 4;
                    }

                    if (attacker instanceof L1PcInstance) {
                        L1PcInstance pc = (L1PcInstance) attacker;

                        if (pc.isGm()) {
                            damageCheck.damageCheck(pc, damage);
                        }
                    }

                    int newHp = currentHp - damage;

                    if (newHp > getMaxHp()) {
                        newHp = getMaxHp();
                    }

                    if (newHp <= 0) {
                        if (isGm()) {
                            setCurrentHp(getMaxHp());
                        } else {
                            death(attacker);
                        }
                    } else {
                        setCurrentHp(newHp);
                    }
                }
            } else {
                death(attacker);
            }
        }
    }

    public void death(L1Character lastAttacker) {
        synchronized (this) {
            if (isDead()) {
                return;
            }

            setDead(true);
            setActionStatus(L1ActionCodes.ACTION_Die);

            LineageAppContext.commonTaskScheduler().execute(new L1Death(this, lastAttacker));
        }
    }

    public int getBattleKillCount() {
        return battleKillCount;
    }

    public void setBattleKillCount(int battleKillCount) {
        this.battleKillCount = battleKillCount;
    }

    public void checkLevelDown() {
        if (getChaTra() == 1 && !isGm()) {
            sendPackets(new S_SystemMessage("현재 피녹으로 인한 케릭 블럭상태입니다."));
        }
    }

    public void sendAllMessage(String msg) {
        Collection<L1PcInstance> list = L1World.getInstance().getAllPlayers();

        for (L1PcInstance pc : list) {
            pc.sendPackets(msg);
        }
    }

    public void getBackCheck() {
        GetBackRestartTable gbrTable = GetBackRestartTable.getInstance();

        for (L1GetBackRestart gbr : gbrTable.getGetBackRestartTableList()) {
            if (getMapId() == gbr.getArea()) {
                setX(gbr.getLocX());
                setY(gbr.getLocY());
                setMap(gbr.getMapId());
                break;
            }
        }

        int castle_id = L1CastleLocation.getCastleIdByArea(this);

        if (0 < castle_id) {
            if (WarTimeScheduler.getInstance().isNowWar(castle_id)) {
                L1Clan clan = L1World.getInstance().getClan(getClanName());
                if (clan != null) {
                    if (clan.getCastleId() != castle_id) {
                        int[] loc = L1CastleLocation.getGetBackLoc(castle_id);
                        setX(loc[0]);
                        setY(loc[1]);
                        setMap((short) loc[2]);
                    }
                } else {
                    int[] loc = L1CastleLocation.getGetBackLoc(castle_id);
                    setX(loc[0]);
                    setY(loc[1]);
                    setMap((short) loc[2]);
                }
            }
        }

        if (getMapId() == 90) {
            LineageAppContext.commonTaskScheduler().schedule(() -> L1TeleportUtils.teleportToGiran(L1PcInstance.this), Instant.now().plusMillis(50));
        }
    }

    public void loadItems() {
        CharacterTable.getInstance().restoreInventory(this);
        sendPackets(new S_InvList(this));
    }

    public void loadSkills() {
        int i = 0;

        int lv1 = 0;
        int lv2 = 0;
        int lv3 = 0;
        int lv4 = 0;
        int lv5 = 0;
        int lv6 = 0;
        int lv7 = 0;
        int lv8 = 0;
        int lv9 = 0;
        int lv10 = 0;
        int knight1 = 0;
        int knight2 = 0;
        int darkElf1 = 0;
        int darkElf2 = 0;
        int royal1 = 0;
        int royal2 = 0;
        int elf1 = 0;
        int elf2 = 0;
        int elf3 = 0;
        int elf4 = 0;
        int elf5 = 0;
        int elf6 = 0;
        int dk1 = 0;
        int dk2 = 0;
        int dk3 = 0;
        int darkMage1 = 0;
        int darkMage2 = 0;
        int darkMage3 = 0;

        List<Map<String, Object>> list = SqlUtils.queryForList("SELECT * FROM character_skills WHERE char_obj_id=?", getId());

        for (Map<String, Object> o : list) {
            int skillId = Integer.parseInt(o.get("skill_id") + "");

            if (skillId == 0) {
                continue;
            }

            L1Skills skill = SkillsTable.getInstance().getTemplate(skillId);

            if (skill.getSkillLevel() == 1) {
                lv1 |= skill.getId();
            }

            if (skill.getSkillLevel() == 2) {
                lv2 |= skill.getId();
            }

            if (skill.getSkillLevel() == 3) {
                lv3 |= skill.getId();
            }

            if (skill.getSkillLevel() == 4) {
                lv4 |= skill.getId();
            }

            if (skill.getSkillLevel() == 5) {
                lv5 |= skill.getId();
            }

            if (skill.getSkillLevel() == 6) {
                lv6 |= skill.getId();
            }

            if (skill.getSkillLevel() == 7) {
                lv7 |= skill.getId();
            }
            if (skill.getSkillLevel() == 8) {
                lv8 |= skill.getId();
            }
            if (skill.getSkillLevel() == 9) {
                lv9 |= skill.getId();
            }
            if (skill.getSkillLevel() == 10) {
                lv10 |= skill.getId();
            }
            if (skill.getSkillLevel() == 11) {
                knight1 |= skill.getId();
            }
            if (skill.getSkillLevel() == 12) {
                knight2 |= skill.getId();
            }
            if (skill.getSkillLevel() == 13) {
                darkElf1 |= skill.getId();
            }
            if (skill.getSkillLevel() == 14) {
                darkElf2 |= skill.getId();
            }
            if (skill.getSkillLevel() == 15) {
                royal1 |= skill.getId();
            }
            if (skill.getSkillLevel() == 16) {
                royal2 |= skill.getId();
            }
            if (skill.getSkillLevel() == 17) {
                elf1 |= skill.getId();
            }
            if (skill.getSkillLevel() == 18) {
                elf2 |= skill.getId();
            }
            if (skill.getSkillLevel() == 19) {
                elf3 |= skill.getId();
            }
            if (skill.getSkillLevel() == 20) {
                elf4 |= skill.getId();
            }
            if (skill.getSkillLevel() == 21) {
                elf5 |= skill.getId();
            }
            if (skill.getSkillLevel() == 22) {
                elf6 |= skill.getId();
            }
            if (skill.getSkillLevel() == 23) {
                dk1 |= skill.getId();
            }
            if (skill.getSkillLevel() == 24) {
                dk2 |= skill.getId();
            }
            if (skill.getSkillLevel() == 25) {
                dk3 |= skill.getId();
            }
            if (skill.getSkillLevel() == 26) {
                darkMage1 |= skill.getId();
            }
            if (skill.getSkillLevel() == 27) {
                darkMage2 |= skill.getId();
            }
            if (skill.getSkillLevel() == 28) {
                darkMage3 |= skill.getId();
            }

            i = lv1 + lv2 + lv3 + lv4 + lv5 + lv6 + lv7 + lv8 + lv9 + lv10
                    + knight1 + knight2 + darkElf1 + darkElf2 + royal1 + royal2 + elf1 + elf2
                    + elf3 + elf4 + elf5 + elf6 + dk1 + dk2 + dk3 + darkMage1
                    + darkMage2 + darkMage3;

            setSkillMastery(skillId);
        }

        if (i > 0) {
            sendPackets(new S_AddSkill(
                    lv1, lv2, lv3, lv4, lv5, lv6, lv7, lv8, lv9, lv10,
                    knight1, knight2,
                    darkElf1, darkElf2,
                    royal1, royal2,
                    elf1, elf2, elf3, elf4, elf5, elf6,
                    dk1, dk2, dk3,
                    darkMage1, darkMage2, darkMage3));
        }
    }

    public void changgo() {
        if (isGm()) {
            return;
        }

        if (getLevel() > 5) {
            L1SkillUse l1skilluse = new L1SkillUse(this, CHANGGO_TIMER, getId(), getX(), getY(), 0);
            l1skilluse.run();
        }
    }

    public void searchSummon() {
        for (L1SummonInstance summon : L1World.getInstance().getAllSummons()) {
            if (summon.getMaster().getId() == getId()) {
                summon.setMaster(this);
                addPet(summon);

                for (L1PcInstance visiblePc : L1World.getInstance().getVisiblePlayer(summon)) {
                    visiblePc.sendPackets(new S_SummonPack(summon, visiblePc));
                }
            }
        }
    }

    public void searchArmor() {
        int count = 0;

        for (byte type = 0; type <= 12; type++) {
            if (count == 1) {
                count = 0;
            }

            for (L1ItemInstance item : getInventory().getItems()) {
                if (item.getItem().getType2() == 2 && item.isEquipped() && item.getItem().getType() == type) {
                    count++;

                    if (count == 2 && item.getItem().getType2() == 2 && item.getItem().getType() == type) {
                        if (type != 9) {
                            item.setEquipped(false);
                            count = 0;
                            break;
                        }
                    }

                    if (count == 3 && item.getItem().getType2() == 2 && item.getItem().getType() == 9) {
                        count = 0;
                        break;
                    }
                }
            }
        }
    }

    public void searchWeapon() { //무기 착용 갯수를 검사
        int t = 0;
        for (L1ItemInstance item : getInventory().getItems()) {
            if (item.getItem().getType2() == 1 && item.isEquipped())
                t++;

            if (t == 2 && item.getItem().getType2() == 1) {
                item.setEquipped(false);
                break;
            }
        }
    }

    public void baphoSystem() {
        setNBapoLevel(7);
    }

    public void checkMail() {
        int privateMailCount = L1LetterUtils.checkMailCount(getName(), false, C_MailBox.TYPE_PRIVATE_MAIL);
        int pledgeMailCount = L1LetterUtils.checkMailCount(getName(), false, C_MailBox.TYPE_BLOOD_PLEDGE_MAIL);

        if (privateMailCount > 0 || pledgeMailCount > 0) {
            sendPackets(new S_SkillSound(getId(), 1091));

            if (privateMailCount > 0) {
                sendPackets("새로운 개인 편지 도착");
            }

            if (pledgeMailCount > 0) {
                sendPackets("새로운 혈맹 편지 도착");
            }

            sendPackets(new S_ServerMessage(428));
        }
    }

    public void updateOnlineStatus() {
        CharacterTable.getInstance().updateOnlineStatus(this);
    }

    public void equipItems() {
        try {
            List<L1ItemInstance> items = inventory.getItems();

            for (L1ItemInstance item : new ArrayList<>(items)) {
                if (item.isEquipped()) {
                    getInventory().toSlotPacket(this, item);
                }
            }
        } catch (Exception e) {
            logger.error("오류", e);
        }
    }

    public void checkCloneItem() {
        inventory.checkCloneItem();
    }

    public void statUpWithBuff() {
        if (huntCount > 0) {
            L1SkillUtils.skillByLogin(this, STATUS_HUNT);
        }

        int[] icon = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

        List<CharBuff> buffList = CharBuffTable.selectList(getId());

        for (CharBuff buff : buffList) {
            int skillId = buff.getSkill_id();
            int remainingTime = buff.getRemainingTime();

            int atime = 0;

            try {
                atime = (int) ((System.currentTimeMillis() - getLogOutTime().getTime()) / 1000);
            } catch (NullPointerException ignored) {
            } catch (Exception e) {
                logger.error(e);
            }

            if (skillId >= COOKING_1_0_N && skillId <= COOKING_1_7_N
                    || skillId >= COOKING_1_8_N && skillId <= COOKING_1_14_N
                    || skillId >= COOKING_1_15_N && skillId <= COOKING_1_23_N
                    || skillId >= COOKING_1_0_S && skillId <= COOKING_1_7_S
                    || skillId >= COOKING_1_8_S && skillId <= COOKING_1_14_S
                    || skillId >= COOKING_1_15_S && skillId <= COOKING_1_23_S
                    || skillId >= COOKING_NEW_1 && skillId <= COOKING_NEW_4
            ) {
                L1CookingUtils.startCookingBuff(L1PcInstance.this, skillId, remainingTime);
                continue;
            }

            if (L1SkillUtils.hasEraseMagic(this)) {
                icon[10] = remainingTime / 4;
            }

            switch (skillId) {
                case ANTA_MAAN://지룡
                    icon[35] = (remainingTime + 16) / 32;
                    icon[36] = 46;
                    break;
                case FAFU_MAAN://수룡
                    icon[35] = (remainingTime + 16) / 32;
                    icon[36] = 47;
                    break;
                case DRAGON_AMETYHST_YES:// 드래곤자수정
                    remainingTime = remainingTime - atime;
                    sendPackets(new S_PacketBox(L1PacketBoxType.AMETYHST, remainingTime / 60));
                    break;
                case LIND_MAAN://풍룡
                    icon[35] = (remainingTime + 16) / 32;
                    icon[36] = 48;
                    break;
                case VALA_MAAN://화룡
                    icon[35] = (remainingTime + 16) / 32;
                    icon[36] = 49;
                    break;
                case BIRTH_MAAN://탄생
                    icon[35] = (remainingTime + 16) / 32;
                    icon[36] = 50;
                    break;
                case SHAPE_MAAN://형상
                    icon[35] = (remainingTime + 16) / 32;
                    icon[36] = 51;
                    break;
                case LIFE_MAAN://생명
                    icon[35] = (remainingTime + 16) / 32;
                    icon[36] = 52;
                    break;
                case DECREASE_WEIGHT:
                    icon[0] = remainingTime / 16;
                    break;
                case WEAKNESS:// 위크니스 //
                    icon[4] = remainingTime / 4;
                    break;
                case BERSERKERS:// 버서커스 //
                    icon[7] = remainingTime / 4;
                    break;
                case DISEASE:// 디지즈 //
                    icon[5] = remainingTime / 4;
                    break;
                case SILENCE:
                    icon[2] = remainingTime / 4;
                    break;
                case SHAPE_CHANGE:
                    int polyId = buff.getPoly_id();
                    L1PolyMorph.doPoly(L1PcInstance.this, polyId, remainingTime, L1PolyMorph.MORPH_BY_LOGIN);
                    continue;
                case DECAY_POTION:
                    icon[1] = remainingTime / 4;
                    break;
                case VENOM_RESIST:// 베놈 레지스트 //
                    icon[3] = remainingTime / 4;
                    break;
                case DRESS_EVASION:// 드레스 이베이젼 //
                    icon[6] = remainingTime / 4;
                    break;
                case ELEMENTAL_FALL_DOWN:
                    icon[12] = remainingTime / 4;
                    break;
                case ERASE_MAGIC:
                    icon[10] = remainingTime / 4;
                    break;
                case NATURES_TOUCH:// 네이쳐스 터치 //
                    icon[8] = remainingTime / 4;
                    break;
                case WIND_SHACKLE:
                    icon[9] = remainingTime / 4;
                    break;
                case ELEMENTAL_FIRE:
                    icon[13] = remainingTime / 4;
                    break;
                case POLLUTE_WATER:// 폴루트 워터 //
                    icon[16] = remainingTime / 4;
                    break;
                case STRIKER_GALE:// 스트라이커 게일 //
                    icon[14] = remainingTime / 4;
                    break;
                case SOUL_OF_FLAME:// 소울 오브 프레임 //
                    icon[15] = remainingTime / 4;
                    break;
                case ADDITIONAL_FIRE:
                    icon[11] = remainingTime / 16;
                    break;
                case DRAGON_SKIN:// 드래곤 스킨 //
                    icon[29] = remainingTime / 16;
                    break;
                case GUARD_BREAK:// 가드 브레이크 //
                    icon[28] = remainingTime / 4;
                    break;
                case FEAR:// 피어 //
                    icon[26] = remainingTime / 4;
                    break;
                case MORTAL_BODY:// 모탈바디 //
                    icon[24] = remainingTime / 4;
                    break;
                case HORROR_OF_DEATH:// 호러 오브 데스 //
                    icon[25] = remainingTime / 4;
                    break;
                case CONCENTRATION:
                    icon[21] = remainingTime / 16;
                    break;
                case PATIENCE:// 페이션스 //
                    icon[27] = remainingTime / 4;
                    break;
                case INSIGHT:
                    icon[22] = remainingTime / 16;
                    break;
                case PANIC:
                    icon[23] = remainingTime / 16;
                    break;
                case STATUS_BRAVE:
                    sendPackets(new S_SkillBrave(getId(), 1, remainingTime));
                    Broadcaster.broadcastPacket(L1PcInstance.this, new S_SkillBrave(getId(), 1, 0));
                    getMoveState().setBraveSpeed(1);
                    break;
                case STATUS_HASTE:
                    sendPackets(new S_SkillHaste(getId(), 1, remainingTime));
                    Broadcaster.broadcastPacket(L1PcInstance.this, new S_SkillHaste(getId(), 1, 0));
                    getMoveState().setMoveSpeed(1);
                    break;
                case STATUS_BLUE_POTION:
                case STATUS_BLUE_POTION2:
                case STATUS_BLUE_POTION3:
                    sendPackets(new S_SkillIconGFX(L1SkillIcon.파란물약, remainingTime));
                    break;
                case STATUS_ELFBRAVE:
                    sendPackets(new S_SkillBrave(getId(), 3, remainingTime));
                    Broadcaster.broadcastPacket(L1PcInstance.this, new S_SkillBrave(getId(), 3, 0));
                    getMoveState().setBraveSpeed(1);
                    break;
                case STATUS_CHAT_PROHIBITED:
                    sendPackets(new S_SkillIconGFX(채금, remainingTime));
                    break;
                case STATUS_COMA_3:// 코마 3
                    icon[31] = (remainingTime + 32) / 32;
                    icon[32] = 40;
                    break;
                case STATUS_COMA_5:// 코마 5
                    icon[31] = (remainingTime + 32) / 32;
                    icon[32] = 41;
                    break;
                case SPECIAL_COOKING:
                    if (getSkillEffectTimerSet().hasSkillEffect(SPECIAL_COOKING)) {
                        if (getSkillEffectTimerSet().getSkillEffectTimeSec(SPECIAL_COOKING) < remainingTime) {
                            getSkillEffectTimerSet().setSkillEffect(SPECIAL_COOKING, remainingTime * 1000);
                        }
                    }
                    continue;
                case STATUS_CASHSCROLL1:// 체력증강주문서 //
                    icon[18] = remainingTime / 16;
                    break;
                case STATUS_CASHSCROLL2:// 마력증강주문서 //
                    icon[18] = remainingTime / 16;
                    icon[19] = 1;
                    break;
                case STATUS_CASHSCROLL3://전투강화
                    icon[18] = remainingTime / 16;
                    icon[19] = 2;
                    break;
                case EXP_POTION1: // 천상의물약
                case EXP_POTION3:
                case EXP_POTION2:
                    icon[17] = remainingTime / 16;//
                    break;
                case STATUS_LUCK_A:// 운세에 따른 깃털 버프 // 매우좋은
                    icon[33] = remainingTime / 16;
                    icon[34] = 70;
                    break;
                case STATUS_LUCK_B:// 운세에 따른 깃털 버프 // 좋은
                    icon[33] = remainingTime / 16;
                    icon[34] = 71;
                    break;
                case STATUS_LUCK_C:// 운세에 따른 깃털 버프 // 보통
                    icon[33] = remainingTime / 16;
                    icon[34] = 72;
                    break;
                case STATUS_LUCK_D:// 운세에 따른 깃털 버프 // 나쁜
                    icon[33] = remainingTime / 16;
                    icon[34] = 73;
                    break;
                case STATUS_DRAGON_PERL:// 드진 스킬아이디
                    int time = (remainingTime / 4) - 2;
                    sendPackets(new S_DragonPerl(getId(), 8));
                    sendPackets(new S_PacketBox(L1PacketBoxType.DRAGONPERL, 8, time));
                    setDragonPerlSpeed(1);
                    break;
                case STATUS_WISDOM_POTION:
                    sendPackets(new S_SkillIconWisdomPotion(remainingTime));
                    getAbility().addSp(2);
                    break;
                case STATUS_DRAGON_EMERALD_YES:
                    sendPackets(new S_PacketBox(L1PacketBoxType.AINHASAD, getAinHasad()));
                    sendPackets(new S_PacketBox(L1PacketBoxType.EMERALD_EVA, 0x02, remainingTime));
                    break;
            }

            L1SkillUtils.skillByLogin(this, skillId, remainingTime);

            getSkillEffectTimerSet().setSkillEffect(skillId, remainingTime * 1000);
        }

        sendPackets(new S_UnityIcon(
                icon[0], icon[1], icon[2], icon[3], icon[4], icon[5], icon[6], icon[7], icon[8], icon[9], icon[10],
                icon[11], icon[12], icon[13], icon[14], icon[15], icon[16], icon[17], icon[18], icon[19], icon[20],
                icon[21], icon[22], icon[23], icon[24], icon[25], icon[26], icon[27], icon[28], icon[29], icon[30],
                icon[31], icon[32], icon[33], icon[34], icon[35], icon[36]));

        if (ainhasad > 0) {
            sendPackets(new S_PacketBox(L1PacketBoxType.AINHASAD, ainhasad));
        }
    }

    public void dieCheck() {
        if (getCurrentHp() > 0) {
            setDead(false);
            setActionStatus(0);
        } else {
            setDead(true);
            setActionStatus(L1ActionCodes.ACTION_Die);
        }
    }

    public void bonusStatCheck() {
        if (getLevel() >= 51 && getLevel() - 50 > getAbility().getBonusAbility() && getAbility().getAmount() < 150) {
            sendPackets(new S_BonusStats(getId(), 1));
        }
    }

    public void initClan() {
        if (getClanId() > 0) {
            L1Clan clan = getClan();

            int emblemId = clan.getEmblemId();

            sendPackets(new S_Emblem(emblemId));
            L1World.getInstance().broadcastPacketToAll(new S_Emblem(emblemId));

            WarTimeScheduler.getInstance().checkCastleWar(this);

            for (L1War war : L1World.getInstance().getWarList()) {
                boolean ret = war.checkClanInWar(getClanName());

                if (ret) {
                    String enemyClanName = war.getEnemyClanName(getClanName());
                    if (enemyClanName != null) {
                        sendPackets(new S_War(8, getClanName(), enemyClanName));
                    }
                    break;
                }
            }

            if (getClanId() == clan.getClanId() && getClanName().equalsIgnoreCase(clan.getClanName())) {
                for (L1PcInstance clanMember : clan.getOnlineClanMember()) {
                    if (clanMember.getId() != getId()) {
                        clanMember.sendPackets(new S_SystemMessage("혈맹원 : " + getName() + " 아덴 월드 접속"));
                    }
                }
            } else {
                setClanId(0);
                setClanName("");
                setClanRank(0);
                save();
            }
        }
    }

    public void loadBookMarks() {
        L1BookMarkTable.load(this);

        sendPackets(new S_BookMarkLoad(this));
    }

    public boolean isInn() {
        int mapId = getMapId();

        return mapId == 16384 || mapId == 16896 || mapId == 17408 || mapId == 17492
                || mapId == 17820 || mapId == 17920 || mapId == 18432 || mapId == 18944
                || mapId == 19456 || mapId == 19968 || mapId == 20480 || mapId == 20992
                || mapId == 21504 || mapId == 22016 || mapId == 22528 || mapId == 23040
                || mapId == 23552 || mapId == 24064 || mapId == 24576 || mapId == 25088;
    }

    public boolean isInLifeStream() {
        L1EffectInstance effect;
        for (L1Object object : getNearObjects().getKnownObjects()) {
            if (!(object instanceof L1EffectInstance)) {
                continue;
            }
            effect = (L1EffectInstance) object;
            if (effect.getNpcId() == 81169 && effect.getLocation().getTileLineDistance(getLocation()) < 4) {
                return true;
            }
        }
        return false;
    }

    public boolean isUnderwater() {
        if (getInventory().checkEquipped(20207)) {
            return false;
        }
        if (getSkillEffectTimerSet().hasSkillEffect(STATUS_UNDERWATER_BREATH)) {
            return false;
        }
        if (getInventory().checkEquipped(21048)
                && getInventory().checkEquipped(21049)
                && getInventory().checkEquipped(21050)) {
            return false;
        }

        return getMap().isUnderwater();
    }

    public L1PcHpMpRegen getHpMpRegen() {
        return hpMpRegen;
    }

    public void resExp() {
        int oldLevel = getLevel();
        int needExp = ExpTable.getInstance().getNeedExpNextLevel(oldLevel);
        int exp;
        double ratio;

        if (oldLevel < 45)
            ratio = 0.05;
        else if (oldLevel >= 49)
            ratio = 0.025;
        else
            ratio = 0.05 - (oldLevel - 44) * 0.005;

        exp = (int) (needExp * ratio);

        if (exp == 0)
            return;

        addExp(exp);
    }

    public void resExpToTemple() {
        int oldLevel = getLevel();
        int needExp = ExpTable.getInstance().getNeedExpNextLevel(oldLevel);
        int exp;

        double ratio;

        if (oldLevel < 45)
            ratio = 0.05;
        else if (oldLevel < 49)
            ratio = 0.05 - (oldLevel - 44) * 0.005;
        else if (oldLevel < 52)
            ratio = 0.025;
        else if (oldLevel == 52)
            ratio = 0.026;
        else if (oldLevel < 74)
            ratio = 0.026 + (oldLevel - 52) * 0.001;
        else if (oldLevel < 79)
            ratio = 0.048 - (oldLevel - 73) * 0.0005;
        else
            ratio = 0.0279;

        exp = (int) (needExp * ratio);

        if (exp == 0)
            return;

        addExp(exp);
    }

    @Override
    public int getEr() {
        int er = super.getEr();

        if (isKnight()) {
            er += getLevel() / 6;
        } else if (isCrown()) {
            er += getLevel() / 6;
        } else if (isElf()) {
            er += getLevel() / 11;
        } else if (isDarkElf()) {
            er += getLevel() / 6;
        } else if (isWizard()) {
            er += getLevel() / 12;
        }

        er += getAddEr();

        return er;
    }

    public L1Quest getQuest() {
        return quest;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String s) {
        accountName = s;
    }

    public short getBaseMaxHp() {
        return baseMaxHp;
    }

    public void addBaseMaxHp(short i) {
        i += baseMaxHp;

        if (i < 1) {
            i = 1;
        }

        addMaxHp(i - baseMaxHp);
        baseMaxHp = i;
    }

    public short getBaseMaxMp() {
        return baseMaxMp;
    }

    public void addBaseMaxMp(short i) {
        i += baseMaxMp;
        if (i < 0) {
            i = 0;
        }
        addMaxMp(i - baseMaxMp);
        baseMaxMp = i;
    }

    public int getBaseAc() {
        return 0;
    }

    public int getAdvenHp() {
        return advenHp;
    }

    public void setAdvenHp(int i) {
        advenHp = i;
    }

    public int getAdvenMp() {
        return advenMp;
    }

    public void setAdvenMp(int i) {
        advenMp = i;
    }

    public int getContribution() {
        return contribution;
    }

    public void setContribution(int i) {
        contribution = i;
    }

    public int getHighLevel() {
        return highLevel;
    }

    public void setHighLevel(int i) {
        highLevel = i;
    }

    public int getElfAttr() {
        return elfAttr;
    }

    public void setElfAttr(int i) {
        elfAttr = i;
    }

    public int getExpRes() {
        return expRes;
    }

    public void setExpRes(int i) {
        expRes = i;
    }

    public int getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(int i) {
        partnerId = i;
    }

    public int getOnlineStatus() {
        return onlineStatus;
    }

    public void setOnlineStatus(int i) {
        onlineStatus = i;
    }

    public int getHellTime() {
        return hellTime;
    }

    public void setHellTime(int i) {
        hellTime = i;
    }

    public boolean isBanned() {
        return banned;
    }

    public void setBanned(boolean flag) {
        banned = flag;
    }

    public int getHomeTownId() {
        return homeTownId;
    }

    public void setHomeTownId(int i) {
        homeTownId = i;
    }

    public int getFood() {
        return food;
    }

    public void setFood(int i) {
        food = i;
    }

    public L1EquipmentSlot getEquipSlot() {
        return equipSlot;
    }

    public void save() {
        try {
            CharacterTable.getInstance().storeCharacter(this);
        } catch (Exception e) {
            System.out.println("zxcvzxcv");
            logger.error("오류", e);
        }
    }

    public void saveInventory() {
        getInventory().getItems().stream()
                .filter(Objects::nonNull)
                .forEach(item -> getInventory().saveItem(item, item.getRecordingColumns()));
    }

    public void setRegenState(int state) {
        setHpRegenState(state);
        setMpRegenState(state);
    }

    public void setHpRegenState(int state) {
        if (hpcurPoint < state)
            return;

        this.hpcurPoint = state;
    }

    public void setMpRegenState(int state) {
        if (mpcurPoint < state)
            return;

        this.mpcurPoint = state;
    }

    public double getMaxWeight() {
        int maxWeight = 0;

        try {
            maxWeight = L1CalcStat.getMaxWeight(getAbility().getTotalStr(), getAbility().getTotalCon());
            maxWeight += getWeightReduction();

            if (getSkillEffectTimerSet().hasSkillEffect(DECREASE_WEIGHT)) {
                maxWeight += 180;
            }

            if (getSkillEffectTimerSet().hasSkillEffect(REDUCE_WEIGHT)) {
                maxWeight += 480;
            }
        } catch (Exception e) {
            logger.error("오류", e);
        }

        return maxWeight;
    }

    public boolean isFastMovable() {
        return getSkillEffectTimerSet().hasSkillEffect(HOLY_WALK, MOVING_ACCELERATION, WIND_WALK);
    }

    public boolean isBrave() {
        return getSkillEffectTimerSet().hasSkillEffect(STATUS_BRAVE, BLOOD_LUST);
    }

    public boolean isDragonPearl() {
        return getSkillEffectTimerSet().hasSkillEffect(STATUS_DRAGON_PERL);
    }

    public boolean isElfBrave() {
        return getSkillEffectTimerSet().hasSkillEffect(STATUS_ELFBRAVE);
    }

    public boolean isHaste() {
        return getSkillEffectTimerSet().hasSkillEffect(STATUS_HASTE, HASTE, GREATER_HASTE);
    }

    public boolean isInvisDelay() {
        return (invisDelayCounter > 0);
    }

    public void addInvisDelayCounter(int counter) {
        invisDelayCounter += counter;
    }

    public void beginInvisTimer() {
        addInvisDelayCounter(1);
        this.invisDelayTime = System.currentTimeMillis() + 3000L;
        PcInvisDelayScheduler.getInstance().addPc(this);
    }

    public synchronized void addExp(double exp) {
        this.exp += exp;

        if (this.exp > ExpTable.MAX_EXP) {
            this.exp = ExpTable.MAX_EXP;
        }
    }

    public void addExpBonus(int expBonus) {
        this.expBonus += expBonus;
    }

    public int getExpBonus() {
        return expBonus;
    }

    @Override
    public void setPoisonEffect(int effectId) {
        sendPackets(new S_Poison(getId(), effectId));

        if (!isGmInvis() && !isInvisible()) {
            Broadcaster.broadcastPacket(this, new S_Poison(getId(), effectId));
        }
    }

    @Override
    public void healHp(int pt) {
        super.healHp(pt);
        sendPackets(new S_HPUpdate(this));
    }

    public void healMp(int pt) {
        int newMp = getCurrentMp() + pt;
        setCurrentMp(newMp);
    }

    @Override
    public int getKarma() {
        return karma.get();
    }

    @Override
    public void setKarma(int i) {
        karma.set(i);
    }

    public void addKarma(int i) {
        karma.add(i);
    }

    public void sendUhodo() {
        if (uhodoPercent != karma.getPercent() || uhodoPercent == 0) {
            sendPackets(new S_PacketBox(87, getKarma()));
            uhodoPercent = karma.getPercent();
        }
    }

    public void addDg(int i) {
        dg += i;
        sendPackets(new S_PacketBox(L1PacketBoxType.UPDATE_DG, dg));
    }

    public int getDg() {
        return dg;
    }

    public int getKarmaLevel() {
        return karma.getLevel();
    }

    public int getKarmaPercent() {
        return karma.getPercent();
    }

    public Timestamp getLastPk() {
        return lastPk;
    }

    public void setLastPk(Timestamp time) {
        lastPk = time;
    }

    public void setLastPk() {
        lastPk = new Timestamp(System.currentTimeMillis());
    }

    public Timestamp getDeleteTime() {
        return deleteTime;
    }

    public void setDeleteTime(Timestamp time) {
        deleteTime = time;
    }

    public int getWeightReduction() {
        return weightReduction;
    }

    public void addWeightReduction(int i) {
        weightReduction += i;
    }

    public int getHasteItemEquipped() {
        return hasteItemEquipped;
    }

    public void addHasteItemEquipped(int i) {
        hasteItemEquipped += i;
    }

    public void removeHasteSkillEffect() {
        if (getSkillEffectTimerSet().hasSkillEffect(L1SkillId.SLOW))
            getSkillEffectTimerSet().removeSkillEffect(L1SkillId.SLOW);
        if (getSkillEffectTimerSet().hasSkillEffect(L1SkillId.GRATE_SLOW))
            getSkillEffectTimerSet().removeSkillEffect(L1SkillId.GRATE_SLOW);
        if (getSkillEffectTimerSet().hasSkillEffect(L1SkillId.ENTANGLE))
            getSkillEffectTimerSet().removeSkillEffect(L1SkillId.ENTANGLE);
        if (getSkillEffectTimerSet().hasSkillEffect(L1SkillId.HASTE))
            getSkillEffectTimerSet().removeSkillEffect(L1SkillId.HASTE);
        if (getSkillEffectTimerSet().hasSkillEffect(L1SkillId.GREATER_HASTE))
            getSkillEffectTimerSet().removeSkillEffect(L1SkillId.GREATER_HASTE);
        if (getSkillEffectTimerSet().hasSkillEffect(L1SkillId.STATUS_HASTE))
            getSkillEffectTimerSet().removeSkillEffect(L1SkillId.STATUS_HASTE);
    }

    public void removeFastMove() {
        if (getSkillEffectTimerSet().hasSkillEffect(HOLY_WALK))
            getSkillEffectTimerSet().removeSkillEffect(HOLY_WALK);

        if (getSkillEffectTimerSet().hasSkillEffect(MOVING_ACCELERATION))
            getSkillEffectTimerSet().removeSkillEffect(MOVING_ACCELERATION);

        if (getSkillEffectTimerSet().hasSkillEffect(WIND_WALK))
            getSkillEffectTimerSet().removeSkillEffect(WIND_WALK);
    }

    public void removeBraveSkillEffect() {
        if (getSkillEffectTimerSet().hasSkillEffect(STATUS_BRAVE))
            getSkillEffectTimerSet().removeSkillEffect(STATUS_BRAVE);

        if (getSkillEffectTimerSet().hasSkillEffect(STATUS_ELFBRAVE))
            getSkillEffectTimerSet().removeSkillEffect(STATUS_ELFBRAVE);

        if (getSkillEffectTimerSet().hasSkillEffect(STATUS_DRAGON_PERL))
            getSkillEffectTimerSet().removeSkillEffect(STATUS_DRAGON_PERL);

        if (getSkillEffectTimerSet().hasSkillEffect(L1SkillId.DANCING_BLADES))
            getSkillEffectTimerSet().removeSkillEffect(L1SkillId.DANCING_BLADES);
    }

    public void checkStatus() {
        if (!getAbility().isNormalAbility(getClassId(), getLevel(), getHighLevel(), getAbility().getBaseAmount()) && !isGm()) {
            returnStats();
        }
    }

    public void cancelAbsoluteBarrier() {
        L1MagicUtils.stopAbsoluteBarrier(this);
    }

    public int getPkCount() {
        return pkCount;
    }

    public void setPkCount(int i) {
        pkCount = i;
    }

    public int getClanId() {
        return clanId;
    }

    public void setClanId(int i) {
        clanId = i;
    }

    public String getClanName() {
        return clanName;
    }

    public void setClanName(String s) {
        clanName = s;
    }

    public L1Clan getClan() {
        return L1World.getInstance().getClan(getClanName());
    }

    public int getClanRank() {
        return clanRank;
    }

    public void setClanRank(int i) {
        clanRank = i;

        try {
            if (getClan() != null) {
                getClan().setClanRank(getName(), i);
            }
        } catch (Exception e) {
            logger.error("오류", e);
        }
    }

    public byte getSex() {
        return sex;
    }

    public void setSex(int i) {
        sex = (byte) i;
    }

    public String getMaleName() {
        if (sex == 0) {
            return "male";
        } else {
            return "female";
        }
    }

    public String getReasonToHunt() {
        return reasonTohunt;
    }

    public void setReasonToHunt(String s) {
        reasonTohunt = s;
    }

    public int getHuntCount() {
        return huntCount;
    }

    public void setHuntCount(int i) {
        huntCount = i;
    }

    public boolean isHunt() {
        if (isAdmin()) {
            return true;
        }

        return huntCount > 0;
    }

    public boolean isAdmin() {
        return "메티스".equalsIgnoreCase(getName()) || "미소피아".equalsIgnoreCase(getName());
    }

    public int getHuntPrice() {
        return huntPrice;
    }

    public void setHuntPrice(int i) {
        huntPrice = i;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int i) {
        age = i;
    }

    public boolean isGm() {
        return gm;
    }

    public void setGm(boolean flag) {
        gm = flag;
    }

    public boolean isThirdSpeed() {
        return (getSkillEffectTimerSet().hasSkillEffect(L1SkillId.STATUS_DRAGON_PERL) || getDragonPerlSpeed() == 1);
    }

    public int getDragonPerlSpeed() {
        return dragonPerlSpeed;
    }

    public void setDragonPerlSpeed(int i) {
        dragonPerlSpeed = i;
    }

    public int getDamageReductionByArmor() {
        return damageReductionByArmor;
    }

    public void addDamageReductionByArmor(int i) {
        damageReductionByArmor += i;
    }

    public int getBowHitUpByArmor() {
        return bowHitUpByArmor;
    }

    public void addBowHitupByArmor(int i) {
        bowHitUpByArmor += i;
    }

    public int getBowDmgUpByArmor() {
        return bowDmgUpByArmor;
    }

    public void addBowDmgupByArmor(int i) {
        bowDmgUpByArmor += i;
    }

    public int getAddDmgUpByArmor() {
        return addDmgUpByArmor;
    }

    public void addAddDmgUpByArmor(int i) {
        addDmgUpByArmor += i;
    }

    public int getAddPvpDmgUp() {
        return addPvpDmgUp;
    }

    public void addAddPvpDmgUp(int i) {
        addPvpDmgUp += i;
    }

    public void addAddPvpReudction(int i) {
        addPvpReduction += i;
    }

    public int getAddPvpReduction() {
        return addPvpReduction;
    }

    public void addAddEr(int i) {
        addEr += i;
    }

    public int getAddEr() {
        return addEr;
    }

    public int getHitUpByArmor() {
        return hitUpByArmor;
    }

    public void addHitUpByArmor(int i) {
        hitUpByArmor += i;
    }

    public int getDmgUpByArmor() {
        return dmgUpByArmor;
    }

    public void addDmgUpByArmor(int i) {
        dmgUpByArmor += i;
    }

    public boolean isGresValid() {
        return gresValid;
    }

    void setGresValid(boolean valid) {
        gresValid = valid;
    }

    public void addAddMagicHitUp(int i) {
        addMagicHitUp += i;
    }

    public boolean isFishing() {
        return fishing;
    }

    public void setFishing(boolean flag) {
        fishing = flag;
    }

    public void endFishing() {
        try {
            if (isFishing()) {
                setFishing(false);
                sendPackets(new S_CharVisualUpdate(this));
                Broadcaster.broadcastPacket(this, new S_CharVisualUpdate(this));
                FishingTimeScheduler.getInstance().removeMember(this);
            }
        } catch (Exception e) {
            logger.error("오류", e);
        }
    }

    public int getCookingId() {
        return cookingId;
    }

    public void setCookingId(int i) {
        cookingId = i;
    }

    public int getDessertId() {
        return dessertId;
    }

    public void setDessertId(int i) {
        dessertId = i;
    }

    public L1ExcludingList getExcludingList() {
        return excludingList;
    }

    public L1AcceleratorCheck getAcceleratorChecker() {
        return acceleratorChecker;
    }

    public int getTeleportX() {
        return teleportX;
    }

    public void setTeleportX(int i) {
        teleportX = i;
    }

    public int getTeleportY() {
        return teleportY;
    }

    public void setTeleportY(int i) {
        teleportY = i;
    }

    public short getTeleportMapId() {
        return teleportMapId;
    }

    public void setTeleportMapId(short i) {
        teleportMapId = i;
    }

    public int getTeleportHeading() {
        return teleportHeading;
    }

    public void setTeleportHeading(int i) {
        teleportHeading = i;
    }

    public int getTempCharGfxAtDead() {
        return tempCharGfxAtDead;
    }

    public void setTempCharGfxAtDead(int i) {
        tempCharGfxAtDead = i;
    }

    public boolean isCanWhisper() {
        return canWhisper;
    }

    public void setCanWhisper(boolean flag) {
        canWhisper = flag;
    }

    public boolean isShowTradeChat() {
        return showTradeChat;
    }

    public void setShowTradeChat(boolean flag) {
        showTradeChat = flag;
    }

    public boolean isShowWorldChat() {
        return showWorldChat;
    }

    public void setShowWorldChat(boolean flag) {
        showWorldChat = flag;
    }

    public int getFightId() {
        return fightId;
    }

    public void setFightId(int i) {
        fightId = i;
    }

    public boolean isDeathMatch() {
        return false;
    }

    public int getCallClanId() {
        return callClanId;
    }

    public void setCallClanId(int i) {
        callClanId = i;
    }

    public int getCallClanHeading() {
        return callClanHeading;
    }

    public void setCallClanHeading(int i) {
        callClanHeading = i;
    }

    public boolean isSummonMonster() {
        return summonMonster;
    }

    public void setSummonMonster(boolean SummonMonster) {
        summonMonster = SummonMonster;
    }

    public boolean isShapeChange() {
        return shapeChange;
    }

    public void setShapeChange(boolean isShapeChange) {
        shapeChange = isShapeChange;
    }

    public boolean isArchShapeChange() {
        return archShapeChange;
    }

    public void setArchShapeChange(boolean isArchShapeChange) {
        archShapeChange = isArchShapeChange;
    }

    public boolean isArchPolyType() {
        return archPolyType;
    }

    public void setArchPolyType(boolean isArchPolyType) {
        archPolyType = isArchPolyType;
    }

    public boolean isDesShapeChange() {
        return desShapeChange;
    }

    public void setDesShapeChange(boolean isDesShapeChange) {
        desShapeChange = isDesShapeChange;
    }

    public int getElfGrave() {
        return elfGrave;
    }

    public void setElfGrave(int i) {
        elfGrave = i;
    }

    public int getDreamTimer() {
        return dreamTimer;
    }

    public void setDream_Timer(int i) {
        dreamTimer = i;
    }

    public int getIvoryTimer() {
        return ivoryTimer;
    }

    public void setIvoryTimer(int i) {
        ivoryTimer = i;
    }

    public Timestamp getLogOutTime() {
        return logoutTime;
    }

    public void setLogOutTime(Timestamp t) {
        logoutTime = t;
    }

    public void setLogOutTime() {
        logoutTime = new Timestamp(System.currentTimeMillis());
    }

    public int getChaTra() {
        return chaTra;
    }

    public void setChaTra(int i) {
        chaTra = i;
    }

    public int getAinHasad() {
        return ainhasad;
    }

    public void setAinHasad(int i) {
        ainhasad = i;
    }

    public void calAinHasad(int i) {
        int calc = ainhasad + i;

        if (calc >= 2000000)
            calc = 2000000;

        if (calc < 0) {
            calc = 0;
        }

        ainhasad = calc;
    }

    public int getLastEnchantItemId() {
        return enchantItemId;
    }

    public void setLastEnchantItemId(int i, L1ItemInstance item) {
        if (getLastEnchantItemId() == i && i != 0) {
            client.disconnect();
            getInventory().removeItem(item, item.getCount());
            return;
        }
        enchantItemId = i;
    }

    public void addPet(L1NpcInstance npc) {
        petList.put(npc.getId(), npc);
        sendPetCtrlMenu(npc, true);
    }

    public void removePet(L1NpcInstance npc) {
        petList.remove(npc.getId(), npc);

        if (petList.isEmpty()) {
            sendPetCtrlMenu(npc, false);
        }
    }

    public void sendPetCtrlMenu(L1NpcInstance npc, boolean type) {
        if (npc instanceof L1PetInstance) {
            L1PetInstance pet = (L1PetInstance) npc;
            L1Character cha = pet.getMaster();
            if (cha instanceof L1PcInstance) {
                L1PcInstance pc = (L1PcInstance) cha;
                pc.sendPackets(new S_PetCtrlMenu(pc, npc, type));
            }
        } else if (npc instanceof L1SummonInstance) {
            L1SummonInstance summon = (L1SummonInstance) npc;
            L1Character cha = summon.getMaster();
            if (cha instanceof L1PcInstance) {
                L1PcInstance pc = (L1PcInstance) cha;
                pc.sendPackets(new S_PetCtrlMenu(pc, npc, type));
            }
        }
    }

    public Map<Integer, L1NpcInstance> getPetList() {
        return petList;
    }

    public void addDoll(L1DollInstance doll) {
        dollList.put(doll.getId(), doll);
    }

    public Map<Integer, L1DollInstance> getDollList() {
        return dollList;
    }

    public L1DollInstance getCurrentDoll() {
        Collection<L1DollInstance> dollList = getDollList().values();

        for (L1DollInstance doll : dollList) {
            if (doll.getItemObjId() == getCurrentDollId()) {
                return doll;
            }
        }

        return null;
    }

    public L1ItemInstance getCurrentDollItem() {
        if (getCurrentDoll() != null) {
            return getCurrentDoll().getItem();
        }

        return null;
    }

    public void addFollower(L1FollowerInstance follower) {
        followerList.put(follower.getId(), follower);
    }

    public Map<Integer, L1FollowerInstance> getFollowerList() {
        return followerList;
    }

    public int getAinState() {
        return ainState;
    }

    public void setAinState(int AinState) {
        this.ainState = AinState;
    }

    public int getTotalIgnoreReduction() {
        int result = 0;

        List<L1ItemInstance> items = inventory.getCurrentItem().getItems();

        for (L1ItemInstance item : items) {
            if (item == null) {
                continue;
            }

            result += item.getIgnoreReduction();
        }

        return result;
    }

    public int getTotalReduction() {
        int result = 0;

        for (int i = COOKING_1_0_S; i <= COOKING_1_23_S; i++) {
            if (getSkillEffectTimerSet().hasSkillEffect(i)) {
                result += 5;
            }
        }

        for (int i = COOKING_NEW_1; i <= COOKING_NEW_4; i++) {
            if (getSkillEffectTimerSet().hasSkillEffect(i)) {
                result += 2;
            }
        }

        if (getSkillEffectTimerSet().hasSkillEffect(EARTH_GUARDIAN)) {
            result += 2;
        }

        if (getSkillEffectTimerSet().hasSkillEffect(STATUS_LUCK_A)) {
            result += 3;
        }

        if (getSkillEffectTimerSet().hasSkillEffect(STATUS_LUCK_B)) {
            result += 2;
        }

        if (getSkillEffectTimerSet().hasSkillEffect(REDUCTION_ARMOR)) {
            double targetPcLvl = getLevel();

            if (targetPcLvl < 50) {
                targetPcLvl = 50;
            }

            result += (targetPcLvl - 50) / 5 + 1;
        }

        if (getSkillEffectTimerSet().hasSkillEffect(DRAGON_SKIN)) {
            result += 5;
        }

        if (getSkillEffectTimerSet().hasSkillEffect(PATIENCE)) {
            result += 2;
        }

        result += getDamageReductionByArmor();

        result += ability.getBaseReduction();

        return result;
    }

    public boolean isCrown() {
        return L1ClassUtils.isCrown(getClassId());
    }

    public boolean isKnight() {
        return L1ClassUtils.isKnight(getClassId());
    }

    public boolean isElf() {
        return L1ClassUtils.isElf(getClassId());
    }

    public boolean isWizard() {
        return L1ClassUtils.isWizard(getClassId());
    }

    public boolean isDarkElf() {
        return L1ClassUtils.isDarkelf(getClassId());
    }

    public boolean isDragonKnight() {
        return L1ClassUtils.isDragonknight(getClassId());
    }

    public boolean isIllusionist() {
        return L1ClassUtils.isIllusionist(getClassId());
    }

    public L1PcBookMark getBookMark() {
        return bookMark;
    }

    public L1AutoPotion getAutoPotion() {
        return autoPotion;
    }

    public boolean isTripleAction() {
        return tripleAction;
    }

    public void setTripleAction(boolean tripleAction) {
        this.tripleAction = tripleAction;
    }

    public int getDrainHp() {
        return drainHp;
    }

    public void setDrainHp(int drainHp) {
        this.drainHp = drainHp;
    }

    @Override
    public int drainMana(L1Character target) {
        int drainValue = getDrainMp();

        if (drainValue > 0 && target.getCurrentMp() > 0) {
            int mp = getCurrentMp() + drainValue;
            setCurrentMp(mp);

            int targetMp = target.getCurrentMp() - drainValue;
            target.setCurrentMp(targetMp);
        }

        setDrainMp(0);

        return drainValue;
    }

    @Override
    public int drainHp(L1Character target) {
        int drainValue = getDrainHp();

        if (drainValue > 0 && target.getCurrentHp() > 0) {
            int newHp = getCurrentHp() + drainValue;
            setCurrentHp(newHp);

            int targetHp = target.getCurrentHp() - drainValue;
            target.setCurrentHp(targetHp);
        }

        setDrainHp(0);

        return 0;
    }

    public int getDrainMp() {
        return drainMp;
    }

    public void setDrainMp(int drainMp) {
        this.drainMp = drainMp;
    }

    public L1PcExpManager getPcExpManager() {
        return pcExpManager;
    }

    public void returnStats() {
        try {
            getAbility().initStat(getClassId());

            L1SkillUse cancel = new L1SkillUse(this, L1SkillId.CANCELLATION, getId(), getX(), getY(), 0);
            cancel.run();

            if (getWeapon() != null) {
                getInventory().setEquipped(getWeapon(), false, false, false);
            }

            sendPackets(new S_CharVisualUpdate(this));
            sendPackets(new S_OwnCharStatus2(this));

            for (L1ItemInstance armor : getInventory().getItems()) {
                for (int i = 0; i <= 12; i++) {
                    if (armor != null) {
                        getInventory().setEquipped(armor, false, false, false);
                    }
                }
            }

            setReturnStat(getExp());
            sendPackets(new S_SPMR(this));
            sendPackets(new S_OwnCharAttrDef(this));
            sendPackets(new S_OwnCharStatus2(this));
            sendPackets(new S_ReturnedStat(this, S_ReturnedStat.START));
            statReturnCheck = true;

            save();
        } catch (Exception e) {
            logger.error("오류", e);
        }
    }

    public void kick() {
        try {
            SqlUtils.update("UPDATE accounts SET banned = 0 WHERE login= ?", getAccountName());
            client.disconnectNow();
            L1World.getInstance().broadcastServerMessage("버그 시도: [" + getName() + "]");
        } catch (Exception ignored) {
        }
    }

    public void clearPlayerClanData() {
        ClanWarehouse clanWarehouse = WarehouseManager.getInstance().getClanWarehouse(getClan().getClanName());

        if (clanWarehouse != null) {
            clanWarehouse.unlock(getId());
        }

        setClanId(0);
        setClanName("");
        setTitle("");
        sendPackets(new S_CharTitle(getId(), ""));
        Broadcaster.broadcastPacket(this, new S_CharTitle(getId(), ""));
        setClanRank(0);
        save();
    }

    public void huntBuff() {
    }

    public void selectCharacter() {
        if (huntCount > 0) {
            huntBuff();
        }

        if (getGfxId().getTempCharGfx() == 1154) {
            L1PolyMorph.undoPoly(this);
        }

        equipItems();

        timeDungeon.loadTimeDungeon();

        if (isCrown()) {
            BraveAvatarScheduler.getInstance().addCrown(this);
        }

        startAutoUpdate();

        autoCheckScheduler.start();

        inventory.initArrow();

        if (CodeConfig.SAFE_MODE) {
            L1CommonUtils.safeMode(this, true);
        }

        loadFavPolyList();

        getAccount().getGrangKain().sendIcon(this);

        getRankBuff().startBuff();

        CommonCommands.getInstance().handleCommands(this, "물약멘트 끔");
        CommonCommands.getInstance().handleCommands(this, "맵핵 켬");

        LineageAppContext.commonTaskScheduler().schedule(this::sendGameTime, Instant.now().plusMillis(500));
    }

    public void sendGameTime() {
        sendPackets(new S_GameTime(GameTimeScheduler.getInstance().getTime().getSeconds()));
    }

    public void startAutoUpdate() {
        if (autoUpdateFuture != null)
            return;

        getNearObjects().removeAllKnownObjects();
        autoUpdateFuture = LineageAppContext.autoUpdateTaskScheduler().scheduleWithFixedDelay(this::updateObject, Duration.ofMillis(CodeConfig.AUTO_UPDATE_INTERVAL));
    }

    public void stopAutoUpdate() {
        if (autoUpdateFuture != null) {
            autoUpdateFuture.cancel(true);
            autoUpdateFuture = null;
        }
    }

    public Integer getExpPer() {
        return ExpTable.getInstance().getExpPercentage(getLevel(), getExp());
    }

    public List<L1Drop> getItemDropSearchList() {
        return itemDropSearchList;
    }

    public void setItemDropSearchList(List<L1Drop> itemDropSearchList) {
        this.itemDropSearchList = itemDropSearchList;
    }

    public int getCurrentDollId() {
        return currentDollId;
    }

    public void setCurrentDollId(int currentDollId) {
        this.currentDollId = currentDollId;
    }

    public L1PcSpeedHackCheck getSpeedHack() {
        return speedHack;
    }

    public int getHprMaxBonus() {
        int maxBonus = 1;

        if (11 < getLevel() && 14 <= getAbility().getTotalCon()) {
            maxBonus = getAbility().getTotalCon() - 12;

            if (25 < getAbility().getTotalCon()) {
                maxBonus = 14;
            }
        }

        return maxBonus;
    }

    public int getCurrentHpTic() {
        if (isDead()) {
            return 0;
        }

        if (isHpMpRegenNotAble()) {
            return 0;
        }

        if (getSkillEffectTimerSet().hasSkillEffect(BERSERKERS)) {
            return 0;
        }

        int basebonus = 0;
        int bonus = 0;

        if (getSkillEffectTimerSet().hasSkillEffect(NATURES_TOUCH)) {
            bonus += 15;
        }

        int inn = 30;

        if (L1HouseLocation.isInHouse(getX(), getY(), getMapId())) {
            bonus += inn;
        }

        if (isInn()) {
            bonus += inn;
        }

        if (L1HouseLocation.isRegenLoc(this, getX(), getY(), getMapId())) {
            bonus += inn;
        }

        if (L1CastleLocation.isInCastleInner(getMapId())) {
            bonus += inn;
        }

        if (isInLifeStream()) {
            bonus += 3;
        }

        int itemTick = getInventory().hpRegenPerTick();

        return bonus + basebonus + itemTick + getHpr();
    }

    public boolean isHpMpRegenNotAble() {
        if (isInn()) {
            return false;
        }

        if (getSkillEffectTimerSet().hasSkillEffect(EXOTIC_VITALIZE)
                || getSkillEffectTimerSet().hasSkillEffect(ADDITIONAL_FIRE)
                || getSkillEffectTimerSet().hasSkillEffect(L1SkillId.SCALES_WATER_DRAGON)) {
            return false;
        }

        if (inventory.isOverWeight48()) {
            return true;
        }

        return getFood() < 24;
    }

    public int getCurrentMpTic() {
        if (isDead()) {
            return 0;
        }

        if (isHpMpRegenNotAble()) {
            return 0;
        }

        int tick = 1;

        int wis = getAbility().getTotalWis();

        if (wis >= 30) {
            tick = 7;
        } else if (wis >= 25) {
            tick = 6;
        } else if (wis >= 20) {
            tick = 5;
        } else if (wis >= 16) {
            tick = 4;
        }

        if (getSkillEffectTimerSet().hasSkillEffect(STATUS_BLUE_POTION)) {
            if (wis < 12) {
                wis = 11;
            }

            tick += wis - 10;
        }

        if (getSkillEffectTimerSet().hasSkillEffect(STATUS_BLUE_POTION2)) {
            if (wis < 12) {
                wis = 12;
            }

            tick += wis - 8;
        }

        if (getSkillEffectTimerSet().hasSkillEffect(STATUS_BLUE_POTION3)) {
            tick += 3;
        }

        if (getSkillEffectTimerSet().hasSkillEffect(MEDITATION)) {
            tick += 5;
        }

        int innTic = 30;

        if (L1HouseLocation.isInHouse(getX(), getY(), getMapId())) {
            tick += innTic;
        }

        if (isInn()) {
            tick += innTic;
        }

        if (L1HouseLocation.isRegenLoc(this, getX(), getY(), getMapId())) {
            tick += innTic;
        }

        if (L1CastleLocation.isInCastleInner(getMapId())) {
            tick += innTic;
        }

        int itemMpr = getInventory().mpRegenPerTick();

        return tick + itemMpr + getMpr();
    }

    public boolean isUsingDoll() {
        return usingDoll;
    }

    public void setUsingDoll(boolean usingDoll) {
        this.usingDoll = usingDoll;
    }

    public boolean isEquipServerRune() {
        return equipServerRune;
    }

    public void setEquipServerRune(boolean equipServerRune) {
        this.equipServerRune = equipServerRune;
    }

    public L1WeaponInfo getWeaponInfo() {
        return new L1WeaponInfo(getWeapon());
    }

    @Override
    public boolean isLongAttack() {
        return getWeaponInfo().isLongAttack();
    }

    public boolean isStatReturnCheck() {
        return statReturnCheck;
    }

    public void setStatReturnCheck(boolean statReturnCheck) {
        this.statReturnCheck = statReturnCheck;
    }

    public void setFishingX(int fishingX) {
        this.fishingX = fishingX;
    }

    public void setFishingY(int fishingY) {
        this.fishingY = fishingY;
    }

    public long getDollHPRegenTime() {
        return dollHPRegenTime;
    }

    public void setDollHpRegenTime(long dollHPRegenTime) {
        this.dollHPRegenTime = dollHPRegenTime;
    }

    public long getDollMPRegenTime() {
        return dollMPRegenTime;
    }

    public void setDollMpRegenTime(long dollMPRegenTime) {
        this.dollMPRegenTime = dollMPRegenTime;
    }

    public long getInvisDelayTime() {
        return invisDelayTime;
    }

    //
    public L1TimeDungeon getTimeDungeon() {
        return timeDungeon;
    }

    public L1Ment getMent() {
        return ment;
    }

    public void onStat(String statName) {
        L1PcInstance pc = this;

        final int BONUS_ABILITY = pc.getAbility().getBonusAbility();

        if (!(pc.getLevel() - 50 > BONUS_ABILITY))
            return;

        if (getOnlineStatus() != 1) { // 127 스텟 버그 수정
            client.disconnect();
            return;
        }

        int MAX_STAT = 35;

        if (statName.equalsIgnoreCase("str")) {
            if (pc.getAbility().getStr() < MAX_STAT) {
                pc.getAbility().addStr(1); // 소의 STR치에+1
                sendDefaultPacket(pc, BONUS_ABILITY);
            } else {
                pc.sendPackets(new S_ServerMessage(481)); // 하나의 능력치의 최대치는 25입니다. 다른 능력치를 선택해 주세요
            }
        } else if (statName.equalsIgnoreCase("dex")) {
            if (pc.getAbility().getDex() < MAX_STAT) {
                pc.getAbility().addDex(1);
                pc.getPcExpManager().resetAc();
                sendDefaultPacket(pc, BONUS_ABILITY);
            } else {
                pc.sendPackets(new S_ServerMessage(481));
            }
        } else if (statName.equalsIgnoreCase("con")) {
            if (pc.getAbility().getCon() < MAX_STAT) {
                pc.getAbility().addCon(1);
                sendDefaultPacket(pc, BONUS_ABILITY);
            } else {
                pc.sendPackets(new S_ServerMessage(481));
            }
        } else if (statName.equalsIgnoreCase("int")) {
            if (pc.getAbility().getInt() < MAX_STAT) {
                pc.getAbility().addInt(1);
                sendDefaultPacket(pc, BONUS_ABILITY);
            } else {
                pc.sendPackets(new S_ServerMessage(481));
            }
        } else if (statName.equalsIgnoreCase("wis")) {
            if (pc.getAbility().getWis() < MAX_STAT) {
                pc.getAbility().addWis(1);
                pc.getPcExpManager().resetMr();
                sendDefaultPacket(pc, BONUS_ABILITY);
            } else {
                pc.sendPackets(new S_ServerMessage(481));
            }
        } else if (statName.equalsIgnoreCase("cha")) {
            if (pc.getAbility().getCha() < MAX_STAT) {
                pc.getAbility().addCha(1);
            } else {
                pc.sendPackets(new S_ServerMessage(481));
            }
        }

        pc.checkStatus();

        if (pc.getLevel() >= 51 && pc.getLevel() - 50 > pc.getAbility().getBonusAbility()) {
            if ((pc.getAbility().getStr() + pc.getAbility().getDex() + pc.getAbility().getCon() + pc.getAbility().getInt() + pc.getAbility().getWis() + pc.getAbility().getCha()) < 150) {
                pc.sendPackets(new S_BonusStats(pc.getId(), 1));
            }
        }
    }

    public void sendDefaultPacket(L1PcInstance pc, int BONUS_ABILITY) {
        pc.getAbility().setBonusAbility(BONUS_ABILITY + 1);

        pc.sendPackets(new S_SPMR(pc));
        pc.sendPackets(new S_OwnCharStatus(pc));
        pc.sendPackets(new S_OwnCharStatus2(pc));
        pc.sendPackets(new S_CharVisualUpdate(pc));
        pc.sendPackets(new S_SystemMessage("* 보너스 스텟을 확인 합니다 *"));
        pc.sendPackets(new S_SystemMessage(" STR: " + pc.getAbility().getStr() +
                " DEX:" + pc.getAbility().getDex() +
                " CON:" + pc.getAbility().getCon() +
                " INT:" + pc.getAbility().getInt() +
                " WIS:" + pc.getAbility().getWis() +
                " CHA:" + pc.getAbility().getCha()));
    }

    public boolean isHuntMapAndNoHunt(int mapId) {
        if (CodeConfig.isHuntMap(mapId) && !isHunt()) {
            String mapName = MapsTable.getInstance().getMapName(mapId);

            String msg = mapName + "은 수배사냥터 입니다";

            sendPackets(msg);
            sendGreenMessage(msg);

            return true;
        }

        return false;
    }

    public int getBattleDeathCount() {
        return battleDeathCount;
    }

    public void setBattleDeathCount(int battleDeathCount) {
        this.battleDeathCount = battleDeathCount;
    }

    public boolean isEscapable() {
        if (CodeConfig.GM_IGNORE_TELEPORT_ABLE && isGm()) {
            return true;
        }

        return MapsTable.getInstance().isEscapeAble(getMapId());
    }

    public boolean isTeleportAble() {
        if (CodeConfig.GM_IGNORE_TELEPORT_ABLE && isGm()) {
            return true;
        }

        return MapsTable.getInstance().isTeleportAble(getMapId());
    }

    public boolean isUsableSkill() {
        return MapsTable.getInstance().isUseAbleSkill(getMapId());
    }

    public boolean isUseResurrection() {
        return MapsTable.getInstance().isUseResurrection(getMapId());
    }

    public int getAddDmg() {
        int dmg = 0;

        L1ItemInstance weapon = getWeapon();

        if (weapon != null && weapon.isEquipped()) {
            dmg += weapon.getDmgByMagic();
        }

        dmg += bapodmg;

        return dmg;
    }

    public int getTotalDmg() {
        return getDmgUp() + getDmgUpByArmor() + getAddDmgUpByArmor();
    }

    @Override
    public int getTotalHitUp() {
        int result = getHitUp() + getHitUpByArmor() + getAddHitUpByArmor();

        L1ItemInstance weapon = getWeapon();

        if (weapon != null && weapon.isEquipped()) {
            result += weapon.getHitByMagic();
        }

        return result;
    }

    public int getMagicDamage() {
        int result = 0;

        result += L1CalcStat.calcBaseStatMagicDamage(this);

        return result;
    }

    public int getTotalBowDmg() {
        return getBowDmgUp() + getBowDmgUpByArmor() + getAddDmgUpByArmor();
    }

    @Override
    public int getTotalBowHitUp() {
        int result = getBowHitUp() + getBowHitUpByArmor() + getAddHitUpByArmor();

        L1ItemInstance weapon = getWeapon();

        if (weapon != null && weapon.isEquipped()) {
            result += weapon.getHitByMagic();
        }

        return result;
    }

    public void disconnect() {
        client.disconnect();
    }

    public void disconnect(String reason) {
        logger.info("disconnect reason : " + reason);

        disconnect();
    }

    public int getAddHitUpByArmor() {
        return addHitUpByArmor;
    }

    public void addAddHitUpByArmor(int i) {
        addHitUpByArmor += i;
    }

    public L1DamageCheck getDamageCheck() {
        return damageCheck;
    }

    public void setDamageCheck(L1DamageCheck damageCheck) {
        this.damageCheck = damageCheck;
    }

    private final L1AutoAttack autoAttack = new L1AutoAttack(this);

    public L1AutoAttack getAutoAttack() {
        return autoAttack;
    }

    public int getAddStunHit() {
        return addStunHit;
    }

    public void addAddStunHit(int i) {
        addStunHit += i;
    }

    public double getAddPotionPer() {
        return addPotionPer;
    }

    public void addAddPotionPer(double i) {
        addPotionPer += i;
    }

    public Account getAccount() {
        if (client == null) {
            return null;
        }

        return client.getAccount();
    }

    public void setTrueTargetLeaderId(int trueTargetLeaderId) {
        this.trueTargetLeaderId = trueTargetLeaderId;
    }

    public int getTrueTargetLeaderId() {
        return trueTargetLeaderId;
    }

    public L1DollCombine getDollCombine() {
        return dollCombine;
    }

    public boolean isMarkShow() {
        return markShow;
    }

    public void setMarkShow(boolean markShow) {
        this.markShow = markShow;
    }

    public void returnAllPetAndSummon() {
        for (L1NpcInstance petObject : petList.values()) {
            if (petObject instanceof L1PetInstance) {
                L1PetInstance pet = (L1PetInstance) petObject;

                if (!pet.getInventory().checkItem(40079)) {
                    pet.dropItem();
                } else {
                    pet.collect();
                    pet.getInventory().consumeItem(40079);
                }

                int time = pet.getSkillEffectTimerSet().getSkillEffectTimeSec(L1SkillId.STATUS_PET_FOOD);
                PetTable.getInstance().storePetFoodTime(pet.getId(), pet.getFood(), time);
                pet.getSkillEffectTimerSet().clearSkillEffectTimer();
                petList.remove(pet.getId());
                pet.deleteMe();
            } else if (petObject instanceof L1SummonInstance) { // 서먼.
                L1SummonInstance sunm = (L1SummonInstance) petObject;
                sunm.dropItem();
                petList.remove(sunm.getId());
                sunm.deleteMe();
            }
        }
    }

    public L1DragonArmorChange getDragonArmorChange() {
        return dragonArmorChange;
    }

    public void quitGame() {
        try {
            LineageAppContext.getCtx().publishEvent(new L1QuitGameEvent(this));

            autoAttack.quit();

            logger.info("[접속종료] : " + getName() + " account=" + getAccountName() + " host=" + client.getIp());

            if (isDead()) {
                int[] loc = GetBackTable.getInstance().getBackLocation(this);
                setX(loc[0]);
                setY(loc[1]);
                setMap((short) loc[2]);
                setCurrentHp(getLevel());
                setFood(39);
            }

            L1Clan clan = L1World.getInstance().getClan(getClanName());

            if (clan != null) {
                ClanWarehouse clanWarehouse = WarehouseManager.getInstance().getClanWarehouse(clan.getClanName());

                if (clanWarehouse != null) {
                    clanWarehouse.unlock(getId());
                }
            }

            if (getTradeID() != 0) {
                L1Trade.cancel(this);
            }

            if (getFightId() != 0) {
                setFightId(0);
                L1PcInstance fightPc = (L1PcInstance) L1World.getInstance().findObject(getFightId());
                if (fightPc != null) {
                    fightPc.setFightId(0);
                    fightPc.sendPackets(new S_PacketBox(L1PacketBoxType.MSG_DUEL, 0, 0));
                }
            }

            if (isInParty()) {
                getParty().leaveMember(this);
            }

            if (isInChatParty()) {
                getChatParty().leaveMember(this);
            }

            returnAllPetAndSummon();

            Collection<L1DollInstance> dollList = getDollList().values();

            for (L1DollInstance doll : dollList) {
                doll.deleteDoll(false);
            }

            for (L1FollowerInstance follower : followerList.values()) {
                follower.setParalyzed(true);
                follower.spawn(follower.getTemplate().getNpcId(), follower.getX(), follower.getY(), follower.getHeading(), follower.getMapId());
                follower.deleteMe();
            }

            for (L1ItemInstance item : inventory.getItems()) {
                ItemTimerScheduler.getInstance().removeEnchant(item);
                ItemTimerScheduler.getInstance().removeEquip(item);
                ItemTimerScheduler.getInstance().removeOwner(item);

                if (item.getPc() != null || item.getEquipPc() != null || item.getItemOwner() != null) {
                    item.setPc(null);
                    item.setEquipPc(null);
                    item.setItemOwner(null);
                }

                if (item.getCount() <= 0) {
                    inventory.deleteItem(item);
                }
            }

            try {
                CharBuffTable.delete(this);
                CharBuffTable.save(this);
                getSkillEffectTimerSet().clearSkillEffectTimer();
            } catch (Exception e) {
                logger.error(e);
            }

            setLogOutTime();
            setOnlineStatus(0);

            try {
                save();
                saveInventory();
            } catch (Exception e) {
                logger.error(e);
            }
        } catch (Exception e) {
            logger.error(e);
        }
    }

    public L1ChatCheck getChatCheck() {
        return chatCheck;
    }

    public void loadExcludes() {
        List<CharacterExclude> excludes = CharacterExcludeTable.getInstance().findByCharId(getId());

        for (CharacterExclude e : excludes) {
            excludingList.add(e.getTargetName());
        }
    }

    private final RankBuff rankBuff = new RankBuff(this);

    public RankBuff getRankBuff() {
        return rankBuff;
    }

    private final ClanRankBuff clanRankBuff = new ClanRankBuff(this);

    public ClanRankBuff getClanRankBuff() {
        return clanRankBuff;
    }

    public void tell() {
        LineageAppContext.commonTaskScheduler().schedule(() -> L1Teleport.teleport(this, getX(), getY(), getMapId(), getHeading(), false), Instant.now().plusMillis(10));
    }

    public int getStatus() {
        return L1CommonUtils.getStatus(this);
    }

    public boolean isMasterPoly(int polyId) {
        return masterPolyIdList.contains(polyId);
    }

    public List<Integer> getMasterPolyIdList() {
        return masterPolyIdList;
    }

    public void loadFavPolyList() {
        favPolyList.clear();
        favPolyList.addAll(FavPolyTable.getInstance().selectList(getId()));
    }

    public void addFavPoly(FavPoly vo) {
        FavPolyTable.getInstance().clear(this);

        if (favPolyList.size() > 2) {
            favPolyList.remove(0);
        }

        if (favPolyList.isEmpty()) {
            favPolyList.add(vo);
            favPolyList.add(vo);
            favPolyList.add(vo);
        } else {
            favPolyList.add(vo);
        }

        FavPolyTable.getInstance().saveAll(favPolyList);
    }

    public List<FavPoly> getFavPolyList() {
        return favPolyList;
    }

    public List<Integer> getFavPolyImgList() {
        List<Integer> result = new ArrayList<>();

        for (FavPoly p : favPolyList) {
            int imgNo = PolyImgTable.getInstance().getImg(p.getPolyId());

            if (imgNo != 0) {
                result.add(imgNo);
            }
        }

        if (favPolyList.size() < 3) {
            for (int i = 0; i < 3 - favPolyList.size(); i++) {
                result.add(1844);
            }
        }

        Collections.reverse(result);

        return result;
    }

    public Map<String, Object> getEtcMap() {
        return etcMap;
    }

    public void addCriticalPer(int i) {
        criticalPer += i;
    }

    public void addBowCriticalPer(int i) {
        bowCriticalPer += i;
    }

    public void addMagicCriticalPer(int i) {
        magicCriticalPer += i;
    }

    public int getCriticalPer() {
        int result = getLevel() / 10;

        result += criticalPer;
        result += L1CalcStat.calcStatCritical(ability.getTotalStr());

        return result;
    }

    public int getBowCriticalPer() {
        int result = getLevel() / 10;

        result += bowCriticalPer;
        result += L1CalcStat.calcStatBowCritical(ability.getTotalDex());

        return result;
    }

    public int getTotalMagicHitUp() {
        int result = getLevel() / 10;

        result += addMagicHitUp;
        result += L1CalcStat.calcStatMagicHitUp(ability.getTotalInt());
        result += L1CalcStat.calcBaseStatMagicHitUp(this);

        return result;
    }

    public int getMagicCriticalPer() {
        int result = getLevel() / 10;

        result += magicCriticalPer;
        result += L1CalcStat.calcStatMagicCritical(ability.getTotalInt());
        result += L1CalcStat.calcBaseStatMagicCriticalPer(this);

        return result;
    }

    public void onDamaged(L1Character attacker) {
        if (attacker instanceof L1PcInstance) {
            autoPotion.damaged(attacker);
            autoAttack.damaged(attacker);

            if (isAutoDragonPerl()) {
                setAutoDragonPerl(false);
                sendPackets("[자동진주기능]이 해제되었습니다.");
            }
        }
    }

    public void addCMAList(String name) {
        if (cmaList.contains(name)) {
            return;
        }
        cmaList.add(name);
    }

    public void removeCMAList(String name) {
        if (!cmaList.contains(name)) {
            return;
        }
        cmaList.remove(name);
    }

    public List<String> getCMAList() {
        return cmaList;
    }

}
