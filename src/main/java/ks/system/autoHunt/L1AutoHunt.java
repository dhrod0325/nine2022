package ks.system.autoHunt;

import ks.app.config.prop.CodeConfig;
import ks.constants.L1NpcConstants;
import ks.constants.L1SkillId;
import ks.core.datatables.SkillsTable;
import ks.core.datatables.SprTable;
import ks.core.datatables.getback.GetBackTable;
import ks.model.*;
import ks.model.attack.magic.L1MagicRun;
import ks.model.attack.utils.L1AttackUtils;
import ks.model.instance.L1MonsterInstance;
import ks.model.instance.L1ScarecrowInstance;
import ks.model.pc.L1PcInstance;
import ks.model.skill.L1SkillUse;
import ks.model.skill.utils.L1SkillUtils;
import ks.packets.serverpackets.S_DoActionGFX;
import ks.packets.serverpackets.S_OtherCharPacks;
import ks.packets.serverpackets.S_RemoveObject;
import ks.packets.serverpackets.S_SkillSound;
import ks.system.robot.L1RobotUtils;
import ks.util.L1CharPosUtils;
import ks.util.common.random.RandomUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class L1AutoHunt implements L1AutoHuntAi {
    private final Logger logger = LogManager.getLogger();

    public static final byte[] HEADING_TABLE_X = CodeConfig.HEADING_TABLE_X;
    public static final byte[] HEADING_TABLE_Y = CodeConfig.HEADING_TABLE_Y;

    public static final int AI_STATUS_SETTING = 0;
    public static final int AI_STATUS_WALK = 1;
    public static final int AI_STATUS_ATTACK = 2;
    public static final int AI_STATUS_DEAD = 3;
    public static final int AI_STATUS_CORPSE = 4;
    public static final int AI_STATUS_SPAWN = 5;
    public static final int AI_STATUS_ESCAPE = 6;
    public static final int AI_STATUS_PICKUP = 7;
    public static final int AI_STATUS_SHOP = 8;
    public static final int AI_STATUS_TARGET_MOVE = 9;

    protected final L1HateList attackList = new L1HateList();
    private final List<L1Character> blackTargetList = new CopyOnWriteArrayList<>();
    protected int aiTryCount = 0;
    protected L1PcInstance robot;
    protected long aiStartTime;
    protected long aiTime;
    protected int aiStatus = 0;
    private int aiMaxTryCount = 100;
    private int toMoveTryMaxCount = 40;
    private int toMoveCount = 0;
    private boolean deadProcessCheck = false;

    public L1AutoHunt(L1PcInstance robot) {
        this.robot = robot;
    }

    public void resetCounts() {
        toMoveCount = 0;
        aiTryCount = 0;
    }

    @Override
    public void receiveDamage(L1Character attacker, int damage) {
        addAttackTarget(attacker, damage);
    }

    public L1Character getFirstTarget() {
        if (attackList.getTotalHate() == 0) {
            return getNearTarget();
        }

        return attackList.getMaxHateCharacter();
    }

    public L1Character getNearTarget() {
        int temp;
        int min = Integer.MAX_VALUE;

        L1Character minTarget = null;

        for (L1Character target : attackList.toTargetList()) {
            temp = robot.getTileLineDistance(target);

            if (temp < min) {
                min = temp;
                minTarget = target;
            }
        }

        if (minTarget == null) {
            minTarget = attackList.getMaxHateCharacter();
        }

        return minTarget;
    }

    @Override
    public void toAiAttack() {
        L1Character target = getFirstTarget();

        if (target == null) {
            giveUpAndTeleport();
            return;
        }

        if (target.isDead()) {
            attackList.remove(target);
            setAiStatus(AI_STATUS_WALK);
            return;
        }

        if (target.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.EARTH_BIND)) {
            attackList.remove(target);
            return;
        }

        if (L1RobotUtils.isDistance(robot.getX(), robot.getY(), robot.getMapId(), target.getX(), target.getY(), target.getMapId(), maxAttackRange())) {
            if (L1CharPosUtils.glanceCheck(robot, target.getX(), target.getY())) {
                toAttack(target);
            } else {
                setAiStatus(AI_STATUS_TARGET_MOVE);
            }
        } else {
            setAiStatus(AI_STATUS_TARGET_MOVE);
        }

        executeBuff();
    }

    @Override
    public void toAiTargetMove() {
        L1Character o = getFirstTarget();

        if (o == null) {
            setAiStatus(AI_STATUS_WALK);
            return;
        }

        if (o.isDead() || robot.getLocation().getTileLineDistance(o.getLocation()) > 15) {
            attackList.remove(o);
            setAiStatus(AI_STATUS_WALK);
            return;
        }

        if (L1RobotUtils.isAttack(robot, o, maxAttackRange())) {
            setAiStatus(AI_STATUS_ATTACK);
            return;
        }

        executeBuff();

        toMove(o);
    }

    private void removeBlackListTarget() {
        for (L1Character c : blackTargetList) {
            if (c == null) {
                blackTargetList.remove(c);
                continue;
            }

            if (c.isDead()) {
                blackTargetList.remove(c);
            }
        }
    }

    public void addAttackTarget(L1Character target) {
        if (blackTargetList.contains(target)) {
            return;
        }

        attackList.add(target, 0);
    }

    public void addAttackTarget(L1Character target, int hate) {
        attackList.add(target, hate);
    }

    public void toSearchTarget() {
        List<L1Object> visibleObjects = L1World.getInstance().getVisibleObjects(robot, 15);

        boolean isBow = robot.isLongAttack();

        for (L1Object obj : visibleObjects) {
            if (obj instanceof L1Character) {
                if (!L1CharPosUtils.isAttackPosition(robot, obj.getX(), obj.getY(), isBow ? 8 : 6)) {
                    continue;
                }
            }

            if (obj instanceof L1ScarecrowInstance) {
                addAttackTarget((L1Character) obj);
            }

            if (obj instanceof L1MonsterInstance) {
                L1MonsterInstance mon = (L1MonsterInstance) obj;

                //스틸 설정이 되어있는경우 누가 친 몹은 잡지 않음
                if (!isSteal()) {
                    boolean check1 = !mon.getHateList().isEmpty();
                    boolean check2 = false;

                    List<L1PcInstance> aroundPlayers = L1World.getInstance().getVisiblePlayer(mon, 8);

                    for (L1PcInstance o : aroundPlayers) {
                        if (o.equals(robot)) {
                            continue;
                        }

                        check2 = true;
                        break;
                    }

                    if (check1 && check2) {
                        continue;
                    }
                }


                if (mon.isDead()) {
                    continue;
                }

                if (attackList.containsKey(mon)) {
                    continue;
                }

                addAttackTarget(mon, 0);
            }
        }
    }

    @Override
    public void toAI(long time) {
        removeBlackListTarget();

        try {
            if (!isAi(time)) {
                return;
            }

            if (robot.isDead()) {
                setAiStatus(AI_STATUS_DEAD);
            } else {
                //살아난 경우
                if (aiStatus == AI_STATUS_DEAD) {
                    setAiStatus(AI_STATUS_SETTING);
                    setDeadProcessCheck(false);
                }
            }

            if (robot.getPoison() != null) {
                if (!robot.isDead() && robot.getCurrentHp() > 0)
                    robot.curePoison();
            }

            if (aiTryCount > aiMaxTryCount && aiStatus != AI_STATUS_SETTING && aiMaxTryCount > 0) {
                if (attackList.getMaxHateCharacter() != null) {
                    L1Character attacker = attackList.getMaxHateCharacter();

                    if (!blackTargetList.contains(attacker)) {
                        blackTargetList.add(attacker);
                    }

                    attackList.remove(attacker);
                }

                giveUpAndTeleport();
            }

            switch (aiStatus) {
                case AI_STATUS_WALK:
                    toSearchTarget();

                    if (attackList.toHateList().size() > 0) {
                        setAiStatus(AI_STATUS_ATTACK);
                    }

                    break;
                case AI_STATUS_ATTACK:
                case AI_STATUS_ESCAPE:
                    if (attackList.toHateList().size() == 0) {
                        setAiStatus(AI_STATUS_WALK);
                    }
                    break;
                default:
                    break;
            }

            aiStartTime = time;

            switch (aiStatus) {
                case AI_STATUS_SETTING:
                    toAiSetting();
                    break;
                case AI_STATUS_WALK:
                    toAiWalk();
                    break;
                case AI_STATUS_ATTACK:
                    toAiAttack();
                    break;
                case AI_STATUS_DEAD:
                    aiDead();
                    break;
                case AI_STATUS_CORPSE:
                    toAiCorpse();
                    break;
                case AI_STATUS_SPAWN:
                    toAiSpawn();
                    break;
                case AI_STATUS_ESCAPE:
                    toAiEscape();
                    break;
                case AI_STATUS_PICKUP:
                    toAiPickup();
                    break;
                case AI_STATUS_SHOP:
                    toAiShop();
                    break;
                case AI_STATUS_TARGET_MOVE:
                    toAiTargetMove();
                    break;
                default:
                    break;
            }

            aiTryCount++;
        } catch (Exception e) {
            logger.error("오류", e);
        }
    }

    @Override
    public void toAiWalk() {
        executeBuff();
    }

    @Override
    public void toAiDead() {
        L1PolyMorph.undoPoly(robot);

        int[] loc = GetBackTable.getInstance().getBackLocation(robot);
        robot.getNearObjects().removeAllKnownObjects();
        Broadcaster.broadcastPacket(robot, new S_RemoveObject(robot));
        robot.setCurrentHp(robot.getLevel());
        robot.setFood(39);
        robot.setDead(false);
        robot.setActionStatus(0);
        L1World.getInstance().moveVisibleObject(robot, loc[2]);
        robot.setX(loc[0]);
        robot.setY(loc[1]);
        robot.setMap((short) loc[2]);
        Broadcaster.broadcastPacket(robot, new S_OtherCharPacks(robot));
        attackList.clear();
    }

    public void aiDead() {
        aiTime = getFrame(robot.getGfxId().getGfxId(), 0);

        if (!isDeadProcessCheck()) {
            toAiDead();

            setDeadProcessCheck(true);
        }
    }

    @Override
    public void toAiSetting() {
        aiTime = getFrame(robot.getGfxId().getGfxId(), 0);

        if (isSmallHp()) {
            return;
        }

        setAiStatus(AI_STATUS_WALK);
        executeBuff();
    }

    public boolean isSmallHp() {
        if (robot.getCurrentHp() < robot.getMaxHp()) {
            doPotion();
            return true;
        }

        return false;
    }

    public void toMove(int x, int y) {
        if (toMoveCount > toMoveTryMaxCount && toMoveTryMaxCount > 0) {
            giveUpAndTeleport();
            toMoveCount = 0;
            return;
        }

        int dir = L1CharPosUtils.calcMoveDirection(robot, x, y);

        if (dir != -1) {
            aiTime = getFrame(robot.getGfxId().getGfxId(), 0);
            L1CharPosUtils.setDirectionMove(robot, dir);
        }

        toMoveCount++;
    }

    public void toMove(L1Object target) {
        toMove(target.getX(), target.getY());
    }

    public void toAttack(L1Character target) {
        if (L1CharPosUtils.isSafeZone(target)) {
            giveUpAndTeleport();
            return;
        }

        if (L1AttackUtils.isNotAttackAbleByTargetStatus(target)) {
            giveUpAndTeleport();
            return;
        }

        if (target instanceof L1MonsterInstance) {
            L1MonsterInstance mon = (L1MonsterInstance) target;

            if (mon.getHiddenStatus() >= 1) {
                if (mon.getHiddenStatus() != L1NpcConstants.HIDDEN_STATUS_FLY) {
                    Broadcaster.broadcastPacket(robot, new S_DoActionGFX(robot.getId(), 19));
                    Broadcaster.broadcastPacket(mon, new S_SkillSound(mon.getId(), 749));
                }

                mon.setHiddenStatus(0);
            }
        }

        if (target instanceof L1PcInstance) {
            List<Integer> skillIds = new ArrayList<>();

            if (robot.isKnight()) {
                skillIds.addAll(Arrays.asList(87, 88, 89));
            } else if (robot.isElf()) {
                if (robot.isLongAttack()) {
                    skillIds.addAll(Arrays.asList(132, 149));
                } else {
                    skillIds.addAll(Arrays.asList(46, 148, 175));
                }
            } else if (robot.isDarkElf()) {
                skillIds.addAll(Arrays.asList(102, 105, 106, 107));
            }

            int skillId = skillIds.get(RandomUtils.nextInt(0, skillIds.size() - 1));

            if (robot.isElf()) {
                if (robot.getCurrentMp() <= 100) {
                    skillId = 146;
                }
            }

            L1Skills skill = SkillsTable.getInstance().getTemplate(skillId);
            String skillTarget = skill.getTarget();
            int targetTo = skill.getTargetTo();

            if (skill.getItemConsumeCount() > 0) {
                robot.getInventory().storeItem(skill.getItemConsumeId(), skill.getItemConsumeCount());
            }

            if ("attack".equalsIgnoreCase(skillTarget)) {
                if (targetTo > 0) {
                    if (skill.getProbabilityValue() > 0) {
                        L1MagicRun magic = new L1MagicRun(robot, target);
                        if (magic.calcProbabilityMagic(skillId)) {
                            new L1SkillUse(robot, skillId, target.getId(), target.getX(), target.getY(), skill.getBuffDuration()).run();
                        }
                    } else {
                        new L1SkillUse(robot, skillId, target.getId(), target.getX(), target.getY(), skill.getBuffDuration()).run();
                    }
                }
            } else if ("none".equalsIgnoreCase(skillTarget)) {
                if (targetTo == 0) {
                    if (!robot.getSkillEffectTimerSet().hasSkillEffect(skillId)) {
                        if (RandomUtils.isWinning(100, 30)) {
                            new L1SkillUse(robot, skillId, robot.getId(), robot.getX(), robot.getY(), skill.getBuffDuration()).run();
                        }
                    }
                }
            } else if ("buff".equalsIgnoreCase(skillTarget)) {
                if (targetTo > 0) {
                    new L1SkillUse(robot, skillId, target.getId(), target.getX(), target.getY(), skill.getBuffDuration()).run();
                }
            }
        }

        resetCounts();

        aiTime = getAttackFrame(robot.getGfxId().getGfxId());
        target.onAction(robot);
    }

    public void setAiStatus(int aiStatus) {
        this.aiStatus = aiStatus;
    }

    public int calcHpPer() {
        return (int) (((double) robot.getCurrentHp() / (double) robot.getMaxHp()) * 100);
    }

    public void giveUpAndTeleport() {
        giveUp();
        teleport();
    }

    @Override
    public boolean isAi(long time) {
        long speed = aiTime;
        long temp = time - aiStartTime;
        double gab = robot.getMoveState().getMoveSpeed() == 1 && robot.getMoveState().getBraveSpeed() == 1 ? 0.5 : robot.getMoveState().getMoveSpeed() == 1 ? 0.2 : robot.getMoveState().getMoveSpeed() == 2 ? -0.5 : 0;
        speed -= (long) (speed * gab);

        if (robot.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.EARTH_BIND)
                || robot.getSkillEffectTimerSet().hasSkillEffect(L1SkillUtils.ICE_SKILLS)
                || robot.getSkillEffectTimerSet().hasSkillEffect(L1SkillUtils.STUN_SKILLS)) {
            return false;
        }

        int gfxId = robot.getGfxId().getTempCharGfx();
        int weapon = robot.getCurrentWeapon();
        int interval = SprTable.getInstance().getAttackSpeed(gfxId, weapon + 1);

        double HASTE_RATE = 0.745;
        double WAFFLE_RATE = 0.874;
        double THIRD_SPEED_RATE = 0.874;

        if (robot.isHaste()) {
            interval *= HASTE_RATE;
        }

        if (robot.isBrave()) {
            interval *= HASTE_RATE;
        }

        if (robot.isElfBrave()) {
            interval *= WAFFLE_RATE;
        }

        if (robot.isDragonPearl()) {
            interval *= THIRD_SPEED_RATE;
        }

        if (temp < interval) {
            return false;
        }

        if (temp >= interval) {
            aiStartTime = time;
            return true;
        }

        if (time == 0 || temp >= speed) {
            aiStartTime = time;
            return true;
        }

        return false;
    }

    public long getAttackFrame(int gfxId) {
        return getFrame(gfxId, 1);
    }

    public int getFrame(int gfx, int gfxMode) {
        int attackSpeed;

        if (gfx == robot.getGfxId().getTempCharGfx()) {
            attackSpeed = 600;
        } else {
            if (!robot.isElf()) {
                attackSpeed = 0;
            } else {
                attackSpeed = 200;
            }
        }

        switch (gfxMode) {
            case 0:
                return 300;
            case 1:
                return 600 + attackSpeed;
        }

        return 1000;
    }

    public int maxAttackRange() {
        if (robot.isElf() && robot.isLongAttack()) {
            return 8;
        }

        return 1;
    }

    protected boolean isSteal() {
        return true;
    }

    public void setAiMaxTryCount(int aiMaxTryCount) {
        this.aiMaxTryCount = aiMaxTryCount;
    }

    public void setToMoveTryMaxCount(int toMoveTryMaxCount) {
        this.toMoveTryMaxCount = toMoveTryMaxCount;
    }


    @Override
    public void toAiCorpse() {
    }

    @Override
    public void toAiSpawn() {

    }

    @Override
    public void toAiEscape() {

    }

    @Override
    public void toAiPickup() {

    }

    @Override
    public void toAiShop() {

    }

    protected abstract void doPotion();

    public abstract void executeBuff();

    public abstract void giveUp();

    public abstract void teleport();

    public void noUseResetAiCount() {
        setToMoveTryMaxCount(-1);
        setAiMaxTryCount(-1);
    }

    public boolean isDeadProcessCheck() {
        return deadProcessCheck;
    }

    public void setDeadProcessCheck(boolean deadProcessCheck) {
        this.deadProcessCheck = deadProcessCheck;
    }
}
