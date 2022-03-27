package ks.model.skill;

import ks.app.LineageAppContext;
import ks.model.L1Character;

public class L1SkillTimerThreadImpl extends Thread implements L1SkillTimer {
    private final L1Character cha;

    private final int timeMillis;

    private final int skillId;

    private int remainingTime;

    private int timeCount;

    public L1SkillTimerThreadImpl(L1Character cha, int skillId, int timeMillis) {
        this.cha = cha;
        this.skillId = skillId;
        this.timeMillis = timeMillis;
    }

    @Override
    public void run() {
        for (timeCount = timeMillis / 1000; timeCount > 0; timeCount--) {
            try {
                Thread.sleep(1000);
                remainingTime = timeCount;
            } catch (InterruptedException e) {
                return;
            }
        }

        cha.getSkillEffectTimerSet().removeSkillEffect(skillId);
    }

    public int getRemainingTime() {
        return remainingTime;
    }

    public void begin() {
        LineageAppContext.skillTaskScheduler().execute(this);
    }

    public void end() {
        L1SkillStop.stopSkill(cha, skillId);
        interrupt();
    }

    public void kill() {
        if (Thread.currentThread().getId() == super.getId()) {
            return;
        }

        timeCount = 0;
    }
}
