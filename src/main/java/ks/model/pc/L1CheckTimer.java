package ks.model.pc;

import java.util.HashMap;
import java.util.Map;

import static ks.scheduler.AutoSaveScheduler.AUTO_SAVE_KEY;

public class L1CheckTimer {
    private final Map<String, Long> timeMap = new HashMap<>();

    public void setWaitTime(String key, long timeMillis) {
        timeMap.put(key, System.currentTimeMillis() + timeMillis);
    }

    public boolean isTimeOver(String key) {
        Long time = timeMap.get(key);

        if (time == null) {
            return true;
        }

        return System.currentTimeMillis() > time;
    }

    public long remainingMillis(String key) {
        Long time = timeMap.get(key);

        if (time == null) {
            return 0;
        }

        return time - System.currentTimeMillis();
    }

    public long remainingSecond(String key) {
        long time = remainingMillis(key);

        return (time / 1000) + 1;
    }
}