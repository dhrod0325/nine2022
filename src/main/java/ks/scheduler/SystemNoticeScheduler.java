package ks.scheduler;

import ks.app.LineageAppContext;
import ks.model.L1World;
import ks.model.txt.L1Sys;
import ks.packets.serverpackets.S_SystemMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SystemNoticeScheduler {
    private int count = 0;

    private final Logger logger = LogManager.getLogger();

    @Scheduled(fixedDelay = 1000 * 60 * 5)
    public void scheduled() {
        if (!LineageAppContext.isRun()) {
            return;
        }

        List<String> msgList = L1Sys.getInstance().getList();

        try {
            if (msgList.isEmpty()) {
                return;
            }

            if (count > msgList.size() - 1) {
                count = 0;
            }

            L1World.getInstance().broadcastPacketToAll(new S_SystemMessage(msgList.get(count++)));
        } catch (Exception e) {
            logger.error("오류", e);
        }
    }
}
