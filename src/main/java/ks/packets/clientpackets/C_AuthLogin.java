package ks.packets.clientpackets;

import ks.app.LineageAppContext;
import ks.core.auth.Authorization;
import ks.core.network.L1Client;
import ks.core.network.util.L1ConnectDelay;
import ks.packets.serverpackets.S_Notice;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Instant;

public class C_AuthLogin extends ClientBasePacket {
    private static final Logger logger = LogManager.getLogger();

    public C_AuthLogin(byte[] decrypt, L1Client client) {
        super(decrypt);

        try {
            client.setAccount(null);

            String ip = client.getIp();
            String host = client.getHostname();

            L1ConnectDelay.Ip delayIp = L1ConnectDelay.getInstance().getIp(ip);

            if (delayIp.block) {
                client.sendPacket(new S_Notice("아이피 공격 의심자입니다\n60초 후에 다시 접속을 시도해주시기 바랍니다\n접속기를 연속해서 누르시면 안됩니다\n계속 해서 접근이 되지 않으시면 운영자에게 문의하세요"));
                LineageAppContext.commonTaskScheduler().schedule(client::disconnect, Instant.now().plusMillis(5000));
                return;
            }

            client.setClientLoginCheck(true);

            String accountName = readS().toLowerCase();
            String password = readS();

            Authorization.getInstance().auth(client, accountName, password, ip, host);
        } catch (Exception e) {
            logger.error(e);
        }
    }
}
