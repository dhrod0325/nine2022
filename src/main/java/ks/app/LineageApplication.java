package ks.app;

import ks.core.GameServer;
import ks.core.Server;
import ks.core.network.server.netty.L1NettyServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication(
        scanBasePackages = {"ks"},
        exclude = {DataSourceAutoConfiguration.class}
)
public class LineageApplication implements CommandLineRunner {
    static {
        System.setProperty("org.xml.sax.driver", "com.sun.org.apache.xerces.internal.parsers.SAXParser");
        System.setProperty("javax.xml.parsers.DocumentBuilderFactory", "com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl");
        System.setProperty("javax.xml.parsers.SAXParserFactory", "com.sun.org.apache.xerces.internal.jaxp.SAXParserFactoryImpl");
    }

    private final static Logger logger = LogManager.getLogger();

    public static void main(String[] args) {
        logger.info("서버가 실행됩니다");

        new SpringApplicationBuilder(LineageApplication.class)
                .web(WebApplicationType.NONE)
                .run(args);
    }

    @Override
    public void run(String... args) {
        GameServer.getInstance().initialize();

        Server server = Server.getInstance();
        server.setServer(new L1NettyServer());
        server.start();
    }
}
