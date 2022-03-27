package ks.system.boss.model;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import java.util.Calendar;
import java.util.Date;

public class L1BossTime implements Comparable<L1BossTime> {
    private int hour;
    private int minute;

    private boolean random;
    private int randomHour;
    private int randomMinute;

    public int getRandomHour() {
        return randomHour;
    }

    public void setRandomHour(int randomHour) {
        this.randomHour = randomHour;
    }

    public int getRandomMinute() {
        return randomMinute;
    }

    public void setRandomMinute(int randomMinute) {
        this.randomMinute = randomMinute;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public L1BossTime() {
    }

    public L1BossTime(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);

        setHour(c.get(Calendar.HOUR_OF_DAY));
        setMinute(c.get(Calendar.MINUTE));
    }

    public Calendar toCalendar() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, getHour());
        c.set(Calendar.MINUTE, getMinute());

        return c;
    }

    public boolean isRandom() {
        return random;
    }

    public void setRandom(boolean random) {
        this.random = random;
    }

    @Override
    public int compareTo(L1BossTime o) {
        return toCalendar().compareTo(o.toCalendar());
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}
