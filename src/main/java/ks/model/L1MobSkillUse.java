package ks.model;

import ks.constants.L1ActionCodes;
import ks.constants.L1NpcConstants;
import ks.constants.L1SkillId;
import ks.core.ObjectIdFactory;
import ks.core.datatables.SkillsTable;
import ks.core.datatables.mobskill.L1MobSkillInfo;
import ks.core.datatables.mobskill.MobSkillInfoTable;
import ks.core.datatables.npc.NpcTable;
import ks.model.attack.physics.L1AttackRun;
import ks.model.instance.L1MonsterInstance;
import ks.model.instance.L1NpcInstance;
import ks.model.instance.L1PetInstance;
import ks.model.instance.L1SummonInstance;
import ks.model.pc.L1PcInstance;
import ks.model.skill.L1SkillUse;
import ks.packets.serverpackets.S_DoActionGFX;
import ks.packets.serverpackets.S_NPCPack;
import ks.packets.serverpackets.S_NpcChatPacket;
import ks.packets.serverpackets.S_SkillSound;
import ks.util.L1InstanceFactory;
import ks.util.common.random.RandomUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static ks.util.L1CharPosUtils.*;

public class L1MobSkillUse {
    private static final Logger logger = LogManager.getLogger();

    private final L1NpcInstance npc;

    private L1Character target = null;

    private L1MobSkill mobSkill = null;

    private int sleepTime = 0;

    public L1MobSkillUse(L1NpcInstance npc) {
        this.npc = npc;
    }

    public void resetAllSkillUseCount() {
        if (getMobSkill() == null) {
            return;
        }

        int size = getMobSkill().getSkillSize();

        for (int i = 0; i < size; i++) {
            mobSkill.setSkillUseCount(i, 0);
        }
    }

    public int getSleepTime() {
        return sleepTime;
    }

    public L1MobSkill getMobSkill() {
        return mobSkill;
    }

    public void setMobSkill(L1MobSkill mobSkill) {
        this.mobSkill = mobSkill;
    }

    public boolean isSkillTrigger(L1Character target) {
        try {
            if (target == null) {
                return false;
            }

            if (mobSkill == null) {
                return false;
            }

            this.target = target;

            int type = getMobSkill().getType(0);

            if (type == L1MobSkill.TYPE_NONE) {
                return false;
            }

            int skillSize = getMobSkill().getSkillSize();

            for (int i = 0; i < skillSize && getMobSkill().getType(i) != L1MobSkill.TYPE_NONE; i++) {
                int changeType = getMobSkill().getChangeTarget(i);

                if (changeType > 0) {
                    this.target = changeTarget(changeType, i);
                } else {
                    this.target = target;
                }

                if (isSkillUseAble(i, false)) {
                    return true;
                }
            }
        } catch (Exception e) {
            logger.error("몹 스킬 오류", e);
        }

        return false;
    }

    public boolean skillUse(L1Character tg, boolean isTriRnd) {
        try {
            if (tg == null || mobSkill == null) {
                return false;
            }

            target = tg;

            int type = getMobSkill().getType(0);

            if (type == L1MobSkill.TYPE_NONE) {
                return false;
            }

            int[] skills;

            int skillSizeCounter = 0;
            int skillSize = getMobSkill().getSkillSize();

            if (skillSize >= 0) {
                skills = new int[skillSize];

                for (int i = 0; i < getMobSkill().getSkillSize() && getMobSkill().getType(i) != L1MobSkill.TYPE_NONE; i++) {
                    int changeType = getMobSkill().getChangeTarget(i);

                    if (changeType > 0) {
                        target = changeTarget(changeType, i);
                    } else {
                        target = tg;
                    }

                    if (isSkillUseAble(i, isTriRnd)) {
                        skills[skillSizeCounter] = i;
                        skillSizeCounter++;
                    }
                }

                if (skillSizeCounter != 0) {
                    int num = RandomUtils.nextInt(skillSizeCounter);
                    return useSkill(skills[num]);
                }
            }
        } catch (Exception e) {
            logger.error(e);
            logger.error("오류", e);
        }

        return false;
    }

    private boolean useSkill(int i) {
        boolean isUseSkill = false;

        logger.trace("useSkill : " + i);

        try {
            int type = getMobSkill().getType(i);

            switch (type) {
                case L1MobSkill.TYPE_PHYSICAL_ATTACK:
                    if (physicalAttack(i)) {
                        mobSkill.skillUseCountUp(i);
                        isUseSkill = true;
                    }
                    break;
                case L1MobSkill.TYPE_MAGIC_ATTACK:
                    if (magicAttack(i)) {
                        mobSkill.skillUseCountUp(i);
                        isUseSkill = true;
                    }
                    break;
                case L1MobSkill.TYPE_SUMMON:
                    if (summon(i)) {
                        mobSkill.skillUseCountUp(i);
                        isUseSkill = true;
                    }
                    break;
                case L1MobSkill.TYPE_POLY:
                    if (poly(i)) {
                        mobSkill.skillUseCountUp(i);
                        isUseSkill = true;
                    }
                    break;
            }

        } catch (Exception e) {
            logger.error(e);
            logger.error("오류", e);
        }

        return isUseSkill;
    }


