package ks.scheduler;

import ks.app.LineageAppContext;
import ks.model.L1World;
import ks.model.rank.L1RankChecker;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class RankCheckScheduler {
    @Scheduled(fixedDelay = 1000 * 60)
    public void scheduled1() {
        if (!LineageAppContext.isRun())
            return;

        L1RankChecker.getInstance().load();
    }

    @Scheduled(fixedDelay = 1000 * 60)
    public void scheduled2() {
        if (!LineageAppContext.isRun())
            return;

        L1World.getInstance().getAllPlayers()
                .stream()
                .filter(Objects::nonNull)
                .forEach(pc -> {
                    pc.getRankBuff().startBuff();
                    pc.getClanRankBuff().startBuff();
                });

    }
}
