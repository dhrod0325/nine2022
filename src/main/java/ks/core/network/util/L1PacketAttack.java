package ks.core.network.util;

import ks.app.config.prop.CodeConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class L1PacketAttack {
    private static final Logger logger = LogManager.getLogger(L1PacketAttack.class);
    private long lastCheckTime = System.currentTimeMillis();

    private int packetSendCount = 0;

    public boolean isPacketAttack() {
        try {
            packetSendCount++;

            long interval = System.currentTimeMillis() - lastCheckTime;

            if (interval > CodeConfig.PACKET_ATTACK_INTERVAL) {
                if (packetSendCount > CodeConfig.PACKET_ATTACK_COUNT) {
                    packetSendCount = 0;
                    lastCheckTime = System.currentTimeMillis();
                    logger.info("패킷공격 의심 : ");
                    return true;
                } else {
                    packetSendCount = 0;
                    lastCheckTime = System.currentTimeMillis();
                }
            }
        } catch (Exception e) {
            logger.error(e);
        }

        return false;
    }
}
