package ks.model.instance;

import ks.app.config.prop.CodeConfig;
import ks.constants.L1ActionCodes;
import ks.constants.L1NpcConstants;
import ks.constants.L1SkillId;
import ks.core.datatables.NPCTalkDataTable;
import ks.core.datatables.NpcChatTable;
import ks.core.datatables.mobskill.MobSkillTable;
import ks.core.datatables.npc.NpcTable;
import ks.model.*;
import ks.model.attack.physics.L1AttackRun;
import ks.model.instance.extend.event.L1DeathEvent;
import ks.model.instance.extend.move.L1AstarMove;
import ks.model.instance.extend.move.L1Move;
import ks.model.pc.L1PcInstance;
import ks.model.skill.L1SkillUse;
import ks.model.types.Point;
import ks.packets.serverpackets.*;
import ks.scheduler.npc.*;
import ks.util.L1CharPosUtils;
import ks.util.common.random.RandomUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.concurrent.ScheduledFuture;

import static ks.constants.L1ItemId.*;
import static ks.util.L1CharPosUtils.checkObject;

public class L1NpcInstance extends L1Character implements L1DeathEvent {
    protected final Logger logger = LogManager.getLogger(getClass());

    private L1Move move = new L1AstarMove();

    public void setMove(L1Move l1MoveAble) {
        this.move = l1MoveAble;
    }

    public L1Move getMove() {
        return move;
    }

    private final List<NpcDeleteScheduler.NpcDeleteCallBack> deleteCallBackList = new ArrayList<>();

    public long HpRegenTime = 0;
    public long mpRegenTime = 0;

    public boolean hprRunning = false;
    public boolean mprRunning = false;

    public long restTime = 0;
    public boolean digestItemRunning = false;
    public boolean destroyed = false;
    public L1NpcChat npcChat = null;
    public long npcChatTime = 0;
    public int npcChatType = 0;
    protected L1Inventory inventory = new L1Inventory();
    protected L1HateList hateList = new L1HateList();
    protected List<L1ItemInstance> targetItemList = new ArrayList<>();
    private L1Character target = null;
    protected L1ItemInstance targetItem = null;
    protected L1PcInstance master = null;
    private L1MobSkillUse mobSkill = new L1MobSkillUse(this);
    private L1MobGroupInfo mobGroupInfo = null;

    private L1Npc template;
    private L1Spawn spawn;
    private int spawnNumber;
    private int petCost;
    private long deleteTime = 0;
    private boolean firstFound = true;
    private boolean rest = false;
    private int randomMoveDistance = 0;
    private int randomMoveDirection = 0;
    private boolean aiRunning = false;
    private boolean activated = false;
    private boolean firstAttack = false;
    private int sleepTime;
    private boolean deathProcessing = false;
    private int mobGroupId = 0;
    private long aiSleepTime = 0;
    private boolean aiCheck = false;

    private Map<Integer, Integer> digestItems;

    private ScheduledFuture<?> spawnScheduleFuture;

    private int addHp;
    private int addHpr;
    private int addMpr;
    private int addMprInterval;
    private int addHprInterval;

    private int passiSpeed;
    private int atkSpeed;
    private boolean pickupItem;

    private String nameId;
    private int homeX;
    private int homeY;
    private boolean reSpawn;
    private int lightSize;
    private boolean weaponBreaking;
    private int hiddenStatus;
    private int movementDistance = 0;
    private int tempLawful = 0;
    private int transformPrevNpcId;

    private boolean ai = true;

    public boolean isAi() {
        return ai;
    }

    public void setAi(boolean ai) {
        this.ai = ai;
    }

    public L1NpcInstance(L1Npc template) {
        super();

        setActionStatus(L1ActionCodes.ACTION_Walk);
        getMoveState().setMoveSpeed(0);
        setDead(false);
        setRespawn(false);

        if (template != null) {
            settingTemplate(template);
        }
    }

    public void onVisiblePcMoved(L1PcInstance visiblePc) {
        move.targetResetting(this, visiblePc);
    }

    public void setTarget(L1Character target) {
        this.target = target;

        move.targetInit(this, this.target);
    }

    public L1Character getTarget() {
        return this.target;
    }


    public long getDeleteTime() {
        return deleteTime;
    }

    public void setDeleteTime(long deleteTime) {
        this.deleteTime = deleteTime;
    }

    public long getAiSleepTime() {
        return aiSleepTime;
    }

    public void setAiSleepTime(long aiSleepTime) {
        this.aiSleepTime = aiSleepTime;
    }

    public boolean isAiCheck() {
        return aiCheck;
    }

    public void setAiCheck(boolean aiCheck) {
        this.aiCheck = aiCheck;
    }

    public List<NpcDeleteScheduler.NpcDeleteCallBack> getDeleteCallBackList() {
        return deleteCallBackList;
    }

    public void toMoveDirection(int dir) {
        directionMove(dir);
        getMap().setPassable(getX(), getY(), true);
    }

    private int calcRandomVal(int random, int val) {
        int ran;

        if (random > 0) {
            if (val > 0) {
                ran = RandomUtils.nextInt(random - val) + 1;
            } else {
                ran = RandomUtils.nextInt(random) + 1;
            }
            val += ran;
        } else {
            ran = RandomUtils.nextInt(random * (-1)) + 1;
            val -= ran;
        }

        return val;
    }

    public void addHp(int addHp) {
        this.addHp += addHp;
    }

    public int getHp() {
        return template.getHp() + addHp;
    }

    public void templateInit() {
        addHp = 0;

        addHpr = 0;
        addHprInterval = 0;

        addMpr = 0;
        addMprInterval = 0;

        dmgUp = 0;
        addDmgUp = 0;
        bowDmgUp = 0;
        addBowDmgUp = 0;
        hitUp = 0;
        addHitUp = 0;
        bowHitUp = 0;
        addBowHitUp = 0;


        setOptionHp(0);
    }

