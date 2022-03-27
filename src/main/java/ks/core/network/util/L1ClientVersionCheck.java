package ks.core.network.util;

import ks.app.LineageAppContext;
import ks.core.network.L1Client;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Instant;

public class L1ClientVersionCheck implements Runnable {
    private static final Logger logger = LogManager.getLogger(L1ClientVersionCheck.class);

    private final L1Client client;

    public L1ClientVersionCheck(L1Client client) {
        this.client = client;
    }

    @Override
    public void run() {
        if (client != null && !client.isClientVersionCheck()) {
            try {
                client.disconnectNow();
                logger.info("DISCONNECT CLIENT SessionOpenCk IP : " + client.getIp());
            } catch (Exception e) {
                logger.error("오류", e);
            }
        } else {
            LineageAppContext.commonTaskScheduler().schedule(new L1LoginCheck(client), Instant.now().plusMillis(1000 * 60));
        }
    }
}
