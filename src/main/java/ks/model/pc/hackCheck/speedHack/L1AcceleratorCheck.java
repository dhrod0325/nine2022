package ks.model.pc.hackCheck.speedHack;

import ks.app.LineageAppContext;
import ks.app.config.prop.CodeConfig;
import ks.constants.L1SkillId;
import ks.core.datatables.CharacterHackTable;
import ks.core.datatables.SprTable;
import ks.core.datatables.spr.SprStrictTable;
import ks.model.L1Teleport;
import ks.model.pc.L1PcInstance;
import ks.model.pc.hackCheck.speedHack.spr.SprCheck;
import ks.model.pc.hackCheck.speedHack.spr.SprChecker;
import ks.util.L1StatusUtils;
import ks.util.L1TeleportUtils;
import ks.util.log.L1LogUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Instant;
import java.util.EnumMap;

public class L1AcceleratorCheck {
    public static final int R_OK = 0;
    public static final int R_DETECTED = 1;
    public static final int R_DISCONNECTED = 2;
    private static final Logger logger = LogManager.getLogger(L1AcceleratorCheck.class);
    private final L1PcInstance pc;
    private final EnumMap<ACT_TYPE, Long> actTimers = new EnumMap<>(ACT_TYPE.class);
    private final SprChecker sprChecker;

    private int injusticeCount = 0;
    private int justiceCount = 0;

    public L1AcceleratorCheck(L1PcInstance pc) {
        this.pc = pc;

        long now = System.currentTimeMillis();

        for (ACT_TYPE each : ACT_TYPE.values()) {
            actTimers.put(each, now);
        }

        sprChecker = new SprChecker(pc);
    }

    public boolean isRightInterval(ACT_TYPE type, int diff) {
        long now = System.currentTimeMillis() + diff;
        long interval = now - actTimers.get(type);
        interval *= (CodeConfig.SPEED_CHECK_STRICTNESS - 5) / 100D;

        int rightInterval = getRightInterval(type);

        logger.trace("rightInterval : {} , interval : {}", rightInterval, interval);

        return interval >= rightInterval + diff;
    }

    public void put(ACT_TYPE type, long time) {
        actTimers.put(type, time);
    }


    public int checkInterval(ACT_TYPE type) {
        int result = R_OK;

        if (!CodeConfig.SPEED_HACK_CHECK) {
            return R_OK;
        }

        long now = System.currentTimeMillis();
        long interval = now - actTimers.get(type);
        int rightInterval = getRightInterval(type);

        interval *= (CodeConfig.SPEED_CHECK_STRICTNESS - 5) / 100D;

        long tempInterval = 0;

        if (CodeConfig.USE_SPR_CHECK) {
            SprCheck check = new SprCheck(pc, type.getActNum(), (int) interval);
            sprChecker.check(check);

            int avgInterval = sprChecker.getAvgInterval(check);

            logger.trace("interval : " + interval + ",rightInerval : " + rightInterval + ", avgInterval : " + avgInterval);

            tempInterval = interval;

            if (avgInterval >= rightInterval) {
                interval = avgInterval;
            }

            logger.trace("interval : " + interval + ", rightInterval : " + rightInterval + ",avgInterval : " + avgInterval);
        }

        logger.trace("injusticeCount : " + injusticeCount);
        logger.trace("justiceCount : " + justiceCount);

        if (interval > 0 && rightInterval > interval) {
            injusticeCount++;
            justiceCount = 0;

            if (injusticeCount / CodeConfig.SPEED_JUSTICE_COUNT >= CodeConfig.SPEED_HACK_PRISON) {
                doDisconnect();
                injusticeCount = 0;
                justiceCount = 0;
                return R_DISCONNECTED;
            } else if (injusticeCount % CodeConfig.SPEED_JUSTICE_COUNT == 0) {
                speedHackPenalty();

                String msg = "[스핵감지] - 이름 : {} 변신 : {} 정상 : {} 오류 : {} 범위 : {} ";
                Object[] args = new Object[]{pc.getName(), pc.getGfxId().getTempCharGfx(), rightInterval, interval, interval - rightInterval};
                logger.warn(msg, args);
                L1LogUtils.debugLog(msg, args);
            }

            if (CodeConfig.USE_SPR_CHECK) {
                long finalTempInterval = tempInterval;
                LineageAppContext.commonTaskScheduler().execute(() -> sprChecker.save(finalTempInterval));
            }

            result = R_DETECTED;
        } else if (interval >= rightInterval) {
            justiceCount++;

            if (justiceCount >= CodeConfig.SPEED_JUSTICE_COUNT + 1) {
                injusticeCount = 0;
                justiceCount = 0;
            }
        }

        actTimers.put(type, now);

        return result;
    }

