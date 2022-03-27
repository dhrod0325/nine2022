package ks.model.skill;

import ks.constants.L1ActionCodes;
import ks.constants.L1SkillIcon;
import ks.core.datatables.SkillsTable;
import ks.model.*;
import ks.model.attack.magic.L1MagicRun;
import ks.model.attack.magic.impl.action.vo.L1MagicActionVo;
import ks.model.attack.utils.L1MagicUtils;
import ks.model.instance.*;
import ks.model.pc.L1PcInstance;
import ks.model.skill.magic.L1Skill;
import ks.model.skill.magic.L1SkillFactory;
import ks.model.skill.magic.L1SkillRequest;
import ks.model.skill.magic.skills.L1SkillFreeze;
import ks.model.skill.magic.skills.L1SkillShockStun;
import ks.model.skill.utils.L1SkillUtils;
import ks.packets.serverpackets.*;
import ks.util.L1CharPosUtils;
import ks.util.L1CommonUtils;
import ks.util.log.L1LogUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static ks.constants.L1SkillId.*;

public class L1SkillUse {
    private final Logger logger = LogManager.getLogger();

    public static final int TYPE_NORMAL = 0;
    public static final int TYPE_LOGIN = 1;
    public static final int TYPE_SPELL_SC = 2;
    public static final int TYPE_NPC_BUFF = 3;
    public static final int TYPE_GM_BUFF = 4;

    private final static List<Integer> NON_ADD_MAGIC_LIST = Arrays.asList(
            CURSE_POISON, CURSE_PARALYZE2, SHAPE_CHANGE,
            BLESSED_ARMOR, HOLY_WEAPON, ENCHANT_WEAPON,
            BLESS_WEAPON, SHADOW_FANG, AREA_OF_SILENCE
    );

    public final L1Skills skill;
    private final int skillType;
    private final L1Character skillUseCharacter;
    private final int skillId;

    private final List<L1Character> targetList = new CopyOnWriteArrayList<>();
    private L1Character targetCharacter;
    private int duration;
    private int targetId;
    private int targetX;
    private int targetY;
    private int itemId;
    private int leverage = 10;

    private final L1SkillRequest skillRequest = new L1SkillRequest();

    public L1SkillUse(L1Character skillUseCharacter, int skillId, int duration, int type) {
        this(skillUseCharacter, skillId, skillUseCharacter.getId(), skillUseCharacter.getX(), skillUseCharacter.getY(), duration, type);
    }

    public L1SkillUse(L1Character skillUseCharacter, int skillId, int targetId, int targetX, int targetY, int duration) {
        this(skillUseCharacter, skillId, targetId, targetX, targetY, duration, TYPE_GM_BUFF);
    }

    public L1SkillUse(L1Character skillUseCharacter, int skillId, int targetId, int targetX, int targetY, int duration, int skillType) {
        this.skill = SkillsTable.getInstance().getTemplate(skillId);

        if (skill == null) {
            throw new NullPointerException();
        }

        if (skillUseCharacter == null) {
            throw new NullPointerException();
        }

        this.skillUseCharacter = skillUseCharacter;

        if (duration == 0) {
            duration = skill.getBuffDuration();
        }

        this.targetId = targetId;
        this.skillId = skillId;
        this.targetX = targetX;
        this.targetY = targetY;
        this.duration = duration;
        this.skillType = skillType;

        if (skill.isTargetNone()) {
            L1Object targetObject = L1World.getInstance().findObject(this.targetId);

            if (targetObject instanceof L1ItemInstance) {
                this.itemId = targetObject.getId();
            }

            this.targetId = skillUseCharacter.getId();
            this.targetX = skillUseCharacter.getX();
            this.targetY = skillUseCharacter.getY();
            this.targetCharacter = skillUseCharacter;
        } else {
            L1Object targetObject = L1World.getInstance().findObject(this.targetId);

            if (targetObject instanceof L1Character) {
                this.targetCharacter = (L1Character) targetObject;
                this.targetX = targetCharacter.getX();
                this.targetY = targetCharacter.getY();
            }
        }

        skillRequest.setSkillUseCharacter(skillUseCharacter);
        skillRequest.setSkillId(skillId);
        skillRequest.setTargetId(targetId);
        skillRequest.setTargetX(targetX);
        skillRequest.setTargetY(targetY);
        skillRequest.setTargetItemId(itemId);
        skillRequest.setDuration(duration);
        skillRequest.setTargetList(targetList);
    }

    private boolean isPk() {
        return targetCharacter instanceof L1PcInstance
                && skill.isTargetAttack()
                && skillUseCharacter.getId() != targetId;
    }

    public void setLeverage(int i) {
        leverage = i;
    }

