package ks.app.config.prop;

import ks.app.LineageAppContext;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.env.Environment;

public final class ServerConfig {
    private static final Logger logger = LogManager.getLogger();

    public static String SERVER_NAME;
    public static String SERVER_HOMEPAGE;

    public static String SERVER_HOST_NAME;
    public static Integer SERVER_PORT;
    public static Integer SERVER_TYPE;

    public static String DB_DRIVER;
    public static String DB_URL;
    public static String DB_LOGIN;
    public static String DB_PASSWORD;

    public static Integer SERVER_MAX_USERS;
    public static String SERVER_TIME_ZONE;

    public static String PROFILE_ACTIVE;

    public static void load() {
        Environment env = LineageAppContext.getBean(Environment.class);

        PROFILE_ACTIVE = env.getProperty("spring.profiles.active");

        FileBasedConfiguration configuration = LineageAppContext.getBean(FileBasedConfiguration.class);
        load(configuration);
    }

    public static void load(FileBasedConfiguration configuration) {
        try {
            logger.debug("Load Server Config");

            SERVER_MAX_USERS = configuration.getInt("l1j.server.user.max");

            SERVER_NAME = configuration.getString("l1j.server.name");
            SERVER_HOMEPAGE = configuration.getString("l1j.server.homepage");

            SERVER_HOST_NAME = configuration.getString("l1j.server.host");
            SERVER_PORT = configuration.getInt("l1j.server.port");
            SERVER_TIME_ZONE = configuration.getString("l1j.server.time-zone");

            DB_DRIVER = configuration.getString("l1j.datasource.driver-class-name");
            DB_URL = configuration.getString("l1j.datasource.url");
            DB_LOGIN = configuration.getString("l1j.datasource.username");
            DB_PASSWORD = configuration.getString("l1j.datasource.password");

            SERVER_TYPE = "local".equals(PROFILE_ACTIVE) ? 0 : 1;
        } catch (Exception e) {
            logger.error("서버 로딩 오류", e);
        }
    }

    public static boolean isTest() {
        //return "local".equalsIgnoreCase(PROFILE_ACTIVE);
        return false;
    }
}
