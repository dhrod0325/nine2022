package ks.scheduler.timer.realTime;

import ks.app.LineageAppContext;
import ks.scheduler.timer.TimeScheduler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class RealTimeScheduler extends TimeScheduler<RealTime> {
    public static RealTimeScheduler getInstance() {
        return LineageAppContext.getBean(RealTimeScheduler.class);
    }

    @Scheduled(fixedDelay = 500)
    public void scheduled() {
        super.run();
    }
}
