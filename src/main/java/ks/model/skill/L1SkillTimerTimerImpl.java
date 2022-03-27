package ks.model.skill;

import ks.app.LineageAppContext;
import ks.model.L1Character;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ScheduledFuture;

public class L1SkillTimerTimerImpl implements L1SkillTimer, Runnable {
    private final Logger logger = LogManager.getLogger();

    private final L1Character target;
    private final int skillId;

    private ScheduledFuture<?> future = null;
    private int remainingTime;

    public L1SkillTimerTimerImpl(L1Character target, int skillId, int timeMillis) {
        this.target = target;
        this.skillId = skillId;
        this.remainingTime = timeMillis / 1000;
    }

    @Override
    public void run() {
        remainingTime--;

        if (remainingTime <= 0) {
            target.getSkillEffectTimerSet().removeSkillEffect(skillId);
        }
    }

    @Override
    public void begin() {
        future = LineageAppContext.skillTaskScheduler().scheduleAtFixedRate(this, Instant.now().plusMillis(1000), Duration.ofMillis(1000));
    }

    @Override
    public void end() {
        stopSkill();
        kill();
    }

    public void stopSkill() {
        try {
            L1SkillStop.stopSkill(target, skillId);
        } catch (Throwable e) {
            logger.error("오류", e);
        }
    }

    @Override
    public void kill() {
        if (future != null) {
            future.cancel(false);
        }
    }

    @Override
    public int getRemainingTime() {
        return remainingTime;
    }

    public int getSkillId() {
        return skillId;
    }
}