    public void settingTemplate(L1Npc template) {
        if (template == null) {
            logger.warn("template is null");
            return;
        }

        this.template = template;

        templateInit();

        if (template.getDamage() > 0) {
            if (getDmgUp() > 0) {
                addDmgUp(-template.getDamage());
            }

            addDmgUp(template.getDamage());
        }

        int diff = 0;

        setName(template.getName());
        setNameId(template.getNameId());

        int level = template.getLevel();
        int randomLevel = template.getRandomLevel();
        if (randomLevel != 0) {
            level = calcRandomVal(randomLevel, level);
            diff = randomLevel - level;
            if (level <= 0)
                level = 1;
        }

        setLevel(level);

        int hp = getHp();

        int randomHp = template.getRandomHp();

        if (randomHp != 0) {
            hp = calcRandomVal(randomHp, hp);

            if (hp <= 0)
                hp = 1;
        }

        setMaxHp(hp);
        setCurrentHp(hp);

        int mp = template.getMp();
        int randomMp = template.getRandomMp();

        if (randomMp != 0) {
            mp = calcRandomVal(randomMp, mp);
            if (mp <= 0)
                mp = 0;
        }
        setMaxMp(mp);
        setCurrentMp(mp);

        int templateAc = template.getAc();
        int randomAc = template.getRandomAc();

        if (randomAc != 0) {
            templateAc = calcRandomVal(randomAc, templateAc);
        }

        ac.setAc(templateAc);

        if (template.getRandomLevel() == 0) {
            ability.setStr(template.getStr());
            ability.setCon(template.getCon());
            ability.setDex(template.getDex());
            ability.setInt(template.get_int());
            ability.setWis(template.getWis());
            resistance.setBaseMr(template.getMr());
        } else {
            ability.setStr((byte) Math.min(template.getStr() + diff, 127));
            ability.setCon((byte) Math.min(template.getCon() + diff, 127));
            ability.setDex((byte) Math.min(template.getDex() + diff, 127));
            ability.setInt((byte) Math.min(template.get_int() + diff, 127));
            ability.setWis((byte) Math.min(template.getWis() + diff, 127));
            resistance.setBaseMr((byte) Math.min(template.getMr() + diff, 127));

            addHitUp((template.getRandomLevel() - level) * 2);
            addDmgUp((template.getRandomLevel() - level) * 2);
        }

        setPassiSpeed(template.getPassispeed());
        setAtkSpeed(template.getAtkspeed());

        gfx.setTempCharGfx(template.getGfxid());
        gfx.setGfxId(template.getGfxid());

        if (template.getRandomExp() == 0) {
            setExp(template.getExp());
        } else {
            int ran = RandomUtils.nextInt(template.getRandomExp()) + template.getExp();
            if (ran >= template.getRandomExp()) {
                ran = template.getRandomExp();
            }
            setExp(ran);
        }

        int lawful = template.getLawful();

        int randomLawful = template.getRandomLawful();

        if (randomLawful != 0) {
            lawful = calcRandomVal(randomLawful, lawful);
        }

        setLawful(lawful);
        setTempLawful(lawful);

        setPickupItem(template.isPicupItem());

        if (template.isBraveSpeed()) {
            getMoveState().setBraveSpeed(1);
        } else {
            getMoveState().setBraveSpeed(0);
        }

        if (template.getDigestItem() > 0) {
            digestItems = new HashMap<>();
        }

        setKarma(template.getKarma());
        setLightSize(template.getLightSize());

        L1MobSkill skillTpl = MobSkillTable.getInstance().getTemplate(getTemplate().getNpcId());

        mobSkill.setMobSkill(skillTpl);
    }

    public L1MobSkillUse getMobSkill() {
        return mobSkill;
    }

    public void setMobSkill(L1MobSkillUse mobSkill) {
        this.mobSkill = mobSkill;
    }

    public void startAI() {
        NpcAIScheduler.getInstance().addNpc(this);
    }

    public boolean toAi() {
        if (!isAi()) {
            return false;
        }

        setSleepTime(300);

        checkTarget();

        if (target == null && master == null) {
            searchTarget();
        }

        onItemUse();

        if (target == null) {
            checkTargetItem();

            if (isPickupItem() && targetItem == null) {
                searchTargetItem();
            }

            if (targetItem == null) {
                return noTarget();
            } else {
                L1Inventory groundInventory = L1World.getInstance().getInventory(targetItem.getX(), targetItem.getY(), targetItem.getMapId());

                if (groundInventory.checkItem(targetItem.getItemId())) {
                    onTargetItem();
                } else {
                    targetItemList.remove(targetItem);
                    targetItem = null;
                    setSleepTime(1000);
                    return false;
                }
            }
        } else {
            if (getHiddenStatus() == L1NpcConstants.HIDDEN_STATUS_NONE) {
                onTarget();
            } else {
                return true;
            }
        }

        return false;
    }

    public void onItemUse() {
    }

    public void searchTarget() {
    }

    @Override
    public boolean isLongAttack() {
        return getTemplate().getRanged() >= 6 && getTemplate().getBowActId() != 0;
    }

    @Override
    public int getTotalHitUp() {
        int result = (int) ((getLevel() / 1.5) + getHitUp());

        L1ItemInstance weapon = getWeapon();

        if (weapon != null && weapon.isEquipped()) {
            result += weapon.getHitUp();
        }

        result += getAbility().getTotalStr();

        return result;
    }

    @Override
    public int getTotalBowHitUp() {
        int result = (int) ((getLevel() / 1.5) + getBowHitUp());

        L1ItemInstance weapon = getWeapon();

        if (weapon != null && weapon.isEquipped()) {
            result += weapon.getBowHitup();
        }

        result += getAbility().getTotalDex();

        return result;
    }

    public void checkTarget() {
        if (target == null
                || target.getMapId() != getMapId()
                || target.isDead()
                || target.getCurrentHp() <= 0
                || (target.isInvisible() && !getTemplate().isAgrocoi() && !hateList.containsKey(target))) {
            if (target != null) {
                targetClear();

                if (move instanceof L1AstarMove) {
                    move.targetRemove(target);
                }
            }

            if (!hateList.isEmpty()) {
                setTarget(hateList.getMaxHateCharacter());
                checkTarget();
            }
        }

        move.validateTarget(this, target);
    }

    public void checkTargetItem() {
        if (targetItem == null
                || targetItem.getMapId() != getMapId()
                || getLocation().getTileDistance(targetItem.getLocation()) > 15) {
            if (!targetItemList.isEmpty()) {
                targetItem = targetItemList.get(0);
                targetItemList.remove(0);
                checkTargetItem();
            } else {
                targetItem = null;
            }
        }
    }


