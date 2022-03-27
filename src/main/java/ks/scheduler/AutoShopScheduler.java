package ks.scheduler;

import ks.model.L1World;
import ks.system.userShop.L1UserShopNpcInstance;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class AutoShopScheduler {
    private static final Logger logger = LogManager.getLogger(AutoShopScheduler.class);

    @Scheduled(fixedDelay = 1000 * 10)
    public void scheduled() {
        L1World.getInstance().getAllAutoNpcShops().forEach(L1UserShopNpcInstance::shopChatChange);
    }
}
