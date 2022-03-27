package basic.test;

import ks.app.config.prop.ServerConfig;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.ReloadingFileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.File;

public abstract class BaseTest {
    protected static JdbcTemplate jdbcTemplate;

    public static FileBasedConfiguration fileBasedConfiguration() {
        try {
            File file = new File("data/config/", "application-local.properties");

            Parameters params = new Parameters();
            ReloadingFileBasedConfigurationBuilder<FileBasedConfiguration> builder = new ReloadingFileBasedConfigurationBuilder<FileBasedConfiguration>(PropertiesConfiguration.class)
                    .configure(params.fileBased().setFile(file));

            return builder.getConfiguration();
        } catch (Exception e) {
        }

        return null;
    }

    static {
        System.setProperty("org.xml.sax.driver", "com.sun.org.apache.xerces.internal.parsers.SAXParser");
        System.setProperty("javax.xml.parsers.DocumentBuilderFactory", "com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl");
        System.setProperty("javax.xml.parsers.SAXParserFactory", "com.sun.org.apache.xerces.internal.jaxp.SAXParserFactoryImpl");

        ServerConfig.load(fileBasedConfiguration());

        BasicDataSource ds = new BasicDataSource();
        ds.setDriverClassName(ServerConfig.DB_DRIVER);
        ds.setUrl(ServerConfig.DB_URL);
        ds.setUsername(ServerConfig.DB_LOGIN);
        ds.setPassword(ServerConfig.DB_PASSWORD);
        ds.setValidationQuery("SELECT 1");
        ds.setMaxIdle(100);

        jdbcTemplate = new JdbcTemplate(ds);
    }
}
