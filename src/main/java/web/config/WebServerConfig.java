package web.config;

import ks.app.LineageAppContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.env.Environment;

public class WebServerConfig {
    private static final Logger logger = LogManager.getLogger();

    public static Integer WEB_SERVER_PORT;
    public static Boolean WEB_API_USE;
    public static String WEB_API_HOST;
    public static Integer WEB_API_PORT;
    public static String WEB_API_URL;
    public static Long WEB_API_INTERVAL;

    static {
        load();
    }

    public static void load() {
        try {
            Environment env = LineageAppContext.getCtx().getBean(Environment.class);

            WEB_SERVER_PORT = env.getProperty("l1j.web-server.port", Integer.class);

            WEB_API_USE = env.getProperty("l1j.web-api.use", Boolean.class);
            WEB_API_HOST = env.getProperty("l1j.web-api.host", String.class);
            WEB_API_PORT = env.getProperty("l1j.web-api.port", Integer.class);
            WEB_API_URL = env.getProperty("l1j.web-api.url", String.class);
            WEB_API_INTERVAL = env.getProperty("l1j.web-api.interval", Long.class);
        } catch (Exception e) {
            logger.error("오류", e);
        }
    }

    public static String getWebManagerUrl() {
        return WEB_API_HOST + ":" + WEB_API_PORT;
    }
}
