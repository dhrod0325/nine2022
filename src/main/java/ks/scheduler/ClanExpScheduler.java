package ks.scheduler;

import ks.app.LineageAppContext;
import ks.model.L1World;
import ks.util.L1ClanUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class ClanExpScheduler {
    private final Map<Integer, Double> oldExpMap = new HashMap<>();

    @Scheduled(fixedDelay = 1000 * 60)
    public void scheduled() {
        if (!LineageAppContext.isRun()) {
            return;
        }

        L1World.getInstance().getAllClans().forEach(clan -> {
            double oldExp = oldExpMap.getOrDefault(clan.getClanId(), 0d);

            if (oldExp != clan.getExp()) {
                L1ClanUtils.updateClan(clan);
            }

            oldExpMap.put(clan.getClanId(), clan.getExp());
        });
    }
}
