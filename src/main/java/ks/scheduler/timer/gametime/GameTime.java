package ks.scheduler.timer.gametime;

import ks.scheduler.timer.BaseTime;

public class GameTime extends BaseTime {
    @Override
    protected int makeTime(long timeMillis) {
        int t2 = (int) ((timeMillis * 6) / 1000L);
        int t3 = t2 % 3;

        return t2 - t3;
    }
}
