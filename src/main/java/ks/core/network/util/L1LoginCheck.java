package ks.core.network.util;

import ks.core.network.L1Client;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class L1LoginCheck implements Runnable {
    private static final Logger logger = LogManager.getLogger(L1LoginCheck.class);

    private final L1Client client;

    public L1LoginCheck(L1Client client) {
        this.client = client;
    }

    @Override
    public void run() {
        if (client != null) {
            if (!client.isClientLoginCheck()) {
                try {
                    client.disconnectNow();
                    logger.trace("DISCONNECT CLIENT SessionOpenLoginCk IP : " + client.getIp());
                } catch (Exception e) {
                    logger.error("오류", e);
                }
            }
        }
    }
}
