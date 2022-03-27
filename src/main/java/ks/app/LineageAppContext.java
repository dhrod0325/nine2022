package ks.app;

import ks.core.common.GeneralThreadPool;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTask;

import java.time.Instant;
import java.util.Set;

@SuppressWarnings("ALL")
public class LineageAppContext {
    private static final Logger logger = LogManager.getLogger();

    private static boolean run = false;

    private static ApplicationContext ctx;

    public static ApplicationContext getCtx() {
        return ctx;
    }

    public static void setCtx(ApplicationContext ctx) {
        LineageAppContext.ctx = ctx;
    }

    public static <T> T getBean(Class<T> clazz) {
        return ctx.getBean(clazz);
    }

    @SuppressWarnings("unchecked")
    public static <T> T getBean(String name) {
        return (T) ctx.getBean(name);
    }

    public static ThreadPoolTaskScheduler spawnTaskScheduler() {
        return (ThreadPoolTaskScheduler) getCtx().getBean("spawnTaskScheduler");
    }

    public static ThreadPoolTaskScheduler robotAiScheduler() {
        return (ThreadPoolTaskScheduler) getCtx().getBean("robotAiScheduler");
    }

    public static ThreadPoolTaskScheduler npcAiScheduler() {
        return (ThreadPoolTaskScheduler) getCtx().getBean("npcAiScheduler");
    }

    public static ThreadPoolTaskScheduler skillTaskScheduler() {
        return (ThreadPoolTaskScheduler) getCtx().getBean("skillTaskScheduler");
    }

    public static ThreadPoolTaskScheduler commonTaskScheduler() {
        return (ThreadPoolTaskScheduler) getCtx().getBean("commonTaskScheduler");
    }

    public static GeneralThreadPool generalThreadPool() {
        return (GeneralThreadPool) getCtx().getBean("generalThreadPool");
    }

    public static ThreadPoolTaskScheduler autoUpdateTaskScheduler() {
        return (ThreadPoolTaskScheduler) getCtx().getBean("autoUpdateTaskScheduler");
    }

    public static ThreadPoolTaskScheduler backUpTaskScheduler() {
        return (ThreadPoolTaskScheduler) getCtx().getBean("backUpTaskScheduler");
    }

    public static void shutdown() {
        setRun(false);

        ScheduledAnnotationBeanPostProcessor scheduledAnnotationBeanPostProcessor = getBean(ScheduledAnnotationBeanPostProcessor.class);
        Set<ScheduledTask> scheduledTasks = scheduledAnnotationBeanPostProcessor.getScheduledTasks();

        for (ScheduledTask task : scheduledTasks) {
            task.cancel();
        }

        commonTaskScheduler().schedule(() -> System.exit(0), Instant.now().plusMillis(2000));
    }

    public static boolean isRun() {
        return run;
    }

    public static void setRun(boolean run) {
        LineageAppContext.run = run;
    }
}