    public void onTarget() {
        setActivated(true);

        targetItemList.clear();
        targetItem = null;

        if (target == null) {
            targetClear();
            return;
        }

        if (target instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) target;

            if (pc.isGmInvis()) {
                targetClear();
                return;
            }
        }
        if (target.isInvisible() && !getTemplate().isAgrocoi()) {
            targetClear();
            return;
        }

        if (target.getMapId() != getMapId()) {
            targetClear();
            return;
        }

        if (target != null) {
            L1PcInstance realTarget = L1World.getInstance().getPlayer(target.getName());

            if (realTarget != null) {
                if (realTarget.getMapId() != getMapId()) {
                    targetClear();
                    return;
                }
            }
        }

        int escapeDistance = CodeConfig.MOB_ESCAPE_DISTANCE;

        if (getMaxHp() < 4300) {
            if (getSkillEffectTimerSet().hasSkillEffect(L1SkillId.DARKNESS)
                    || getSkillEffectTimerSet().hasSkillEffect(L1SkillId.CURSE_BLIND)) {
                escapeDistance = 1;
            }
        }

        int targetDistance = getLocation().getTileDistance(target.getLocation());

        if (targetDistance > escapeDistance) {
            targetClear();
            return;
        }

        if (targetDistance > CodeConfig.MON_TARGET_CHANGE_DISTANCE) {
            L1Character tempMinChar = null;
            int tempMinDistance = Integer.MAX_VALUE;

            for (L1Character ch : hateList.toTargetList()) {
                if (ch instanceof L1PcInstance) {
                    L1PcInstance pc = (L1PcInstance) ch;

                    if (ch.equals(target))
                        continue;

                    if (pc.isInvisible() && !getTemplate().isAgrocoi()) {
                        continue;
                    }

                    int dis = ch.getLocation().getTileDistance(getLocation());

                    if (dis < tempMinDistance) {
                        tempMinDistance = dis;
                        tempMinChar = ch;
                    }
                }
            }

            if (tempMinChar != null) {
                if (!tempMinChar.equals(target)) {
                    setTarget(L1World.getInstance().getPlayer(tempMinChar.getName()));
                }
            }
        }