    private void speedHackPenalty() {
        if (pc.isInvisible()) {
            pc.delInvis();
        }

        int checkCount = (injusticeCount / CodeConfig.SPEED_JUSTICE_COUNT);

        String msg = "경고횟수 : " + checkCount + "회";

        if (checkCount >= 2) {
            L1LogUtils.bugLog(pc.getName() + " - 스핵감지, " + msg);
        }

        pc.sendPackets(msg);

        if (CodeConfig.SPEED_HACK_STUN) {
            int stunTime = CodeConfig.SPEED_HACK_STUN_TIME;
            L1StatusUtils.shockStun(pc, stunTime);
        } else {
            L1Teleport.teleport(pc, pc.getX(), pc.getY(), pc.getMapId(), pc.getHeading(), false);
        }
    }

    private void doDisconnect() {
        try {
            L1Teleport.teleport(pc, 32736, 32799, (short) 34, 5, true);

            pc.sendPackets("20초 후에 마을로 텔레포트 됩니다");
            pc.sendPackets("리스하실경우 빠저나갈수 없습니다");
            pc.sendPackets("확인 후 프로그램 사용 했을시 벤처리됩니다");

            String msg = pc.getName() + "님이 감옥에 감금되었습니다.";
            L1LogUtils.bugLog(msg);
            logger.info(msg);

            LineageAppContext.commonTaskScheduler().schedule(() -> L1TeleportUtils.teleportToGiran(pc), Instant.now().plusMillis(1000 * 20));

            injusticeCount = 0;
            justiceCount = 0;

            CharacterHackTable.getInstance().insertHack(pc);
        } catch (Exception e) {
            logger.error("오류", e);
        }
    }

    public int getRightInterval(int t) {
        ACT_TYPE act = ACT_TYPE.valueOf(t);

        if (act != null) {
            return getRightInterval(act);
        }

        return 0;
    }

    public int getRightInterval(ACT_TYPE type) {
        int interval;

        double HASTE_RATE = CodeConfig.CHECK_HASTE_RATE;
        double BRAVE_RATE = CodeConfig.CHECK_BRAVE_RATE;
        double WAFFLE_RATE = CodeConfig.CHECK_WAFFLE_RATE;
        double DRAGON_PEARL_RATE = CodeConfig.CHECK_DRGON_PER_RATE;
        double MOVE_RATE = CodeConfig.CHECK_MOVE_RATE;

        switch (type) {
            case ATTACK:
                interval = SprTable.getInstance().getAttackSpeed(pc.getGfxId().getTempCharGfx(), pc.getCurrentWeapon() + 1);
                break;
            case MOVE:
                interval = SprTable.getInstance().getMoveSpeed(pc.getGfxId().getTempCharGfx(), pc.getCurrentWeapon());
                interval *= MOVE_RATE;
                break;
            case SPELL_DELAY:
                interval = SprTable.getInstance().getDirSpellSpeed(pc.getGfxId().getTempCharGfx());
                break;
            case SPELL_NO_DELAY:
                interval = SprTable.getInstance().getNoDelaySpellSpeed(pc.getGfxId().getTempCharGfx());
                break;
            default:
                return 0;
        }

        if (type.equals(ACT_TYPE.MOVE)) {
            if (pc.isFastMovable()) {
                interval *= HASTE_RATE;
            }
        }

        if (pc.isHaste()) {
            interval *= HASTE_RATE;
        }

        if (pc.isBrave()) {
            interval *= BRAVE_RATE;
        }

        if (pc.isElfBrave()) {
            interval *= WAFFLE_RATE;
        }

        if (pc.isThirdSpeed()) {
            interval *= DRAGON_PEARL_RATE;
        }

        if (pc.isElf() && pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.DANCING_BLADES)) {
            interval *= HASTE_RATE;
        }

        if (pc.getGfxId().getTempCharGfx() == pc.getClassId() && !pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.SHAPE_CHANGE)) {
            interval *= CodeConfig.CHECK_NO_POLY_RATE;
        }

        return interval - SprStrictTable.getInstance().findStrictValueByGfxId(pc.getGfxId().getTempCharGfx());
    }

    public enum ACT_TYPE {
        MOVE(0), ATTACK(1), SPELL_DELAY(2), SPELL_NO_DELAY(3);

        private final int actNum;

        ACT_TYPE(int actNum) {
            this.actNum = actNum;
        }

        public static ACT_TYPE valueOf(int actNum) {
            switch (actNum) {
                case 0:
                    return MOVE;
                case 1:
                    return ATTACK;
                case 2:
                    return SPELL_DELAY;
                case 3:
                    return SPELL_NO_DELAY;
            }

            return null;
        }

        public int getActNum() {
            return actNum;
        }
    }
}
