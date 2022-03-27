package ks.model.poison;

import ks.app.LineageAppContext;
import ks.constants.L1SkillId;
import ks.model.L1Character;
import ks.model.instance.L1MonsterInstance;
import ks.model.pc.L1PcInstance;

import java.util.concurrent.ScheduledFuture;

public class L1DamagePoison extends L1Poison {
    private final L1Character attacker;
    private final L1Character target;
    private final int damageSpan;
    private final int damage;
    private final NormalPoisonTimer poisonTimer = new NormalPoisonTimer();
    private int durationSecond;

    private L1DamagePoison(L1Character attacker, L1Character target, int damageSpan, int damage, int durationSecond) {
        this.attacker = attacker;
        this.target = target;
        this.damageSpan = damageSpan;
        this.damage = damage;
        this.durationSecond = durationSecond;

        doInfection();
    }

    public static void doInfection(L1Character attacker, L1Character cha, int damageSpan, int damage) {
        doInfection(attacker, cha, damageSpan, damage, 0);
    }

    public static void doInfection(L1Character attacker, L1Character cha, int damageSpan, int damage, int durationSecond) {
        if (isNotValidTarget(cha)) {
            return;
        }

        cha.setPoison(new L1DamagePoison(attacker, cha, damageSpan, damage, durationSecond));
    }

    boolean isDamageTarget(L1Character cha) {
        return (cha instanceof L1PcInstance) || (cha instanceof L1MonsterInstance);
    }

    private void doInfection() {
        if (durationSecond == 0) {
            durationSecond = 30;
        }

        target.getSkillEffectTimerSet().setSkillEffect(L1SkillId.STATUS_POISON, 1000 * durationSecond);
        target.setPoisonEffect(1);

        if (isDamageTarget(target)) {
            poisonTimer.start();
        }
    }

    @Override
    public int getEffectId() {
        return 1;
    }

    @Override
    public void cure() {
        poisonTimer.stop();

        target.setPoisonEffect(0);
        target.getSkillEffectTimerSet().killSkillEffectTimer(L1SkillId.STATUS_POISON);
        target.setPoison(null);
    }

    private class NormalPoisonTimer implements Runnable {
        private ScheduledFuture<?> timerFuture;

        public void start() {
            stop();
            timerFuture = LineageAppContext.commonTaskScheduler().scheduleAtFixedRate(this, damageSpan);
        }

        public void stop() {
            if (timerFuture != null) {
                timerFuture.cancel(true);
                timerFuture = null;
                cure();
            }
        }

        @Override
        public void run() {
            if (!target.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.STATUS_POISON)) {
                stop();
            }

            if (target instanceof L1PcInstance) {
                L1PcInstance player = (L1PcInstance) target;
                player.receiveDamage(attacker, damage);

                if (player.isDead()) {
                    stop();
                }
            } else if (target instanceof L1MonsterInstance) {
                L1MonsterInstance mob = (L1MonsterInstance) target;
                mob.receiveDamage(attacker, damage);

                if (mob.isDead()) {
                    stop();
                }
            }
        }
    }
}
