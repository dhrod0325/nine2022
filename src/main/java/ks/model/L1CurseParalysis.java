package ks.model;

import ks.app.LineageAppContext;
import ks.constants.L1SkillId;
import ks.model.instance.L1MonsterInstance;
import ks.model.instance.L1NpcInstance;
import ks.model.pc.L1PcInstance;
import ks.model.skill.utils.L1SkillUtils;
import ks.packets.serverpackets.S_NPCPack;
import ks.packets.serverpackets.S_Paralysis;
import ks.packets.serverpackets.S_ServerMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Instant;
import java.util.concurrent.ScheduledFuture;

public class L1CurseParalysis extends L1Paralysis {
    private static final Logger logger = LogManager.getLogger(L1CurseParalysis.class);

    private final L1Character target;

    private final int delay;

    private final int time;

    private final Paralysis paralysis = new Paralysis();

    private L1CurseParalysis(L1Character cha, int delay, int time) {
        this.target = cha;
        this.delay = delay;
        this.time = time;

        curse();
    }

    public static void curse(L1Character cha, int delay, int time) {
        if (!(cha instanceof L1PcInstance || cha instanceof L1MonsterInstance)) {
            return;
        }

        if (cha.getSkillEffectTimerSet().hasSkillEffect(L1SkillUtils.CURSE_SKILLS)) {
            return;
        }

        cha.setParalaysis(new L1CurseParalysis(cha, delay, time));
    }

    private void curse() {
        if (target instanceof L1PcInstance) {
            L1PcInstance player = (L1PcInstance) target;
            player.sendPackets(new S_ServerMessage(212));
        }

        target.setPoisonEffect(2);

        paralysis.start();
    }

    @Override
    public int getEffectId() {
        return 2;
    }

    @Override
    public void cure() {
        target.setPoisonEffect(0);
        target.getSkillEffectTimerSet().removeSkillEffect(L1SkillUtils.CURSE_SKILLS);
        target.setParalyzed(false);
        target.setParalaysis(null);

        if (target instanceof L1PcInstance) {
            L1PcInstance player = (L1PcInstance) target;
            if (!player.isDead()) {
                player.sendPackets(new S_Paralysis(1, false));
            }
        } else if (target instanceof L1NpcInstance) {
            L1NpcInstance npc = (L1NpcInstance) target;

            if (!npc.isDead()) {
                Broadcaster.broadcastPacket(npc, new S_NPCPack(npc));
            }
        }

        paralysis.stop();
    }

    private class Paralysis implements Runnable {
        private ScheduledFuture<?> delayFutre;
        private ScheduledFuture<?> cureFutre;

        public void start() {
            stop();

            delayFutre = LineageAppContext.commonTaskScheduler().schedule(this, Instant.now().plusMillis(delay));
        }

        public void stop() {
            if (delayFutre != null) {
                delayFutre.cancel(true);
                delayFutre = null;
            }

            if (cureFutre != null) {
                cureFutre.cancel(true);
                cureFutre = null;
            }
        }

        @Override
        public void run() {
            target.getSkillEffectTimerSet().setSkillEffect(L1SkillId.STATUS_CURSE_PARALYZING, 0);

            if (target.getParalysis() == null) {
                return;
            }

            if (target instanceof L1PcInstance) {
                L1PcInstance player = (L1PcInstance) target;
                if (!player.isDead()) {
                    player.sendPackets(new S_Paralysis(1, true));
                }
            }

            target.setParalyzed(true);

            if (cureFutre != null) {
                cureFutre.cancel(true);
            }

            cureFutre = LineageAppContext.commonTaskScheduler().schedule(() -> {
                target.getSkillEffectTimerSet().removeSkillEffect(L1SkillId.STATUS_CURSE_PARALYZING);

                cure();
            }, Instant.now().plusMillis(time));
        }
    }
}
