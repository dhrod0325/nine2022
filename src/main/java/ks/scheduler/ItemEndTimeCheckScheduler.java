package ks.scheduler;

import ks.app.LineageAppContext;
import ks.model.L1Inventory;
import ks.model.L1World;
import ks.model.instance.L1ItemInstance;
import ks.packets.serverpackets.S_SystemMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
public class ItemEndTimeCheckScheduler {
    private static final Logger logger = LogManager.getLogger(ItemEndTimeCheckScheduler.class);

    @Scheduled(fixedDelay = 1000 * 60)
    public void run() {
        if (!LineageAppContext.isRun()) {
            return;
        }

        long currentTimeMillis = System.currentTimeMillis();

        L1World.getInstance().getAllPlayers().forEach(pc -> {
            L1Inventory pcInventory = pc.getInventory();

            List<L1ItemInstance> items = pcInventory.getItems();

            items.stream().filter(Objects::nonNull).forEach(item -> {
                try {
                    if (item.getEndTime() == null)
                        return;

                    if (currentTimeMillis > item.getEndTime().getTime()) {
                        pcInventory.removeItem(item);
                        pc.sendPackets(new S_SystemMessage(item.getName() + "의 사용시간이 만료 되어 소멸 되었습니다."));
                    }
                } catch (Exception e) {
                    logger.error(e);
                }
            });
        });
    }
}