    public boolean checkUseSkill() {
        if (skill == null) {
            return false;
        }

        if (skillUseCharacter instanceof L1PcInstance) {
            L1Object object = L1World.getInstance().findObject(targetId);

            if (object instanceof L1ItemInstance) {
                L1ItemInstance item = (L1ItemInstance) object;

                if (item.getX() != 0 && item.getY() != 0) {
                    return false;
                }
            }
        }

        boolean checkedResult = true;

        if (skillType == TYPE_NORMAL) {
            int itemConsume = skill.getItemConsumeId();
            int itemConsumeCount = skill.getItemConsumeCount();

            if (skillUseCharacter instanceof L1PcInstance) {
                L1PcInstance pc = (L1PcInstance) skillUseCharacter;

                if (itemConsume > 0) {
                    if (!pc.getInventory().checkItem(itemConsume, itemConsumeCount)) {
                        pc.sendPackets(new S_ServerMessage(299));
                        return false;
                    }
                }
            }

            checkedResult = L1SkillUtils.isNormalSkillUsable(skillUseCharacter, skillId);
        } else if (skillType == TYPE_SPELL_SC) {
            if (skillUseCharacter instanceof L1PcInstance) {
                checkedResult = L1SkillUtils.isSpellScrollUsable(skillUseCharacter, skillId);
            }
        }

        if (!checkedResult) {
            return false;
        }

        if (skillId == FIRE_WALL
                || skillId == LIFE_STREAM
                || skillId == CUBE_BALANCE
                || skillId == CUBE_IGNITION
                || skillId == CUBE_QUAKE
                || skillId == CUBE_SHOCK) {
            return true;
        }

        if (!isSkillTargetAbleInstance()) {
            return false;
        }

        makeTargetList();

        if (targetList.isEmpty()) {
            if (skillUseCharacter instanceof L1NpcInstance) {
                checkedResult = false;
            }
        }

        return checkedResult;
    }

    public boolean isSkillTargetAbleInstance() {
        return targetCharacter instanceof L1PcInstance
                || targetCharacter instanceof L1MonsterInstance
                || targetCharacter instanceof L1SummonInstance
                || targetCharacter instanceof L1PetInstance
                || targetCharacter instanceof L1ScarecrowInstance
                || targetCharacter instanceof L1MerchantInstance;
    }

