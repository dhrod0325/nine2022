package ks.scheduler.timer;

import ks.util.common.IntRange;

import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public abstract class BaseTime {
    protected final int second;
    protected final Calendar calendar;

    public BaseTime() {
        this(System.currentTimeMillis());
    }

    public BaseTime(long timeMillis) {
        second = makeTime(timeMillis);
        calendar = makeCalendar(second);
    }

    protected Calendar makeCalendar(int time) {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        cal.setTimeInMillis(0);
        cal.add(Calendar.SECOND, second);

        return cal;
    }

    protected abstract int makeTime(long timeMillis);

    public int get(int field) {
        return calendar.get(field);
    }

    public int getSeconds() {
        return second;
    }

    public Calendar getCalendar() {
        return (Calendar) calendar.clone();
    }

    public boolean isNight() {
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        return !IntRange.includes(hour, 6, 17); // 6:00-17:59, 낮이 아니면 true
    }

    public Instant toInstant() {
        return Instant.ofEpochSecond(second);
    }

    public Date toDate() {
        return calendar.getTime();
    }
}
