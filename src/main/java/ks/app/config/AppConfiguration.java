package ks.app.config;

import ks.app.LineageAppContext;
import ks.app.config.prop.ServerConfig;
import ks.core.common.GeneralThreadPool;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.ReloadingFileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import javax.sql.DataSource;
import java.io.File;
import java.util.concurrent.Executors;

@Configuration
public class AppConfiguration implements ApplicationContextAware, SchedulingConfigurer {
    private static final Logger logger = LogManager.getLogger();

    @Value("${spring.profiles.active}")
    private String profileActive;

    @Value("${spring.config.location}")
    private String configLocation;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        logger.debug("setApplicationContext");

        LineageAppContext.setCtx(applicationContext);

        ServerConfig.load();
    }

    @Bean
    @Scope("prototype")
    public FileBasedConfiguration fileBasedConfiguration() {
        try {
            String filePath = new File(configLocation.substring("file:".length())).getCanonicalPath();
            File file = new File(filePath, "application-" + profileActive + ".properties");

            Parameters params = new Parameters();
            ReloadingFileBasedConfigurationBuilder<FileBasedConfiguration> builder = new ReloadingFileBasedConfigurationBuilder<FileBasedConfiguration>(PropertiesConfiguration.class)
                    .configure(params.fileBased().setFile(file));

            return builder.getConfiguration();
        } catch (Exception e) {
            logger.error("오류", e);
        }

        return null;
    }

    @Bean(name = "dataSource")
    public DataSource dataSource() {
        BasicDataSource ds = new BasicDataSource();
        ds.setDriverClassName(ServerConfig.DB_DRIVER);
        ds.setUrl(ServerConfig.DB_URL);
        ds.setUsername(ServerConfig.DB_LOGIN);
        ds.setPassword(ServerConfig.DB_PASSWORD);
        ds.setValidationQuery("SELECT 1");
        ds.setMaxIdle(100);

        return ds;
    }

    @Bean(name = "backUpTaskScheduler")
    public ThreadPoolTaskScheduler backUpTaskScheduler() {
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.initialize();
        return taskScheduler;
    }

    @Bean(name = "commonTaskScheduler")
    public ThreadPoolTaskScheduler commonTaskScheduler() {
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(50);
        taskScheduler.initialize();

        return taskScheduler;
    }

    @Bean(name = "generalThreadPool")
    public GeneralThreadPool generalThreadPool() {
        return new GeneralThreadPool(Executors.newCachedThreadPool());
    }

    @Bean(name = "autoUpdateTaskScheduler")
    public ThreadPoolTaskScheduler autoUpdateTaskScheduler() {
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(1 + ServerConfig.SERVER_MAX_USERS / 10);
        taskScheduler.initialize();

        return taskScheduler;
    }

    @Bean(name = "skillTaskScheduler")
    public ThreadPoolTaskScheduler skillTaskScheduler() {
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(30);
        taskScheduler.initialize();
        return taskScheduler;
    }

    @Bean(name = "spawnTaskScheduler")
    public ThreadPoolTaskScheduler spawnTaskScheduler() {
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(10);
        taskScheduler.initialize();
        return taskScheduler;
    }

    @Bean(name = "robotAiScheduler")
    public ThreadPoolTaskScheduler robotAiScheduler() {
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(10);
        taskScheduler.initialize();

        return taskScheduler;
    }

    @Bean(name = "npcAiScheduler")
    public ThreadPoolTaskScheduler npcAiScheduler() {
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(30);
        taskScheduler.initialize();

        return taskScheduler;
    }

    @Bean
    public ThreadPoolTaskScheduler springScheduler() {
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(5);
        taskScheduler.initialize();

        return taskScheduler;
    }

    @Bean
    public JdbcTemplate jdbcTemplate() {
        return new JdbcTemplate(dataSource());
    }

    @Bean
    public NamedParameterJdbcTemplate namedParameterJdbcTemplate() {
        return new NamedParameterJdbcTemplate(dataSource());
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar scheduledTaskRegistrar) {
        scheduledTaskRegistrar.setTaskScheduler(springScheduler());
    }
}