    public void runConsume() {
        int itemConsume = skill.getItemConsumeId();
        int itemConsumeCount = skill.getItemConsumeCount();

        if (skillUseCharacter instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) skillUseCharacter;

            if (itemConsume > 0) {
                if (!pc.getInventory().checkItem(itemConsume, itemConsumeCount)) {
                    pc.sendPackets(new S_ServerMessage(299));
                    return;
                }
            }

            pc.getInventory().consumeItem(itemConsume, itemConsumeCount);

            if (skillId == FINAL_BURN) {
                pc.setCurrentHp(100);
                pc.setCurrentMp(1);
            } else {
                int current_hp = pc.getCurrentHp() - skill.calcConsumeHp(skillUseCharacter);
                pc.setCurrentHp(current_hp);

                int current_mp = pc.getCurrentMp() - skill.calcConsumeMp(skillUseCharacter);
                pc.setCurrentMp(current_mp);
            }

            int lawful = pc.getLawful() + skill.getLawful();

            if (lawful > 32767) {
                lawful = 32767;
            }

            if (lawful < -32767) {
                lawful = -32767;
            }

            pc.setLawful(lawful);
        } else if (skillUseCharacter instanceof L1NpcInstance) {
            L1NpcInstance npc = (L1NpcInstance) skillUseCharacter;

            int currentHp = npc.getCurrentHp() - skill.calcConsumeHp(skillUseCharacter);
            npc.setCurrentHp(currentHp);

            int currentMp = npc.getCurrentMp() - skill.calcConsumeMp(skillUseCharacter);
            npc.setCurrentMp(currentMp);
        }
    }

    private void unlockTeleport() {
        if (skillId == TELEPORT || skillId == MASS_TELEPORT || skillId == TELEPORT_TO_MOTHER) {
            skillUseCharacter.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK, false));
        }
    }

    public void run() {
        try {
            boolean isUseSkill = checkUseSkill();

            if (!isUseSkill) {
                unlockTeleport();
                return;
            }

            switch (skillType) {
                case TYPE_NORMAL:
                    if (isValidGlanceCheck(targetCharacter) || skill.getArea() > 0 || skill.isTargetNone()) {
                        if (skillUseCharacter instanceof L1PcInstance) {
                            L1PcInstance pc = (L1PcInstance) skillUseCharacter;

                            boolean isDelay = pc.getNoDelayCheck().allDelayCheck();

                            if (isDelay) {
                                return;
                            }

                            pc.getNoDelayCheck().startCheck(skillId);
                        }

                        if (skill.calcConsumeHp(skillUseCharacter) <= -1) {
                            skillUseCharacter.sendPackets(new S_ServerMessage(279));
                            unlockTeleport();
                            return;
                        }

                        if (skill.calcConsumeMp(skillUseCharacter) <= -1) {
                            skillUseCharacter.sendPackets(new S_ServerMessage(278));
                            unlockTeleport();
                            return;
                        }

                        runSkill();
                        sendGrfx(true);
                        sendFailMessage();
                        runConsume();

                        L1SkillUtils.pinkName(skillUseCharacter, targetCharacter, skill);
                    }
                    break;
                case TYPE_LOGIN:
                    runSkill();
                    break;
                case TYPE_SPELL_SC:
                    runSkill();
                    sendGrfx(true);
                    break;
                case TYPE_GM_BUFF:
                    runSkill();
                    sendGrfx(false);
                    break;
                case TYPE_NPC_BUFF:
                    sendGrfx(true);
                    runSkill();
                    break;
                default:
                    break;
            }

        } catch (Exception e) {
            logger.error("오류", e);
        }
    }

    private boolean isTarget(L1Character target) {
        if (!L1SkillUtils.targetCheck(target, skillId)) {
            return false;
        }

        if (skillUseCharacter instanceof L1NpcInstance) {
            if (target instanceof L1PcInstance) {
                if (skill.isTargetAttackAndTypeAttack()) {
                    if (skillUseCharacter instanceof L1SummonInstance) {
                        L1SummonInstance summon = (L1SummonInstance) skillUseCharacter;

                        if (target.getId() == summon.getMaster().getId()) {
                            return false;
                        }
                        if (L1CharPosUtils.isSafeZone(target)) {
                            return false;
                        }
                    } else if (skillUseCharacter instanceof L1PetInstance) {
                        L1PetInstance pet = (L1PetInstance) skillUseCharacter;
                        if (target.getId() == pet.getMaster().getId()) {
                            return false;
                        }
                        if (L1CharPosUtils.isSafeZone(target)) {
                            return false;
                        }
                    }
                }
            } else if (target instanceof L1NpcInstance) {
                if (skill.isTargetAttackAndTypeAttack() && skillUseCharacter instanceof L1MonsterInstance && target instanceof L1MonsterInstance) {
                    return false;
                }
            }
        } else if (skillUseCharacter instanceof L1PcInstance) {
            if (target instanceof L1NpcInstance) {
                if (targetCharacter instanceof L1NpcInstance
                        && !(targetCharacter instanceof L1PetInstance)
                        && !(targetCharacter instanceof L1SummonInstance)
                        && (target instanceof L1PetInstance || target instanceof L1SummonInstance)
                ) {
                    return false;
                }

                if (targetCharacter instanceof L1NpcInstance && !(targetCharacter instanceof L1GuardInstance) && target instanceof L1GuardInstance) {
                    return false;
                }
            } else if (target instanceof L1PcInstance) {
                L1PcInstance skillUsePc = (L1PcInstance) skillUseCharacter;
                L1PcInstance skillTarget = (L1PcInstance) target;

                if ((skill.getTargetTo() & L1Skills.TARGET_TO_CLAN) == L1Skills.TARGET_TO_CLAN) {
                    if (skillUsePc.getClanId() != 0 && (skillUsePc.getClanId() == skillTarget.getClanId())) {
                        return true;
                    }
                }

                boolean check1 = (skillUsePc.getParty() != null && skillUsePc.getParty().isMember(skillTarget));
                boolean check2 = (skill.getTargetTo() & L1Skills.TARGET_TO_PARTY) == L1Skills.TARGET_TO_PARTY;

                if (check1 && check2) {
                    return true;
                }

                if (skill.isTargetAttackAndTypeAttack() && !isPk()) {
                    if (skillId == COUNTER_DETECTION
                            && !L1CharPosUtils.isSafeZone(skillTarget)
                            && (target.getSkillEffectTimerSet().hasSkillEffect(INVISIBILITY) || target.getSkillEffectTimerSet().hasSkillEffect(BLIND_HIDING))
                            && !skillTarget.isGmInvis()
                    ) {
                        return true;
                    }

                    if (skillUsePc.getClanId() != 0 && skillTarget.getClanId() != 0) {
                        Collection<L1War> li = L1World.getInstance().getWarList();

                        for (L1War war : li) {
                            if (war.checkClanInWar(skillUsePc.getClanName())) {
                                if (war.checkClanInSameWar(skillUsePc.getClanName(), skillTarget.getClanName())) {
                                    if (L1CastleLocation.checkInAllWarArea(skillTarget.getX(), skillTarget.getY(), skillTarget.getMapId())) {
                                        return true;
                                    }
                                }
                            }
                        }
                    }

                    return false;
                }
            }
        }

        if (skillUseCharacter.getId() == target.getId()) {
            if (skill.isAttack()) {
                return false;
            }

            if (skillId == HEAL_ALL) {
                return false;
            }

            if (((skill.getTargetTo() & L1Skills.TARGET_TO_PC) == L1Skills.TARGET_TO_PC
                    || (skill.getTargetTo() & L1Skills.TARGET_TO_CLAN) == L1Skills.TARGET_TO_CLAN
                    || (skill.getTargetTo() & L1Skills.TARGET_TO_PARTY) == L1Skills.TARGET_TO_PARTY)) {
                return true;
            }
        }

        if (skillUseCharacter instanceof L1PcInstance && skill.isTargetAttackAndTypeAttack() && !isPk()) {
            if (L1SkillUtils.isPetMaster(skillUseCharacter, target)) {
                return false;
            }
        }

        if (!isValidGlanceCheck(target)) {
            return false;
        }

        if ((skill.getTargetTo() & L1Skills.TARGET_TO_PC) == L1Skills.TARGET_TO_PC && target instanceof L1PcInstance) {
            return true;
        } else if ((skill.getTargetTo() & L1Skills.TARGET_TO_NPC) == L1Skills.TARGET_TO_NPC && target instanceof L1NpcInstance) {
            return true;
        } else if ((skill.getTargetTo() & L1Skills.TARGET_TO_PET) == L1Skills.TARGET_TO_PET && skillUseCharacter instanceof L1PcInstance) {
            if (target instanceof L1SummonInstance) {
                return true;
            }

            if (target instanceof L1PetInstance) {
                L1PetInstance pet = (L1PetInstance) target;

                if (pet.getMaster() != null) {
                    return skillUseCharacter.getId() == pet.getMaster().getId();
                }
            }
        }

        return false;
    }

    private boolean isValidGlanceCheck(L1Character targetCharacter) {
        if (skill.getType() == L1Skills.TYPE_CHANGE || skill.getType() == L1Skills.TYPE_RESTORE) {
            return true;
        }

        if (skillId == FIRE_WALL || skillId == LIFE_STREAM) {
            return true;
        }

        if (skill.isThrough()) {
            return true;
        }

        return L1CharPosUtils.glanceCheck(skillUseCharacter, targetCharacter.getX(), targetCharacter.getY());
    }

    private void makeTargetList() {
        if (skillType == TYPE_LOGIN) {
            addTarget(skillUseCharacter);
            return;
        }

        if (skill.getTargetTo() == L1Skills.TARGET_TO_ME) {
            if (!skill.isTargetAttackAndTypeAttack()) {
                addTarget(skillUseCharacter);
                return;
            }
        }

        if (skill.getRanged() != -1) {
            if (skillUseCharacter.getLocation().getTileLineDistance(targetCharacter.getLocation()) > skill.getRanged()) {
                return;
            }
        } else {
            if (!skillUseCharacter.getLocation().isInScreen(targetCharacter.getLocation())) {
                return;
            }
        }

        if (!skill.isTargetNone()) {
            if (!isTarget(targetCharacter)) {
                return;
            }
        }

        if (skill.getArea() == 0) {
            addTarget(targetCharacter);
        } else {
            if (!skill.isTargetNone()) {
                addTarget(targetCharacter);
            }

            if (!skill.isTargetAttackAndTypeAttack()) {
                addTarget(skillUseCharacter);
            }

            List<L1Object> objects;

            if (skill.getArea() == -1) {
                objects = L1World.getInstance().getVisibleObjects(skillUseCharacter);
            } else {
                objects = L1World.getInstance().getVisibleObjects(targetCharacter, skill.getArea());
            }

            for (L1Object targetObj : objects) {
                if (targetObj == null) {
                    continue;
                }

                if (!(targetObj instanceof L1Character)) {
                    continue;
                }

                L1Character target = (L1Character) targetObj;

                if (!isTarget(target)) {
                    continue;
                }

                addTarget(target);
            }
        }
    }

    private void sendHappenMessage(L1PcInstance pc) {
        int msgID = skill.getSysmsgIdHappen();

        if (msgID > 0) {
            pc.sendPackets(new S_ServerMessage(msgID));
        }
    }

    private void sendFailMessage() {
        if (targetList.isEmpty()) {
            if (!skill.isAttack() && !skill.isTargetNone()) {
                int msgID = skill.getSystemMsgIdFail();
                if (msgID > 0) {
                    skillUseCharacter.sendPackets(new S_ServerMessage(msgID));
                }
            }
        }
    }

    private void addMagicList(L1Character cha, boolean sendIcon) {
        int buffDuration = duration;

        if (NON_ADD_MAGIC_LIST.contains(skillId)) {
            return;
        }

        if (buffDuration == 0) {
            return;
        }

        L1Skill runSkill = L1SkillFactory.create(skillId);

        if (!(runSkill instanceof L1SkillShockStun) && !(runSkill instanceof L1SkillFreeze)) {
            buffDuration *= 1000;
        }

        logger.debug("cha:{},duration:{}", cha.getName(), buffDuration);

        cha.getSkillEffectTimerSet().setSkillEffect(skillId, buffDuration);

        if (cha instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) cha;

            if (sendIcon) {
                sendIcon(pc, buffDuration);
            }

            pc.sendPackets(new S_SPMR(pc));
        }
    }

    private void sendIcon(L1PcInstance pc, int buffDuration) {
        int buffIconDuration = buffDuration / 1000;

        L1Skill runSkill = L1SkillFactory.create(skillId);

        if (runSkill != null) {
            runSkill.sendIcon(pc, buffIconDuration);
        }

        if (skillId == IMMUNE_TO_HARM) {
            pc.sendPackets(new S_SkillIconGFX(L1SkillIcon.IMMUNE_TO_HARM, buffIconDuration));
        }

        pc.sendPackets(new S_OwnCharStatus(pc));
    }

    private void sendGrfx(boolean isSkillAction) {
        int actionId = skill.getActionId();
        int castGfx = skill.getCastGfx();
        int castgfx2 = skill.getCastGfx2();

        if (castgfx2 != 0) {
            if (skillUseCharacter instanceof L1PcInstance) {
                L1PcInstance pc = (L1PcInstance) skillUseCharacter;

                if (isSkillAction) {
                    if (!(!skill.isAttack() && !skill.isTargetNone() && targetList.size() == 0)) {
                        L1CommonUtils.locationEffect(skillUseCharacter, targetX, targetY, castgfx2);
                        sendHappenMessage(pc);
                    }

                    pc.sendPackets(new S_DoActionGFX(pc.getId(), actionId));
                    Broadcaster.broadcastPacket(pc, new S_DoActionGFX(pc.getId(), actionId));
                }

                return;
            }
        }

        if (castGfx == 0) {
            return;
        }

        L1Skill runSkill = L1SkillFactory.create(skillId);

        if (runSkill != null) {
            runSkill.sendGrfx(skillRequest, isSkillAction);

            if (runSkill.getRunSkillState() == L1Skill.STATUS_RETURN) {
                return;
            }
        }

        if (runSkill instanceof L1SkillShockStun) {
            return;
        }

        L1LogUtils.skillLog("skillUser : {},skillId:{} actionId : {}, castGfx : {}, area : {},target:{},type:{}",
                skillUseCharacter.getName(),
                skillId, actionId,
                skill.getCastGfx(),
                skill.getArea(),
                skill.getTarget(),
                skill.getType()
        );

        if (skillUseCharacter instanceof L1PcInstance) {
            L1PcInstance skillUsePc = (L1PcInstance) skillUseCharacter;

            switch (skillId) {
                case FIRE_WALL:
                case LIFE_STREAM: {
                    if (skillId == FIRE_WALL) {
                        skillUsePc.setHeading(L1CharPosUtils.targetDirection(skillUsePc, targetX, targetY));
                        skillUsePc.sendPackets(new S_ChangeHeading(skillUsePc));
                        Broadcaster.broadcastPacket(skillUsePc, new S_ChangeHeading(skillUsePc));
                    }
                }
                break;
            }

            if (targetList.isEmpty() && !skill.isTargetNone()) {
                int tempCharGfx = skillUsePc.getGfxId().getTempCharGfx();

                if (tempCharGfx == 5727 || tempCharGfx == 5730) {
                    actionId = L1ActionCodes.ACTION_SkillBuff;
                } else if (tempCharGfx == 5733 || tempCharGfx == 5736) {
                    actionId = L1ActionCodes.ACTION_Attack;
                }

                if (isSkillAction && actionId > 0) {
                    skillUsePc.sendPackets(new S_DoActionGFX(skillUsePc.getId(), actionId));
                    Broadcaster.broadcastPacket(skillUsePc, new S_DoActionGFX(skillUsePc.getId(), actionId));
                }

                return;
            }

            if (skill.isTargetAttack() && !skill.isTurnUndead()) {
                if (L1SkillUtils.isPcSummonPet(skillUseCharacter, targetCharacter)) {
                    if (L1CharPosUtils.isSafeZone(skillUsePc) || L1CharPosUtils.isSafeZone(targetCharacter) || skillUsePc.checkNonPvP()) {
                        skillUsePc.sendPackets(new S_UseAttackSkill(skillUsePc, 0, castGfx, targetX, targetY, actionId));
                        Broadcaster.broadcastPacket(skillUsePc, new S_UseAttackSkill(skillUsePc, 0, castGfx, targetX, targetY, actionId));
                        return;
                    }
                }

                if (skill.getArea() == 0) {
                    skillUsePc.sendPackets(new S_UseAttackSkill(skillUsePc, targetId, castGfx, targetX, targetY, actionId));
                    Broadcaster.broadcastPacket(skillUsePc, new S_UseAttackSkill(skillUsePc, targetId, castGfx, targetX, targetY, actionId));
                    Broadcaster.broadcastPacketExceptTargetSight(targetCharacter, new S_DoActionGFX(targetId, L1ActionCodes.ACTION_Damage), skillUsePc);
                } else {
                    skillUsePc.sendPackets(new S_RangeSkill(skillUsePc, targetList, castGfx, actionId, S_RangeSkill.TYPE_DIR));
                    Broadcaster.broadcastPacket(skillUsePc, new S_RangeSkill(skillUsePc, targetList, castGfx, actionId, S_RangeSkill.TYPE_DIR));
                }
            } else if (skill.isTargetNone() && skill.isAttack()) {
                for (L1Character target : targetList) {
                    Broadcaster.broadcastPacketExceptTargetSight(target, new S_DoActionGFX(target.getId(), L1ActionCodes.ACTION_Damage), skillUsePc);
                }

                skillUsePc.sendPackets(new S_RangeSkill(skillUsePc, targetList, castGfx, actionId, S_RangeSkill.TYPE_NODIR));
                Broadcaster.broadcastPacket(skillUsePc, new S_RangeSkill(skillUsePc, targetList, castGfx, actionId, S_RangeSkill.TYPE_NODIR));
            } else {
                if (skillId != TELEPORT && skillId != MASS_TELEPORT && skillId != TELEPORT_TO_MOTHER && skillId != TRUE_TARGET) {
                    if (isSkillAction && actionId > 0) {
                        skillUsePc.sendPackets(new S_DoActionGFX(skillUsePc.getId(), skill.getActionId()));
                        Broadcaster.broadcastPacket(skillUsePc, new S_DoActionGFX(skillUsePc.getId(), skill.getActionId()));
                    }

                    if (skillId == COUNTER_MAGIC || skillId == COUNTER_MIRROR) {
                        skillUsePc.sendPackets(new S_SkillSound(targetId, castGfx));
                        Broadcaster.broadcastPacket(skillUsePc, new S_SkillSound(targetId, castGfx));
                    } else if (skillId == COUNTER_BARRIER) {
                        skillUsePc.sendPackets(new S_SkillSound(targetId, castGfx));
                        Broadcaster.broadcastPacket(skillUsePc, new S_SkillSound(targetId, castGfx));
                    } else {
                        switch (skillId) {
                            case BLOODY_SOUL:
                            case BRAVE_MENTAL:
                            case STORM_SHOT:
                            case EYE_OF_STORM:
                                skillUsePc.sendPackets(new S_EffectLocation(skillUsePc.getX(), skillUsePc.getY(), castGfx));
                                Broadcaster.broadcastPacket(skillUsePc, new S_SkillSound(targetId, castGfx));
                                break;
                            default:
                                if (skillId == UNCANNY_DODGE) {
                                    if (skillUsePc.getAC().getAc() <= -100) {
                                        castGfx = 11766;
                                    }
                                }

                                skillUsePc.sendPackets(new S_SkillSound(targetId, castGfx));
                                Broadcaster.broadcastPacket(skillUsePc, new S_SkillSound(targetId, castGfx));

                                break;
                        }
                    }
                }

                for (L1Character cha : targetList) {
                    if (cha instanceof L1PcInstance) {
                        L1PcInstance target = (L1PcInstance) cha;

                        if (target.equals(skillUseCharacter)) {
                            continue;
                        }

                        if (skill.getTargetTo() == L1Skills.TARGET_TO_PARTY) {
                            target.sendPackets(new S_EffectLocation(target.getLocation(), castGfx));
                            Broadcaster.broadcastPacket(target, new S_EffectLocation(target.getLocation(), castGfx));
                            target.sendPackets(new S_OwnCharStatus(target));
                        }
                    }
                }
            }
        } else if (skillUseCharacter instanceof L1NpcInstance) {
            if (skillUseCharacter instanceof L1MerchantInstance) {
                Broadcaster.broadcastPacket(skillUseCharacter, new S_SkillSound(targetId, castGfx));
                return;
            }

            if (targetList.isEmpty() && !skill.isTargetNone()) {
                Broadcaster.broadcastPacket(skillUseCharacter, new S_DoActionGFX(skillUseCharacter.getId(), skill.getActionId()));
                return;
            }

            if (skill.isTargetAttack() && !skill.isTurnUndead()) {
                if (skill.getArea() == 0) {
                    Broadcaster.broadcastPacket(skillUseCharacter, new S_UseAttackSkill(skillUseCharacter, targetId, castGfx, targetX, targetY, actionId));
                    Broadcaster.broadcastPacketExceptTargetSight(targetCharacter, new S_DoActionGFX(targetId, L1ActionCodes.ACTION_Damage), skillUseCharacter);
                } else {
                    for (L1Character target : targetList) {
                        Broadcaster.broadcastPacketExceptTargetSight(target, new S_DoActionGFX(target.getId(), L1ActionCodes.ACTION_Damage), skillUseCharacter);
                    }

                    Broadcaster.broadcastPacket(skillUseCharacter, new S_RangeSkill(skillUseCharacter, targetList, castGfx, actionId, S_RangeSkill.TYPE_DIR));
                }
            } else if (skill.isTargetNone() && skill.isAttack()) {
                Broadcaster.broadcastPacket(skillUseCharacter, new S_RangeSkill(skillUseCharacter, targetList, castGfx, actionId, S_RangeSkill.TYPE_NODIR));
            } else {
                if (skillId != 5 && skillId != MASS_TELEPORT && skillId != 131) {
                    Broadcaster.broadcastPacket(skillUseCharacter, new S_DoActionGFX(skillUseCharacter.getId(), skill.getActionId()));
                    Broadcaster.broadcastPacket(skillUseCharacter, new S_SkillSound(targetId, castGfx));
                }
            }
        }
    }

    private void addTarget(L1Character target) {
        if (!targetList.contains(target)) {
            if (!target.getName().isEmpty()) {
                targetList.add(target);
                L1LogUtils.gmLog(skillUseCharacter, "타겟추가 : {}", target.getName());
            }
        }
    }

    private void removeTarget(L1Character target) {
        targetList.remove(target);
    }

    private void runSkill() {
        L1LogUtils.gmLog(skillUseCharacter, "스킬호출 : {}", skill.getName());

        if (skillUseCharacter instanceof L1PcInstance) {
            L1PcInstance skillUsePc = (L1PcInstance) skillUseCharacter;
            skillUsePc.getAutoPotion().checkSkill(skillId);
        }

        if (skillId == LIFE_STREAM) {
            L1EffectSpawn.getInstance().doSpawnLifeStream(skillUseCharacter, targetX, targetY);
            return;
        }

        if (skillId == FIRE_WALL) {
            L1EffectSpawn.getInstance().doSpawnFireWall(skillUseCharacter, targetX, targetY);
            return;
        }

        if (skillId == CUBE_BALANCE) {
            L1EffectSpawn.getInstance().doSpawnCubeBalance(skillUseCharacter, targetX, targetY);
            return;
        }

        if (skillId == CUBE_SHOCK) {
            L1EffectSpawn.getInstance().doSpawnCubeShock(skillUseCharacter, targetX, targetY);
            return;
        }

        if (skillId == CUBE_IGNITION) {
            L1EffectSpawn.getInstance().doSpawnCubeIgnition(skillUseCharacter, targetX, targetY);
            return;
        }

        if (skillId == CUBE_QUAKE) {
            L1EffectSpawn.getInstance().doSpawnCubeQuake(skillUseCharacter, targetX, targetY);
            return;
        }

        L1Skill runSkill = L1SkillFactory.create(skillId);

        try {
            if (runSkill != null) {
                runSkill.preSkill(skillRequest);
            }

            for (L1Character cha : targetList) {
                if (!L1SkillUtils.isTargetCalc(skillUseCharacter, cha, skillId)) {
                    L1LogUtils.gmLog(skillUseCharacter, "isTargetCalc 스킬스킵 : {} 타겟 : {}", skill.getName(), cha.getName());
                    continue;
                }

                L1MagicRun magic = new L1MagicRun(skillUseCharacter, cha);
                magic.setLeverage(leverage);

                if (skill.isProbability() && isInValidTargetForCurseOrProbability(cha)) {
                    removeTarget(cha);

                    L1LogUtils.gmLog(skillUseCharacter, "isInValidTargetForCurseOrProbability 스킬스킵 : {} 타겟 : {}", skill.getName(), cha.getName());

                    continue;
                }

                L1SkillUtils.deleteRepeatedSkills(cha, skillId);

                int dmg = 0;

                if (runSkill != null) {
                    skillRequest.setMagic(magic);
                    skillRequest.setTargetCharacter(cha);
                }

                if (skill.isAttack() && skillUseCharacter.getId() != cha.getId()) {
                    if (L1SkillUtils.isCounterMagic(cha, skillId)) {
                        removeTarget(cha);
                        L1LogUtils.gmLog(skillUseCharacter, "isCounterMagic 스킬스킵 : {} 타겟 : {}", skill.getName(), cha.getName());
                        continue;
                    }

                    dmg = magic.calcMagicDamage(skillId);

                    if (skillId != TRIPLE_ARROW) {
                        if (L1SkillUtils.hasEraseMagic(cha)) {
                            L1SkillUtils.removeEraseMagic(cha);
                        }
                    }

                    skillRequest.setDamage(dmg);

                    if (runSkill != null) {
                        duration = runSkill.interceptorDuration(skillRequest, duration);
                    }
                } else if (skill.isProbability()) {
                    boolean isSuccess;

                    if (skillType == TYPE_GM_BUFF) {
                        isSuccess = true;
                    } else {
                        isSuccess = magic.calcProbabilityMagic(skillId);
                    }

                    if (runSkill != null) {
                        isSuccess = runSkill.interceptProbability(skillRequest, isSuccess);
                    }

                    skillRequest.setSuccess(isSuccess);

                    if (!L1SkillUtils.isEraseMagic(skillId) && skillId != EARTH_BIND) {
                        if (L1SkillUtils.hasEraseMagic(cha)) {
                            L1SkillUtils.removeEraseMagic(cha);
                        }
                    }

                    L1SkillUtils.removeSleep(cha);

                    if (isSuccess) {
                        if (L1SkillUtils.isCounterMagic(cha, skillId)) {
                            removeTarget(cha);
                            continue;
                        }

                        if (runSkill != null) {
                            duration = runSkill.interceptorDuration(skillRequest, duration);
                        }
                    } else {
                        removeTarget(cha);
                        continue;
                    }
                } else if (skill.isHeal()) {
                    dmg = -1 * L1MagicUtils.calcHealHp(skillUseCharacter, cha, skillId);
                }

                if (cha.getSkillEffectTimerSet().hasSkillEffect(skillId) && !(runSkill instanceof L1SkillShockStun)) {
                    addMagicList(cha, true);

                    if (skillId != SHAPE_CHANGE) {
                        L1LogUtils.gmLog(skillUseCharacter, "hasSkillEffect 스킬스킵 : {} 타겟 : {}", skill.getName(), cha.getName());
                        continue;
                    }
                }

                if (runSkill != null) {
                    if (skillRequest.isSuccess()) {
                        runSkill.runSkill(skillRequest);

                        L1LogUtils.skillLog("시전자 : {}, 타겟 : {}", skillUseCharacter, targetList);

                        int status = runSkill.getRunSkillState();

                        if (status == L1Skill.STATUS_CONTINUE) {
                            continue;
                        } else if (status == L1Skill.STATUS_RETURN) {
                            return;
                        }
                    }

                    dmg = runSkill.interceptDamage(skillRequest, dmg);
                }

                if (skill.isHeal()) {
                    if (cha instanceof L1MonsterInstance) {
                        L1MonsterInstance mon = (L1MonsterInstance) cha;
                        int undeadType = mon.getTemplate().getUndead();

                        if (undeadType == 1) {
                            dmg *= -1;
                        } else if (undeadType == 3) {
                            dmg = 0;
                        }
                    }
                }

                if ((cha instanceof L1TowerInstance || cha instanceof L1DoorInstance) && dmg < 0) {
                    dmg = 0;
                }

                if (dmg != 0) {
                    magic.commit(new L1MagicActionVo(dmg, 0));
                }

                if (cha instanceof L1PcInstance) {
                    L1PcInstance pc = (L1PcInstance) cha;
                    pc.getLight().turnOnOffLight();
                    pc.sendPackets(new S_OwnCharAttrDef(pc));
                    pc.sendPackets(new S_OwnCharStatus(pc));
                    pc.sendPackets(new S_SPMR(pc));

                    sendHappenMessage(pc);
                }

                addMagicList(cha, false);
            }

            if (runSkill != null) {
                runSkill.completeSkill(skillRequest);
            }

            if (skillId == DETECTION || skillId == COUNTER_DETECTION || skillId == EYE_OF_DRAGON || skillId == EYES_BREAK) {
                L1SkillUtils.detection(skillUseCharacter);
            }
        } catch (Exception e) {
            logger.error("오류", e);
        }
    }

    private boolean isInValidTargetForCurseOrProbability(L1Character cha) {
        if (cha instanceof L1TowerInstance || cha instanceof L1DoorInstance || cha instanceof L1MerchantInstance) {
            return true;
        }

        if (cha instanceof L1PcInstance) {
            if (skillUseCharacter instanceof L1PcInstance) {
                L1PcInstance skillUsePc = (L1PcInstance) skillUseCharacter;
                L1PcInstance targetPc = (L1PcInstance) cha;

                if (skillUsePc.checkNonPvP()) {
                    return skillUsePc.getId() != targetPc.getId() && (targetPc.getClanId() == 0 || skillUsePc.getClanId() != targetPc.getClanId());
                }
            }
            return false;
        }

        boolean isTu = false;
        boolean isErase = false;
        int undeadType = 0;

        if (cha instanceof L1MonsterInstance) {
            isTu = ((L1MonsterInstance) cha).getTemplate().getIsTU();
            isErase = ((L1MonsterInstance) cha).getTemplate().getIsErase();
            undeadType = ((L1MonsterInstance) cha).getTemplate().getUndead();
        }

        if (L1SkillUtils.isEraseMagic(skillId)) {
            return !isErase;
        }

        switch (skill.getSkillId()) {
            case TURN_UNDEAD: {
                return (undeadType == 0 || undeadType == 2) || !isTu;
            }
            case SLOW:
            case MOB_SLOW_1:
            case MOB_SLOW_18:
            case MANA_DRAIN:
            case GRATE_SLOW:
            case ENTANGLE:
            case WIND_SHACKLE: {
                return !isErase;
            }
        }

        return false;
    }
}
