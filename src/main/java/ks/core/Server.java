package ks.core;

import ks.app.config.prop.ServerConfig;
import ks.core.network.L1Server;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Server {
    private static final Logger logger = LogManager.getLogger();

    private static final Server instance = new Server();

    private L1Server server;

    public static Server getInstance() {
        return instance;
    }

    public void setServer(L1Server server) {
        this.server = server;
    }

    public void start() {
        try {
            server.start(ServerConfig.SERVER_PORT);
            finishedStart();
        } catch (Exception e) {
            logger.error("오류", e);
        }
    }

    public void shutDown() {
        server.shutDown();
    }

    private void finishedStart() {
        logger.info("＊--------------------------------------------------＊");
        logger.info("서버포트 : {}", ServerConfig.SERVER_PORT);
        logger.info("서버이름 : {}", ServerConfig.SERVER_NAME);
        logger.info("홈페이지 : {}", ServerConfig.SERVER_HOMEPAGE);
        logger.info("＊--------------------------------------------------＊");
    }
}
