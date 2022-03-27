package ks.scheduler;

import ks.model.L1CastleLocation;
import ks.model.L1PcInventory;
import ks.model.L1World;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class InWarCheckScheduler {
    @Scheduled(fixedDelay = 1000)
    public void scheduled() {
        L1World.getInstance().getAllPlayers().forEach(pc -> {
            if (L1CastleLocation.checkInAllWarArea(pc.getLocation())) {
                if (!L1CastleLocation.isInCastleInner(pc.getMapId())) {
                    L1PcInventory inventory = pc.getInventory();

                    if (inventory.checkEquipped(20077) || inventory.checkEquipped(120077) || inventory.checkEquipped(20062)) {
                        if (!pc.isGm()) {
                            pc.death(null);
                        }
                    }
                }
            }
        });
    }
}
