package ks.model.poison;

import ks.constants.L1SkillId;
import ks.model.L1Character;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_Paralysis;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class L1ParalysisPoison extends L1Poison {
    private final Logger logger = LogManager.getLogger();

    private final L1Character target;
    private final int delay;
    private final int time;
    private Thread timer;
    private int effectId = 1;

    private L1ParalysisPoison(L1Character target, int delay, int time) {
        this.target = target;
        this.delay = delay;
        this.time = time;

        doInfection();
    }

    public static void doInfection(L1Character cha, int delay, int time) {
        if (L1Poison.isNotValidTarget(cha)) {
            return;
        }

        cha.setPoison(new L1ParalysisPoison(cha, delay, time));
    }

    private void doInfection() {
        sendMessageIfPlayer(target, 212);
        target.setPoisonEffect(1);

        if (target instanceof L1PcInstance) {
            timer = new ParalysisPoisonTimer();
            timer.start();
        }
    }

    @Override
    public int getEffectId() {
        return effectId;
    }

    @Override
    public void cure() {
        if (timer != null) {
            timer.interrupt();
        }

        target.setPoisonEffect(0);
        target.setPoison(null);
    }

    private class ParalysisPoisonTimer extends Thread {
        @Override
        public void run() {
            target.getSkillEffectTimerSet().setSkillEffect(L1SkillId.STATUS_POISON_PARALYZING, 0);

            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                target.getSkillEffectTimerSet().killSkillEffectTimer(L1SkillId.STATUS_POISON_PARALYZING);
                return;
            }

            effectId = 2;
            target.setPoisonEffect(2);

            if (target instanceof L1PcInstance) {
                L1PcInstance player = (L1PcInstance) target;

                if (!player.isDead()) {
                    player.sendPackets(new S_Paralysis(1, true));

                    timer = new ParalysisTimer();
                    timer.start();

                    if (isInterrupted()) {
                        timer.interrupt();
                    }
                }
            }
        }
    }

    private class ParalysisTimer extends Thread {
        @Override
        public void run() {
            target.getSkillEffectTimerSet().killSkillEffectTimer(L1SkillId.STATUS_POISON_PARALYZING);
            target.getSkillEffectTimerSet().setSkillEffect(L1SkillId.STATUS_POISON_PARALYZED, 0);

            try {
                Thread.sleep(time);
            } catch (InterruptedException e) {
                logger.error("오류", e);
            }

            target.getSkillEffectTimerSet().killSkillEffectTimer(L1SkillId.STATUS_POISON_PARALYZED);

            if (target instanceof L1PcInstance) {
                L1PcInstance player = (L1PcInstance) target;

                if (!player.isDead()) {
                    player.sendPackets(new S_Paralysis(1, false));
                    cure();
                }
            }
        }
    }
}
