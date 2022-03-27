package ks.scheduler;

import ks.app.LineageAppContext;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_SkillSound;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class DollHpMpRegenScheduler {
    private static final Logger logger = LogManager.getLogger(DollHpMpRegenScheduler.class);

    private final List<L1PcInstance> hpList = new CopyOnWriteArrayList<>();
    private final List<L1PcInstance> mpList = new CopyOnWriteArrayList<>();

    public static DollHpMpRegenScheduler getInstance() {
        return LineageAppContext.getCtx().getBean(DollHpMpRegenScheduler.class);
    }

    @Scheduled(fixedDelay = 1000)
    public void scheduled() {
        if (!LineageAppContext.isRun()) {
            return;
        }

        hpList.stream().filter(Objects::nonNull).forEach(pc -> {
            try {
                if (pc.isDead())
                    return;

                if (!pc.isUsingDoll())
                    return;

                if (pc.getCurrentDoll().getAbHpr() == 0) {
                    return;
                }

                if (pc.getDollHPRegenTime() <= System.currentTimeMillis()) {
                    regenHp(pc);
                    pc.setDollHpRegenTime(System.currentTimeMillis() + (pc.getCurrentDoll().getAbHprTime() * 1000L));
                }
            } catch (Exception e) {
                logger.error(e);
            }
        });

        mpList.stream().filter(Objects::nonNull).forEach(pc -> {
            try {
                if (pc.isDead())
                    return;

                if (!pc.isUsingDoll())
                    return;

                if (pc.getCurrentDoll().getAbMpr() == 0) {
                    return;
                }

                if (pc.getDollMPRegenTime() <= System.currentTimeMillis()) {
                    regenMp(pc);
                    pc.setDollMpRegenTime(System.currentTimeMillis() + (pc.getCurrentDoll().getAbMprTime() * 1000L));
                }
            } catch (Exception e) {
                logger.error(e);
            }
        });
    }

    public void registerHpRegen(L1PcInstance pc) {
        if (!hpList.contains(pc))
            hpList.add(pc);
    }

    public void registerMpRegen(L1PcInstance pc) {
        if (!mpList.contains(pc))
            mpList.add(pc);
    }

    public void removeHp(L1PcInstance pc) {
        hpList.remove(pc);
    }

    public void removeMp(L1PcInstance pc) {
        mpList.remove(pc);
    }

    public void regenHp(L1PcInstance pc) {
        int newHp = pc.getCurrentHp();
        int healHp = pc.getCurrentDoll().getAbHpr();

        if (healHp == 0) {
            return;
        }

        newHp = newHp + healHp;

        if (newHp < 0) {
            newHp = 0;
        }

        pc.setCurrentHp(newHp);
        pc.sendPackets(new S_SkillSound(pc.getId(), 1608));
    }

    public void regenMp(L1PcInstance pc) {
        int regenMp = pc.getCurrentDoll().getAbMpr();

        if (regenMp == 0) {
            return;
        }

        int newMp = pc.getCurrentMp() + regenMp;

        pc.setCurrentMp(newMp);
        pc.sendPackets(new S_SkillSound(pc.getId(), 6321));
    }
}
