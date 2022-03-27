package ks.core.network;

import ks.app.LineageAppContext;
import ks.constants.L1Options;
import ks.core.datatables.IpTable;
import ks.core.network.util.L1ClientManager;
import ks.core.network.util.L1ClientVersionCheck;
import ks.core.network.util.L1ConnectDelay;
import ks.core.network.util.L1NetworkUtils;
import ks.packets.serverpackets.KeyPacket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Instant;
import java.util.StringTokenizer;

public class L1ProtocolHandler {
    private static final Logger logger = LogManager.getLogger(L1ProtocolHandler.class);

    public void sessionCreated(L1Session session) {
        try {
            StringTokenizer st = new StringTokenizer(session.getRemoteAddress().substring(1), ":");
            String ip = st.nextToken();

            if (IpTable.getInstance().isBanned(ip)) {
                session.close(true);
            }

            if (st.nextToken().startsWith("0")) {
                session.close(true);
            }

            if (L1ConnectDelay.getInstance().isManyApplyConnection(ip)) {
                session.close(true);
            }
        } catch (Exception e) {
            logger.error("오류", e);
        }

        logger.trace("sessionCreated");
    }

    public void sessionOpened(L1Session session) {
        try {
            if (!session.isClosed()) {
                if (L1NetworkUtils.isPortAttack(session.getRemoteAddress())) {
                    session.close(true);
                    return;
                }

                KeyPacket key = new KeyPacket();
                session.write(key);

                L1Client client = new L1Client(session);
                session.setAttribute(L1Options.CLIENT_KEY, client);

                LineageAppContext.commonTaskScheduler().schedule(new L1ClientVersionCheck(client), Instant.now().plusMillis(1000));

                L1ClientManager.getInstance().add(client);
            }
        } catch (Exception e) {
            logger.error("오류", e);
        }

        logger.trace("sessionOpened");
    }

    public void sessionClosed(L1Session session) {
        L1Client client = (L1Client) session.getAttribute(L1Options.CLIENT_KEY);

        if (client != null) {
            client.disconnect();
            client.queueNotify();
            logger.trace("clientClosed");
        }

        L1ClientManager.getInstance().remove(client);
    }
}