    private boolean summon(int idx) {
        int summonId = getMobSkill().getSummon(idx);
        int min = getMobSkill().getSummonMin(idx);
        int max = getMobSkill().getSummonMax(idx);
        int count;

        if (summonId == 0) {
            return false;
        }

        if (min == 0) {
            min = 1;
        }

        if (max == 0) {
            max = min;
        }

        count = RandomUtils.nextInt(max) + min;

        mobSpawn(summonId, count);

        Broadcaster.broadcastPacket(npc, new S_SkillSound(npc.getId(), 761));
        Broadcaster.broadcastPacket(npc, new S_DoActionGFX(npc.getId(), L1ActionCodes.ACTION_SkillBuff));

        sleepTime = npc.getTemplate().getSubMagicSpeed();

        return true;
    }

    private boolean poly(int idx) {
        int polyId = getMobSkill().getPolyId(idx);
        boolean usePoly = false;

        if (polyId == 0) {
            return false;
        }

        for (L1PcInstance pc : L1World.getInstance().getVisiblePlayer(npc)) {
            if (pc == null || pc.isDead()) {
                continue;
            }
            if (pc.isGmInvis()) {
                continue;
            }

            if (!glanceCheck(npc, pc)) {
                continue;
            }

            int npcId = npc.getTemplate().getNpcId();

            if (npcId == 81082) {
                pc.getInventory().takeoffEquip(945);
            }
            L1PolyMorph.doPoly(pc, polyId, 1800, L1PolyMorph.MORPH_BY_NPC);
            usePoly = true;
        }

        if (usePoly) {
            for (L1PcInstance pc : L1World.getInstance().getVisiblePlayer(npc)) {
                if (pc == null)
                    continue;

                pc.sendPackets(new S_SkillSound(pc.getId(), 230));
                Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 230));
                break;
            }

            Broadcaster.broadcastPacket(npc, new S_DoActionGFX(npc.getId(), L1ActionCodes.ACTION_SkillBuff));

