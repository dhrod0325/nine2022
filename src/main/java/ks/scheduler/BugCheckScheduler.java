package ks.scheduler;

import ks.app.LineageAppContext;
import ks.model.L1World;
import ks.util.log.L1LogUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class BugCheckScheduler {
    private static final Logger logger = LogManager.getLogger(BugCheckScheduler.class);

    @Scheduled(fixedDelay = 1000 * 60)
    public void statCheck() {
        if (!LineageAppContext.isRun()) {
            return;
        }

        L1World.getInstance().getAllPlayers().forEach(pc -> {
            try {
                if (pc.isGm()) {
                    return;
                }

                int lvl = pc.getLevel();
                int str = pc.getAbility().getStr();
                int dex = pc.getAbility().getDex();
                int con = pc.getAbility().getCon();
                int wis = pc.getAbility().getWis();
                int ints = pc.getAbility().getInt();
                int cha = pc.getAbility().getCha();

                int totalCount;
                int statCount = str + dex + con + wis + ints + cha;
                int bonusCount = 0;

                if (lvl > 50) {
                    bonusCount += lvl - 50;
                }

                int eCount = 75 + pc.getAbility().getElixirCount();

                totalCount = statCount - bonusCount - pc.getAbility().getElixirCount();

                if (totalCount > eCount && (totalCount - eCount) > 1) {
                    L1LogUtils.bugLog("스텟버그 의심자 : " + pc.getName() + ", 올바른스텟 : " + eCount + "," + "현재스텟 : " + totalCount);
                }
            } catch (Exception e) {
                logger.error(e);
            }
        });
    }
}