        if (getAtkSpeed() == 0 && getPassiSpeed() > 0) {
            int dir = targetReverseDirection(target.getX(), target.getY());
            dir = checkObject(getX(), getY(), getMapId(), dir);

            directionMove(dir);
            setSleepTime(calcSleepTime(getPassiSpeed(), L1NpcConstants.MOVE_SPEED));
        } else {
            if (L1CharPosUtils.isAttackPosition(this, target, getTemplate().getRanged())) {
                move.targetResetting(this, target);

                boolean trigger = mobSkill.isSkillTrigger(target);

                if (trigger) {
                    if (RandomUtils.isWinning(100, 50)) {
                        setHeading(L1CharPosUtils.targetDirection(this, target.getX(), target.getY()));
                        attackTarget(target);
                    } else {
                        if (mobSkill.skillUse(target, true)) {
                            setSleepTime(calcSleepTime(mobSkill.getSleepTime(), L1NpcConstants.MAGIC_SPEED));
                        } else {
                            setHeading(L1CharPosUtils.targetDirection(this, target.getX(), target.getY()));
                            attackTarget(target);
                        }
                    }
                } else {
                    setHeading(L1CharPosUtils.targetDirection(this, target.getX(), target.getY()));
                    attackTarget(target);
                }
            } else {
                if (mobSkill.skillUse(target, true)) {
                    setSleepTime(calcSleepTime(mobSkill.getSleepTime(), L1NpcConstants.MAGIC_SPEED));
                    return;
                }

                if (getPassiSpeed() > 0) {
                    int distance = getLocation().getTileDistance(target.getLocation());

                    if (firstFound && getTemplate().isTeleport() && distance > 3 && distance < 15) {
                        if (nearTeleport(target.getX(), target.getY())) {
                            firstFound = false;
                            return;
                        }
                    }

                    //가까이 텔레포트사용이 가능한 엔피씨
                    if (getTemplate().isTeleport() && 20 > RandomUtils.nextInt(100)
                            && getCurrentMp() >= 10
                            && (distance > 6 && distance < 15)) {
                        if (nearTeleport(target.getX(), target.getY())) {
                            return;
                        }
                    }

                    int dir = move.calcDirection(this, target);

                    if (dir == -1) {
                        targetClear();
                    } else {
                        directionMove(dir);
                        setSleepTime(calcSleepTime(getPassiSpeed(), L1NpcConstants.MOVE_SPEED));
                    }
                } else {
                    targetClear();
                }
            }
        }
    }

    public void setHate(L1Character cha, int hate) {
        if (cha != null) {
            if (cha.getId() != getId()) {
                if (!isFirstAttack() && hate != 0) {
                    hate *= 2;
                    this.firstAttack = true;
                }

                hateList.add(cha, hate);

                this.target = hateList.getMaxHateCharacter();
                move.targetInit(this, this.target);

                checkTarget();
            }
        }
    }

    public void setLink(L1Character cha) {
    }

    public void searchLink(L1PcInstance targetPlayer, int family) {
        List<L1Object> targetKnownObjects = targetPlayer.getNearObjects().getKnownObjects();

        for (Object knownObject : targetKnownObjects) {
            if (knownObject == null)
                continue;

            if (knownObject instanceof L1NpcInstance) {
                L1NpcInstance npc = (L1NpcInstance) knownObject;

                L1MobGroupInfo mobGroupInfo = getMobGroupInfo();

                if (mobGroupInfo != null) {
                    if (getMobGroupId() != 0 && getMobGroupId() == npc.getMobGroupId()) {
                        npc.setLink(targetPlayer);
                    }
                }

                L1Npc tpl = npc.getTemplate();

                if (tpl == null)
                    continue;

                if (npc.getTemplate().getAgroFamily() > 0) {
                    if (npc.getTemplate().getAgroFamily() == 1) {
                        if (npc.getTemplate().getFamily() == family) {
                            npc.setLink(targetPlayer);
                        }
                    } else {
                        npc.setLink(targetPlayer);
                    }
                }
            }
        }
    }

    public void attackTarget(L1Character target) {
        if (target == null)
            return;
        if (target instanceof L1PcInstance) {
            L1PcInstance player = (L1PcInstance) target;
            if (player.isTeleport()) {
                return;
            }
        } else if (target instanceof L1PetInstance) {
            L1PetInstance pet = (L1PetInstance) target;
            L1Character cha = pet.getMaster();
            if (cha instanceof L1PcInstance) {
                L1PcInstance player = (L1PcInstance) cha;
                if (player.isTeleport()) {
                    return;
                }
            }
        } else if (target instanceof L1SummonInstance) {
            L1SummonInstance summon = (L1SummonInstance) target;
            L1Character cha = summon.getMaster();
            if (cha instanceof L1PcInstance) {
                L1PcInstance player = (L1PcInstance) cha;
                if (player.isTeleport()) {
                    return;
                }
            }
        }
        if (this instanceof L1PetInstance) {
            L1PetInstance pet = (L1PetInstance) this;
            L1Character cha = pet.getMaster();
            if (cha instanceof L1PcInstance) {
                L1PcInstance player = (L1PcInstance) cha;
                if (player.isTeleport()) {
                    return;
                }
            }
        } else if (this instanceof L1SummonInstance) {
            L1SummonInstance summon = (L1SummonInstance) this;
            L1Character cha = summon.getMaster();
            if (cha instanceof L1PcInstance) {
                L1PcInstance player = (L1PcInstance) cha;
                if (player.isTeleport()) {
                    return;
                }
            }
        }

        if (target instanceof L1NpcInstance) {
            L1NpcInstance npc = (L1NpcInstance) target;
            if (npc.getHiddenStatus() != L1NpcConstants.HIDDEN_STATUS_NONE) {
                allTargetClear();
                return;
            }
        }

        L1AttackRun attack = new L1AttackRun(this, target);
        attack.action();
        attack.commit();

        setSleepTime(calcSleepTime(getAtkSpeed(), L1NpcConstants.ATTACK_SPEED));
    }

    public void searchTargetItem() {
        List<L1GroundInventory> groundInventories = new ArrayList<>();
        List<L1Object> list = L1World.getInstance().getVisibleObjects(this);

        for (L1Object obj : list) {
            if (obj == null)
                continue;
            if (obj instanceof L1GroundInventory) {
                groundInventories.add((L1GroundInventory) obj);
            }
        }

        if (groundInventories.size() == 0) {
            return;
        }

        int pickupIndex = (int) (Math.random() * groundInventories.size());
        L1GroundInventory inventory = groundInventories.get(pickupIndex);
        List<L1ItemInstance> _itemList = inventory.getItems();

        for (L1ItemInstance item : _itemList) {
            if (item == null)
                continue;
            if (item.getItem().getItemId() == 40308)
                continue;
            if (getInventory().checkAddItem(item, item.getCount()) == L1Inventory.OK) {
                targetItem = item;
                targetItemList.add(targetItem);
            }
        }
    }

    public void onTargetItem() {
        if (getLocation().getTileLineDistance(targetItem.getLocation()) == 0) {
            pickupTargetItem(targetItem);
        } else {
            int dir = moveDirection(targetItem.getX(), targetItem.getY());
            if (dir == -1) {
                targetItemList.remove(targetItem);
                targetItem = null;
            } else {
                directionMove(dir);
                setSleepTime(calcSleepTime(getPassiSpeed(), L1NpcConstants.MOVE_SPEED));
            }
        }
    }

    public void pickupTargetItem(L1ItemInstance targetItem) {
        L1Inventory groundInventory = L1World.getInstance().getInventory(targetItem.getX(), targetItem.getY(), targetItem.getMapId());
        L1ItemInstance item = groundInventory.tradeItem(targetItem, targetItem.getCount(), getInventory());
        light.turnOnOffLight();
        onGetItem(item);
        targetItemList.remove(this.targetItem);
        this.targetItem = null;
        setSleepTime(1000);
    }

    public void directionMove(int dir) {
        L1CharPosUtils.setDirectionMove(this, dir);

        if (getMovementDistance() > 0) {
            if (this instanceof L1GuardInstance
                    || this instanceof L1CastleGuardInstance
                    || this instanceof L1MerchantInstance
                    || this instanceof L1MonsterInstance) {
                if (getLocation().getLineDistance(new Point(getHomeX(), getHomeY())) > getMovementDistance()) {
                    teleport(getHomeX(), getHomeY(), getHeading());
                }
            }
        }

        if (getNpcId() >= 45912 && getNpcId() <= 45916) {
            if (!(getX() >= 32597 && getY() >= 32650 && getX() <= 32634 && getY() <= 32689 && getMapId() == 4)) {
                teleport(getHomeX(), getHomeY(), getHeading());
            }
        }
    }

    //타겟이 주변에 아무도 없는경우
    public boolean noTarget() {
        if (master != null) {
            if (master.getMapId() == getMapId() && getLocation().getTileLineDistance(master.getLocation()) > 2) {
                int dir = moveDirection(master.getX(), master.getY());

                if (dir != -1) {
                    directionMove(dir);
                    setSleepTime(calcSleepTime(getPassiSpeed(), L1NpcConstants.MOVE_SPEED));
                } else {
                    return true;
                }
            }
        } else {
            List<L1PcInstance> players = L1World.getInstance().getRecognizePlayer(this);

            if (players.isEmpty()) {
                return true;
            }

            if (getPassiSpeed() > 0 && !isRest()) {
                L1MobGroupInfo mobGroupInfo = getMobGroupInfo();

                if (mobGroupInfo == null || mobGroupInfo.isLeader(this)) {
                    if (randomMoveDistance == 0) {
                        randomMoveDistance = RandomUtils.nextInt(5) + 1;
                        randomMoveDirection = RandomUtils.nextInt(20);

                        if (getHomeX() != 0 && getHomeY() != 0 && randomMoveDirection < 8 && RandomUtils.nextInt(3) == 0) {
                            randomMoveDirection = moveDirection(getHomeX(), getHomeY());
                        }
                    } else {
                        randomMoveDistance--;
                    }

                    int dir = checkObject(getX(), getY(), getMapId(), randomMoveDirection);

                    if (dir != -1) {
                        directionMove(dir);
                        setSleepTime(calcSleepTime(getPassiSpeed(), L1NpcConstants.MOVE_SPEED));
                    }
                } else {
                    L1NpcInstance leader = mobGroupInfo.getLeader();

                    if (getLocation().getTileLineDistance(leader.getLocation()) > 2) {
                        int dir = moveDirection(leader.getX(), leader.getY());
                        if (dir == -1) {
                            return true;
                        } else {
                            directionMove(dir);
                            setSleepTime(calcSleepTime(getPassiSpeed(), L1NpcConstants.MOVE_SPEED));
                        }
                    }
                }
            }
        }

        return false;
    }

    public void onFinalAction(L1PcInstance pc, String s) {
    }

    public void targetClear() {
        hateList.remove(target);
        target = null;
    }

    public void targetRemove(L1Character character) {
        if (hateList != null) {
            hateList.remove(character);
        }

        if (target != null && target.equals(character)) {
            target = null;
        }
    }

    public void allTargetClear() {
        hateList.clear();
        target = null;

        targetItemList.clear();
        targetItem = null;
    }

    public L1Character getMaster() {
        return master;
    }

    public void setMaster(L1PcInstance cha) {
        master = cha;
    }

    public void onNpcAI() {
    }

    @Override
    public void onAction(L1PcInstance pc) {
        L1AttackRun attack = new L1AttackRun(pc, this);
        attack.action();
    }

    @Override
    public void onTalkAction(L1PcInstance player) {
        if (player == null)
            return;

        int objId = getId();

        L1NpcTalkData talking = NPCTalkDataTable.getInstance().getTemplate(getTemplate().getNpcId());

        if (player.getLawful() < -1000) {
            player.sendPackets(new S_NPCTalkReturn(talking, objId, 2));
        } else {
            player.sendPackets(new S_NPCTalkReturn(talking, objId, 1));
        }
    }

    public void refineItem() {
        int[] materials;
        int[] counts;
        int[] createitem;
        int[] createcount = new int[]{1};

        if (template.getNpcId() == 45032) {
            if (getExp() != 0 && !inventory.checkItem(20)) {
                materials = new int[]{40508, 40521, 40045};
                counts = new int[]{150, 3, 3};
                createitem = new int[]{20};

                extractRefine(materials, counts, createitem, createcount);
            }

            if (getExp() != 0 && !inventory.checkItem(19)) {
                materials = new int[]{40494, 40521};
                counts = new int[]{150, 3};
                createitem = new int[]{19};
                extractRefine(materials, counts, createitem, createcount);
            }

            if (getExp() != 0 && !inventory.checkItem(3)) {
                materials = new int[]{40494, 40521};
                counts = new int[]{50, 1};
                createitem = new int[]{3};

                extractRefine(materials, counts, createitem, createcount);
            }

            if (getExp() != 0 && !inventory.checkItem(100)) {
                materials = new int[]{88, 40508, 40045};
                counts = new int[]{4, 80, 3};
                createitem = new int[]{100};
                extractRefine(materials, counts, createitem, createcount);
            }

            if (getExp() != 0 && !inventory.checkItem(89)) {
                materials = new int[]{88, 40494};
                counts = new int[]{2, 80};
                createitem = new int[]{89};

                if (inventory.checkItem(materials, counts)) {
                    for (int i = 0; i < materials.length; i++) {
                        inventory.consumeItem(materials[i], counts[i]);
                    }
                    for (int j = 0; j < createitem.length; j++) {
                        L1ItemInstance item = inventory.storeItem(createitem[j], createcount[j]);

                        if (getTemplate().getDigestItem() > 0) {
                            setDigestItem(item);
                        }
                    }
                }
            }
        } else if (template.getNpcId() == 81069) {
            if (getExp() != 0 && !inventory.checkItem(40542)) {
                materials = new int[]{40032};
                counts = new int[]{1};
                createitem = new int[]{40542};
                extractRefine(materials, counts, createitem, createcount);
            }
        }
    }

    private void extractRefine(int[] materials, int[] counts, int[] createItem, int[] createCount) {
        if (inventory.checkItem(materials, counts)) {
            for (int i = 0; i < materials.length; i++) {
                inventory.consumeItem(materials[i], counts[i]);
            }
            for (int j = 0; j < createItem.length; j++) {
                inventory.storeItem(createItem[j], createCount[j]);
            }
        }
    }

    public void setParalysisTime(int ptime) {
        aiSleepTime = System.currentTimeMillis() + ptime;
    }

    public L1HateList getHateList() {
        return hateList;
    }

    public final void startHpRegeneration() {
        int hprInterval = getTemplate().getHprInterval();
        int hpr = getTemplate().getHpr();

        if (!hprRunning && hprInterval > 0 && hpr > 0) {
            HpRegenTime = hprInterval + System.currentTimeMillis();
            hprRunning = true;
            NpcHPScheduler.getInstance().add(this);
        }
    }

    public final void stopHpRegeneration() {
        if (hprRunning) {
            hprRunning = false;
            NpcHPScheduler.getInstance().remove(this);
        }
    }

    public final void startMpRegeneration() {
        int mprInterval = getTemplate().getMprInterval();
        int mpr = getTemplate().getMpr();
        if (!mprRunning && mprInterval > 0 && mpr > 0) {
            mpRegenTime = mprInterval + System.currentTimeMillis();
            mprRunning = true;
            NpcMPScheduler.getInstance().add(this);
        }
    }

    public final void stopMpRegeneration() {
        if (mprRunning) {
            hprRunning = false;
            NpcMPScheduler.getInstance().remove(this);
        }
    }

    public void addDeleteCallBack(NpcDeleteScheduler.NpcDeleteCallBack callBack) {
        if (!deleteCallBackList.contains(callBack))
            deleteCallBackList.add(callBack);
    }

    public int getMpr() {
        int result = getTemplate().getMpr();

        result += addMpr;

        return result;
    }

    public int getMprInterval() {
        int result = getTemplate().getMprInterval();

        result += addMprInterval;

        return result;
    }

    public int getHprInterval() {
        int result = getTemplate().getHprInterval();

        result += addHprInterval;

        return result;
    }

    public int getHpr() {
        int result = getTemplate().getHpr();

        result += addHpr;

        return result;
    }

    public void addHpr(int i) {
        addHpr += i;
    }

    public void addMpr(int i) {
        addMpr += i;
    }

    public void addMprInterval(int i) {
        addMprInterval += i;
    }

    public void addHprInterval(int i) {
        addHprInterval += i;
    }

    public int getPassiSpeed() {
        return passiSpeed;
    }

    public void setPassiSpeed(int i) {
        passiSpeed = i;
    }

    public int getAtkSpeed() {
        return atkSpeed;
    }

    public void setAtkSpeed(int i) {
        atkSpeed = i;
    }

    public boolean isPickupItem() {
        return pickupItem;
    }

    public void setPickupItem(boolean flag) {
        pickupItem = flag;
    }

    @Override
    public L1Inventory getInventory() {
        return inventory;
    }

    public void setInventory(L1Inventory inventory) {
        this.inventory = inventory;
    }

    public L1Npc getTemplate() {
        return template;
    }

    public int getNpcId() {
        return template.getNpcId();
    }

    public int getPetCost() {
        return petCost;
    }

    public void setPetCost(int i) {
        petCost = i;
    }

    public L1Spawn getSpawn() {
        return spawn;
    }

    public void setSpawn(L1Spawn spawn) {
        this.spawn = spawn;
    }

    public int getSpawnNumber() {
        return spawnNumber;
    }

    public void setSpawnNumber(int number) {
        spawnNumber = number;
    }

    public void respawn(boolean isReuseId) {
        int id = isReuseId ? getId() : 0;
        spawnScheduleFuture = spawn.respawn(spawnNumber, id);
    }

    @Override
    public void onPerceive(L1PcInstance perceivedFrom) {
        if (perceivedFrom == null)
            return;

        perceivedFrom.getNearObjects().addKnownObject(this);
        perceivedFrom.sendPackets(new S_NPCPack(this));

        onNpcAI();

        if (getMapId() == 90) {
            getMap().setAttackAble(getX(), getY(), false);
        }
    }

    @Override
    public void receiveManaDamage(L1Character attacker, int damageMp) {
    }

    @Override
    public void receiveDamage(L1Character attacker, int damage) {
    }

    public void setDigestItem(L1ItemInstance item) {
        if (item == null) {
            return;
        }

        digestItems.put(item.getId(), getTemplate().getDigestItem());

        if (!digestItemRunning) {
            new Timer().schedule(new DigestItemTimer(), 0);
        }
    }

    public void onGetItem(L1ItemInstance item) {
        refineItem();
        getInventory().shuffle();
        if (getTemplate().getDigestItem() > 0) {
            setDigestItem(item);
        }
    }

    public void approachPlayer(L1PcInstance pc) {
        if (pc == null)
            return;

        if (pc.getSkillEffectTimerSet().hasSkillEffect(60) || pc.getSkillEffectTimerSet().hasSkillEffect(97)) {
            return;
        }

        if (getHiddenStatus() == L1NpcConstants.HIDDEN_STATUS_SINK) {
            if (getCurrentHp() == getMaxHp()) {
                if (pc.getLocation().getTileLineDistance(this.getLocation()) <= 2) {
                    appearOnGround(pc);
                }
            }
        } else if (getHiddenStatus() == L1NpcConstants.HIDDEN_STATUS_FLY) {
            if (pc.getLocation().getTileLineDistance(this.getLocation()) <= 1) {
                appearOnGround(pc);
            }
        }
    }

    public void appearOnGround(L1Character pc) {
        if (pc == null)
            return;

        if (getHiddenStatus() == L1NpcConstants.HIDDEN_STATUS_SINK) {
            Broadcaster.broadcastPacket(this, new S_DoActionGFX(getId(), L1ActionCodes.ACTION_Appear));
            appearOnGroundAction(pc);
        } else if (getHiddenStatus() == L1NpcConstants.HIDDEN_STATUS_FLY) {
            Broadcaster.broadcastPacket(this, new S_DoActionGFX(getId(), L1ActionCodes.ACTION_Movedown));
            appearOnGroundAction(pc);
            startChat(L1NpcConstants.CHAT_TIMING_HIDE);
        }
    }

    private void appearOnGroundAction(L1Character pc) {
        setHiddenStatus(L1NpcConstants.HIDDEN_STATUS_NONE);
        setActionStatus(0);
        Broadcaster.broadcastPacket(this, new S_NPCPack(this));

        if (!pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.INVISIBILITY) && !pc.getSkillEffectTimerSet().hasSkillEffect(97)) {
            hateList.add(pc, 0);
            setTarget(pc);
        }

        onNpcAI();
    }

    public int moveDirection(int x, int y) {
        return L1CharPosUtils.calcMoveDirection(this, x, y);
    }

    public int targetReverseDirection(int tx, int ty) {
        int dir = L1CharPosUtils.targetDirection(this, tx, ty);

        dir += 4;

        if (dir > 7) {
            dir -= 8;
        }

        return dir;
    }

    private void useHealPotion(int healHp, int effectId) {
        Broadcaster.broadcastPacket(this, new S_SkillSound(getId(), effectId));
        if (this.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.POLLUTE_WATER)) {
            healHp /= 2;
        }
        if (this instanceof L1PetInstance) {
            this.setCurrentHp(getCurrentHp() + healHp);
        } else if (this instanceof L1SummonInstance) {
            this.setCurrentHp(getCurrentHp() + healHp);
        } else {
            setCurrentHp(getCurrentHp() + healHp);
        }
    }

    public void useHastePotion(int time) {
        Broadcaster.broadcastPacket(this, new S_SkillHaste(getId(), 1, time));
        Broadcaster.broadcastPacket(this, new S_SkillSound(getId(), 191));
        getMoveState().setMoveSpeed(1);

        getSkillEffectTimerSet().setSkillEffect(L1SkillId.STATUS_HASTE, time * 1000);
    }

    public void useItem(int type, int chance) {
        if (getSkillEffectTimerSet().hasSkillEffect(71)) {
            return;
        }

        if (RandomUtils.nextInt(100) > chance) {
            return;
        }

        if (type == L1NpcConstants.USEITEM_HEAL) {
            if (getInventory().consumeItem(POTION_OF_GREATER_HEALING, 1)) {
                useHealPotion(75, 197);
            } else if (getInventory().consumeItem(POTION_OF_EXTRA_HEALING, 1)) {
                useHealPotion(45, 194);
            } else if (getInventory().consumeItem(POTION_OF_HEALING, 1)) {
                useHealPotion(15, 189);
            }
        } else if (type == L1NpcConstants.USEITEM_HASTE) {
            if (getSkillEffectTimerSet().hasSkillEffect(1001)) {
                return;
            }

            if (getInventory().consumeItem(B_POTION_OF_GREATER_HASTE_SELF, 1)) {
                useHastePotion(2100);
            } else if (getInventory().consumeItem(POTION_OF_GREATER_HASTE_SELF,
                    1)) {
                useHastePotion(1800);
            } else if (getInventory().consumeItem(B_POTION_OF_HASTE_SELF, 1)) {
                useHastePotion(350);
            } else if (getInventory().consumeItem(POTION_OF_HASTE_SELF, 1)) {
                useHastePotion(300);
            }
        }
    }

    public boolean nearTeleport(int nx, int ny) {
        int rDir = RandomUtils.nextInt(8);
        int dir;
        for (int i = 0; i < 8; i++) {
            dir = rDir + i;

            if (dir > 7) {
                dir -= 8;
            }

            switch (dir) {
                case 1:
                    nx++;
                    ny--;
                    break;
                case 2:
                    nx++;
                    break;
                case 3:
                    nx++;
                    ny++;
                    break;
                case 4:
                    ny++;
                    break;
                case 5:
                    nx--;
                    ny++;
                    break;
                case 6:
                    nx--;
                    break;
                case 7:
                    nx--;
                    ny--;
                    break;
                case 0:
                    ny--;
                    break;
                default:
                    break;
            }

            if (getMap().isPassable(nx, ny)) {
                dir += 4;
                if (dir > 7) {
                    dir -= 8;
                }
                teleport(nx, ny, dir);
                setCurrentMp(getCurrentMp() - 10);
                return true;
            }
        }

        return false;
    }

    public void sendPackets(ServerBasePacket serverbasepacket) {
    }

    public void teleport(int nx, int ny, int dir) {
        targetClear();

        teleport(nx, ny, dir, true);
    }

    public void teleport(int nx, int ny, int dir, boolean effect) {
        List<L1PcInstance> list = L1World.getInstance().getRecognizePlayer(this);

        for (L1PcInstance pc : list) {
            if (pc == null)
                continue;

            if (effect) {
                pc.sendPackets(new S_SkillSound(getId(), 169));
            }

            pc.sendPackets(new S_RemoveObject(this));
            pc.getNearObjects().removeKnownObject(this);
        }

        setX(nx);
        setY(ny);
        setHeading(dir);
    }

    public String getNameId() {
        return nameId;
    }

    public void setNameId(String s) {
        nameId = s;
    }

    public int getHomeX() {
        return homeX;
    }

    public void setHomeX(int i) {
        homeX = i;
    }

    public int getHomeY() {
        return homeY;
    }

    public void setHomeY(int i) {
        homeY = i;
    }

    public boolean isReSpawn() {
        return reSpawn;
    }

    public void setRespawn(boolean flag) {
        reSpawn = flag;
    }

    public int getLightSize() {
        return lightSize;
    }

    public void setLightSize(int i) {
        lightSize = i;
    }

    public boolean isWeaponBreaking() {
        return weaponBreaking;
    }

    public void setWeaponBreaking(boolean flag) {
        weaponBreaking = flag;
    }

    public int getHiddenStatus() {
        return hiddenStatus;
    }

    public void setHiddenStatus(int i) {
        hiddenStatus = i;
    }

    public int getMovementDistance() {
        return movementDistance;
    }

    public void setMovementDistance(int i) {
        movementDistance = i;
    }

    public int getTempLawful() {
        return tempLawful;
    }

    public void setTempLawful(int i) {
        tempLawful = i;
    }

    protected int calcSleepTime(int sleepTime, int type) {
        switch (getMoveState().getMoveSpeed()) {
            case 0:
                break;
            case 1:
                sleepTime -= (sleepTime * 0.25);
                break;
            case 2:
                sleepTime *= 2;
                break;
        }

        if (getMoveState().getBraveSpeed() == 1) {
            sleepTime -= (sleepTime * 0.25);
        }

        if (getSkillEffectTimerSet().hasSkillEffect(L1SkillId.WIND_SHACKLE)) {
            if (type == L1NpcConstants.ATTACK_SPEED || type == L1NpcConstants.MAGIC_SPEED) {
                sleepTime += (sleepTime * 0.25);
            }
        }

        return sleepTime;
    }

    public boolean isAiRunning() {
        return aiRunning;
    }

    public void setAiRunning(boolean aiRunning) {
        this.aiRunning = aiRunning;
    }

    protected boolean isActivated() {
        return activated;
    }

    protected void setActivated(boolean activated) {
        this.activated = activated;
    }

    protected boolean isFirstAttack() {
        return firstAttack;
    }

    public int getSleepTime() {
        return sleepTime;
    }

    public void setSleepTime(int sleep_time) {
        sleepTime = sleep_time;
    }

    public boolean isDeathProcessing() {
        return deathProcessing;
    }

    public void setDeathProcessing(boolean deathProcessing) {
        this.deathProcessing = deathProcessing;
    }

    public int getTransformPrevNpcId() {
        return transformPrevNpcId;
    }

    public void setTransformPrevNpcId(int transformPrevNpcId) {
        this.transformPrevNpcId = transformPrevNpcId;
    }

    protected void transform(int transformId) {
        stopHpRegeneration();
        stopMpRegeneration();

        int transformGfxId = getTemplate().getTransformGfxId();

        if (transformGfxId != 0) {
            Broadcaster.broadcastPacket(this, new S_SkillSound(getId(), transformGfxId));
        }

        L1Npc npcTemplate = NpcTable.getInstance().getTemplate(transformId);

        settingTemplate(npcTemplate);

        Broadcaster.broadcastPacket(this, new S_ChangeShape(getId(), getGfxId().getTempCharGfx()));

        List<L1PcInstance> list = L1World.getInstance().getRecognizePlayer(this);

        for (L1PcInstance pc : list) {
            if (pc != null) {
                onPerceive(pc);
            }
        }

        startHpRegeneration();
        startMpRegeneration();
    }

    public boolean isRest() {
        return rest;
    }

    public void setRest(boolean rest) {
        this.rest = rest;
    }

    @Override
    public synchronized void resurrect(int hp) {
        if (destroyed) {
            return;
        }

        super.resurrect(hp);

        startHpRegeneration();
        startMpRegeneration();

        L1SkillUse skill = new L1SkillUse(this, L1SkillId.CANCELLATION, getId(), getX(), getY(), 0, L1SkillUse.TYPE_LOGIN);
        skill.run();

        NpcDeleteScheduler.getInstance().removeNpcDelete(this);
    }

    public synchronized void startDeleteTimer(long time) {
        NpcDeleteScheduler.getInstance().addNpcDelete(this, time);
    }

    public synchronized void startDeleteTimer() {
        startDeleteTimer(L1NpcConstants.DEFAULT_DELETE_TIME);
    }

    public synchronized void stopDeleteTimer() {
        NpcDeleteScheduler.getInstance().removeNpcDelete(this);
    }

    public L1MobGroupInfo getMobGroupInfo() {
        return mobGroupInfo;
    }

    public void setMobGroupInfo(L1MobGroupInfo m) {
        mobGroupInfo = m;
    }

    public int getMobGroupId() {
        return mobGroupId;
    }

    public void setMobGroupId(int i) {
        mobGroupId = i;
    }

    public void startChat(int chatTiming) {
        if (chatTiming == L1NpcConstants.CHAT_TIMING_APPEARANCE && this.isDead()) {
            return;
        }
        if (chatTiming == L1NpcConstants.CHAT_TIMING_DEAD && !this.isDead()) {
            return;
        }
        if (chatTiming == L1NpcConstants.CHAT_TIMING_HIDE && this.isDead()) {
            return;
        }
        if (chatTiming == L1NpcConstants.CHAT_TIMING_GAME_TIME && this.isDead()) {
            return;
        }

        int npcId = this.getTemplate().getNpcId();

        switch (chatTiming) {
            case L1NpcConstants.CHAT_TIMING_APPEARANCE:
                npcChat = NpcChatTable.getInstance().getTemplateAppearance(npcId);
                break;
            case L1NpcConstants.CHAT_TIMING_DEAD:
                npcChat = NpcChatTable.getInstance().getTemplateDead(npcId);
                break;
            case L1NpcConstants.CHAT_TIMING_HIDE:
                npcChat = NpcChatTable.getInstance().getTemplateHide(npcId);
                break;
            case L1NpcConstants.CHAT_TIMING_GAME_TIME:
                npcChat = NpcChatTable.getInstance().getTemplateGameTime(npcId);
                break;
            default:
                break;
        }

        if (npcChat == null) {
            return;
        }

        NpcAiChatScheduler.getInstance().add(this);
    }

    public void clearInventory() {
        if (getInventory() != null) {
            getInventory().clearItems();
        }
    }

    public void removed() {
        destroyed = true;
        clearInventory();
        master = null;

        L1World.getInstance().removeVisibleObject(this);
        L1World.getInstance().removeObject(this);

        List<L1PcInstance> players = L1World.getInstance().getRecognizePlayer(this);

        for (L1PcInstance pc : players) {
            pc.getNearObjects().removeKnownObject(this);
            pc.sendPackets(new S_RemoveObject(this));
        }

        getNearObjects().removeAllKnownObjects();
    }

    public void deleteMe() {
        try {
            allTargetClear();
            removed();
            stopSpawnTaskSchedule();
        } catch (Exception e) {
            logger.error(e);
        }
    }

    private void stopSpawnTaskSchedule() {
        if (spawnScheduleFuture != null) {
            spawnScheduleFuture.cancel(true);
            spawnScheduleFuture = null;
        }
    }

    @Override
    public void onAroundDeath(L1Character attacker, L1Character deathCharacter) {
        move.targetResetting(attacker, attacker);
    }

    class DigestItemTimer extends TimerTask {
        @Override
        public void run() {
            try {
                digestItemRunning = true;

                while (!destroyed && digestItems.size() > 0) {
                    sleep();

                    Object[] keys = digestItems.keySet().toArray();

                    Integer digestCounter;

                    for (Object o : keys) {
                        Integer key = (Integer) o;
                        digestCounter = digestItems.get(key);
                        digestCounter -= 1;

                        if (digestCounter <= 0) {
                            digestItems.remove(key);

                            L1ItemInstance digestItem = getInventory().getItem(key);

                            if (digestItem != null) {
                                getInventory().removeItem(digestItem, digestItem.getCount());
                            }
                        } else {
                            digestItems.put(key, digestCounter);
                        }
                    }
                }

                digestItemRunning = false;
            } catch (Exception e) {
                logger.error("오류", e);
            }
        }

        public void sleep() {
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                logger.error("오류", e);
            }
        }
    }

    public boolean isExistCharacterBetweenTarget(int dir) {
        if (!(this instanceof L1MonsterInstance)) {
            return false;
        }

        if (target == null) {
            return false;
        }

        int locX = getX();
        int locY = getY();
        int targetX = locX;
        int targetY = locY;

        switch (dir) {
            case 1:
                targetX = locX + 1;
                targetY = locY - 1;
                break;
            case 2:
                targetX = locX + 1;
                break;
            case 3:
                targetX = locX + 1;
                targetY = locY + 1;
                break;
            case 4:
                targetY = locY + 1;
                break;
            case 5:
                targetX = locX - 1;
                targetY = locY + 1;
                break;
            case 6:
                targetX = locX - 1;
                break;
            case 7:
                targetX = locX - 1;
                targetY = locY - 1;
                break;
            case 0:
                targetY = locY - 1;
                break;
            default:
                break;
        }

        for (L1Object object : L1World.getInstance().getVisibleObjects(this, 1)) {
            if (object == null)
                continue;
            if (object instanceof L1PcInstance || object instanceof L1SummonInstance || object instanceof L1PetInstance) {
                L1Character cha = (L1Character) object;

                if (cha.getX() == targetX && cha.getY() == targetY && cha.getMapId() == getMapId()) {
                    hateList.add(cha, 0);
                    setTarget(cha);
                    return true;
                }
            }
        }

        return false;
    }

    public double onAttack(L1Character target, double damage) {
        return damage;
    }

    public void deleteSpawn() {
        destroyed = true;

        if (getInventory() != null) {
            getInventory().clearItems();
        }

        L1World.getInstance().removeVisibleObject(this);
        L1World.getInstance().removeObject(this);
        List<L1PcInstance> list = L1World.getInstance().getRecognizePlayer(this);

        for (L1PcInstance pc : list) {
            if (pc == null)
                continue;

            pc.getNearObjects().removeKnownObject(this);
            pc.sendPackets(new S_RemoveObject(this));
        }

        getNearObjects().removeAllKnownObjects();
    }
}