            sleepTime = npc.getTemplate().getSubMagicSpeed();
        }

        return usePoly;
    }

    private boolean magicAttack(int idx) {
        try {
            L1MobSkill tpl = getMobSkill();

            int skillId = tpl.getSkillId(idx);
            L1Skills skill = SkillsTable.getInstance().getTemplate(skillId);

            if (skill == null) {
                logger.error("{}의 스킬이 존재하지 않습니다. ACT_ID : {}", npc.getName(), idx);
                return false;
            }

            if (npc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.SILENCE)) {
                return false;
            }

            if (skillId == 43) {
                if (npc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.HASTE)) {
                    return false;
                }
                target = npc;
            }

            if (skillId > 0) {
                L1SkillUse skillUse = new L1SkillUse(npc, skillId, target.getId(), target.getX(), target.getY(), 0, L1SkillUse.TYPE_NORMAL);
                boolean canUseSkill = skillUse.checkUseSkill();

                if (skill.calcConsumeHp(npc) <= -1) {
                    return false;
                }

                if (skill.calcConsumeMp(npc) <= -1) {
                    return false;
                }

                if (canUseSkill) {
                    if (tpl.getLeverage(idx) > 0) {
                        skillUse.setLeverage(tpl.getLeverage(idx));
                    }

                    skillUse.run();

                    if (skillUse.skill.isAttack() && !skillUse.skill.isTurnUndead()) {
                        sleepTime = npc.getTemplate().getAtkMagicSpeed();
                    } else {
                        sleepTime = npc.getTemplate().getSubMagicSpeed();
                    }

                    L1MobSkillInfo mobSkillInfo = MobSkillInfoTable.getInstance().findByMobSkillId(skillId);

                    if (mobSkillInfo != null) {
                        Broadcaster.broadcastPacket(npc, new S_NpcChatPacket(npc, mobSkillInfo.getMent(), 0));
                    }

                    return true;
                }
            }
        } catch (Exception e) {
            logger.error(e);
            logger.error("오류", e);

            return false;
        }


        return false;
    }

    private boolean physicalAttack(int idx) {
        Map<Integer, Integer> targetList = new ConcurrentHashMap<>();

        int areaWidth = getMobSkill().getAreaWidth(idx);
        int areaHeight = getMobSkill().getAreaHeight(idx);

        int range = getMobSkill().getRange(idx);
        int actId = getMobSkill().getActid(idx);
        int gfxId = getMobSkill().getGfxid(idx);

        if (npc.getLocation().getTileLineDistance(target.getLocation()) > range) {
            return false;
        }

        if (!glanceCheck(npc, target)) {
            return false;
        }

        npc.setHeading(targetDirection(npc, target.getX(), target.getY()));

        if (areaHeight > 0) {
            for (L1Object obj : L1World.getInstance().getVisibleBoxObjects(npc, npc.getHeading(), areaWidth, areaHeight)) {
                if (obj == null)
                    continue;
                if (!(obj instanceof L1Character)) {
                    continue;
                }

                L1Character cha = (L1Character) obj;
                if (cha.isDead()) {
                    continue;
                }

                if (cha instanceof L1PcInstance
                        && npc instanceof L1SummonInstance
                        || npc instanceof L1PetInstance) {
                    if (cha.getId() == npc.getMaster().getId()) {
                        continue;
                    }
                    if (isSafeZone(cha)) {
                        continue;
                    }
                }

                if (!glanceCheck(npc, cha)) {
                    continue;
                }

                if (target instanceof L1PcInstance
                        || target instanceof L1SummonInstance
                        || target instanceof L1PetInstance) {
                    if (obj instanceof L1PcInstance
                            && !((L1PcInstance) obj).isGmInvis()
                            || obj instanceof L1SummonInstance
                            || obj instanceof L1PetInstance) {
                        targetList.put(obj.getId(), 0);
                    }
                } else {
                    if (obj instanceof L1MonsterInstance) {
                        targetList.put(obj.getId(), 0);
                    }
                }
            }
        } else {
            targetList.put(target.getId(), 0);
        }

        if (targetList.isEmpty()) {
            return false;
        }

        for (int targetId : targetList.keySet()) {
            L1Object target = L1World.getInstance().findObject(targetId);

            if (target == null)
                continue;

            L1AttackRun attack = new L1AttackRun(npc, (L1Character) target);

            if (attack.getAttackParam().isHitUp()) {
                if (mobSkill.getLeverage(idx) > 0) {
                    attack.getAttackParam().setLeverage(mobSkill.getLeverage(idx));
                }
            }

            if (actId > 0) {
                attack.getAttackParam().setActId(actId);
            }

            if (targetId == target.getId()) {
                if (gfxId > 0) {
                    Broadcaster.broadcastPacket(npc, new S_SkillSound(npc.getId(), gfxId));
                }

                attack.action();
            }

            attack.commit();
        }

        sleepTime = npc.getAtkSpeed();

        return true;
    }

    private boolean isSkillUseAble(int actNo, boolean isTriRnd) {
        boolean useAble = false;

        int type = getMobSkill().getType(actNo);

        if (isTriRnd || type == L1MobSkill.TYPE_SUMMON || type == L1MobSkill.TYPE_POLY) {
            int triggerRandom = getMobSkill().getTriggerRandom(actNo);

            if (triggerRandom > 0) {
                if (RandomUtils.isWinning(100, triggerRandom)) {
                    useAble = true;
                } else {
                    return false;
                }
            }
        }

        int triggerHp = getMobSkill().getTriggerHp(actNo);

        if (triggerHp > 0) {
            int currentHp = npc.getCurrentHp() * 100;
            int maxHp = npc.getMaxHp() + npc.getOptionHp();

            if (currentHp <= 0) {
                currentHp = 1;
            }

            if (maxHp <= 0) {
                maxHp = 1;
            }

            int hpRatio = currentHp / maxHp;

            if (hpRatio <= triggerHp) {
                useAble = true;
            } else {
                return false;
            }
        }

        if (getMobSkill().getTriggerCompanionHp(actNo) > 0) {
            L1NpcInstance companionNpc = searchMinCompanionHp();

            if (companionNpc == null) {
                return false;
            }

            int hpRatio = (companionNpc.getCurrentHp() * 100) / companionNpc.getMaxHp();

            if (hpRatio <= getMobSkill().getTriggerCompanionHp(actNo)) {
                target = companionNpc;
                useAble = true;
            } else {
                return false;
            }
        }

        int triggerRange = getMobSkill().getTriggerRange(actNo);
        int triggerCount = getMobSkill().getTriggerCount(actNo);

        if (triggerRange != 0) {
            int distance = npc.getLocation().getTileLineDistance(target.getLocation());

            if (getMobSkill().isTriggerDistance(actNo, distance)) {
                useAble = true;
            } else {
                return false;
            }
        }

        if (triggerCount > 0) {
            if (mobSkill.getSkillUseCount(actNo) < triggerCount) {
                useAble = true;
            } else {
                return false;
            }
        }

        return useAble;
    }

    private L1NpcInstance searchMinCompanionHp() {
        L1NpcInstance npc;
        L1NpcInstance minHpNpc = null;
        int hpRatio = 100;
        int companionHpRatio;
        int family = this.npc.getTemplate().getFamily();

        for (L1Object object : L1World.getInstance().getVisibleObjects(
                this.npc)) {
            if (object == null)
                continue;
            if (object instanceof L1NpcInstance) {
                npc = (L1NpcInstance) object;
                if (npc.getTemplate().getFamily() == family) {
                    companionHpRatio = (npc.getCurrentHp() * 100) / npc.getMaxHp();
                    if (companionHpRatio < hpRatio) {
                        hpRatio = companionHpRatio;
                        minHpNpc = npc;
                    }
                }
            }
        }
        return minHpNpc;
    }

    private void mobSpawn(int summonId, int count) {
        int i;

        for (i = 0; i < count; i++) {
            mobSpawn(summonId);
        }
    }

    private void mobSpawn(int summonId) {
        try {
            L1Npc spawnMonster = NpcTable.getInstance().getTemplate(summonId);

            if (spawnMonster != null) {
                try {
                    L1NpcInstance mob = L1InstanceFactory.createInstance(spawnMonster);
                    mob.setId(ObjectIdFactory.getInstance().nextId());
                    L1Location loc = npc.getLocation().randomLocation(6, false);// 6으로 변경
                    int heading = RandomUtils.nextInt(8);
                    mob.setX(loc.getX());
                    mob.setY(loc.getY());
                    mob.setHomeX(loc.getX());
                    mob.setHomeY(loc.getY());
                    short mapid = npc.getMapId();
                    mob.setMap(mapid);
                    mob.setHeading(heading);
                    mob.getInventory().clearItems();

                    L1World.getInstance().storeObject(mob);
                    L1World.getInstance().addVisibleObject(mob);

                    L1Object object = L1World.getInstance().findObject(mob.getId());

                    L1MonsterInstance newNpc = (L1MonsterInstance) object;

                    if (summonId == 45061 || summonId == 45161 || summonId == 45181 || summonId == 45455) {
                        Broadcaster.broadcastPacket(newNpc, new S_DoActionGFX(newNpc.getId(), L1ActionCodes.ACTION_Hide));
                        newNpc.setActionStatus(13);
                        Broadcaster.broadcastPacket(newNpc, new S_NPCPack(newNpc));
                        Broadcaster.broadcastPacket(newNpc, new S_DoActionGFX(newNpc.getId(), L1ActionCodes.ACTION_Appear));
                        newNpc.setActionStatus(0);
                        Broadcaster.broadcastPacket(newNpc, new S_NPCPack(newNpc));
                    }

                    newNpc.onNpcAI();
                    newNpc.getLight().turnOnOffLight();
                    newNpc.startChat(L1NpcConstants.CHAT_TIMING_APPEARANCE);
                } catch (Exception e) {
                    logger.error(e);
                    logger.error("오류", e);
                }
            }
        } catch (Exception e) {
            logger.error(e);
            logger.error("오류", e);
        }
    }

    private L1Character changeTarget(int type, int idx) {
        L1Character target;

        switch (type) {
            case L1MobSkill.CHANGE_TARGET_ME:
                target = npc;
                break;
            case L1MobSkill.CHANGE_TARGET_RANDOM:
                List<L1Character> targetList = new ArrayList<>();
                for (L1Object obj : L1World.getInstance().getVisibleObjects(npc)) {
                    if (obj == null)
                        continue;
                    if (obj instanceof L1PcInstance || obj instanceof L1PetInstance
                            || obj instanceof L1SummonInstance) {
                        L1Character cha = (L1Character) obj;

                        int distance = npc.getLocation().getTileLineDistance(cha.getLocation());

                        if (!getMobSkill().isTriggerDistance(idx, distance)) {
                            continue;
                        }

                        if (!glanceCheck(npc, cha)) {
                            continue;
                        }

                        if (!npc.getHateList().containsKey(cha)) {
                            continue;
                        }

                        if (cha.isDead()) {
                            continue;
                        }

                        targetList.add((L1Character) obj);
                    }
                }

                if (targetList.size() == 0) {
                    target = this.target;
                } else {
                    int randomSize = targetList.size() * 100;
                    int targetIndex = RandomUtils.nextInt(randomSize) / 100;
                    target = targetList.get(targetIndex);
                }
                break;

            default:
                target = this.target;
                break;
        }
        return target;
    }


}
