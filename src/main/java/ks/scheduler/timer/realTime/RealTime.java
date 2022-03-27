package ks.scheduler.timer.realTime;

import ks.scheduler.timer.BaseTime;

import java.util.Calendar;
import java.util.TimeZone;

public class RealTime extends BaseTime {
    @Override
    protected Calendar makeCalendar(int time) {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+9")); // 한국
        cal.setTimeInMillis(0);
        cal.add(Calendar.SECOND, second);

        return cal;
    }

    @Override
    protected int makeTime(long timeMillis) {
        return (int) (timeMillis / 1000L);
    }
}
