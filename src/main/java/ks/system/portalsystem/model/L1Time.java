package ks.system.portalsystem.model;

import java.util.Calendar;
import java.util.Date;

public class L1Time {
    private int hour;
    private int minute;

    public L1Time(int hour, int minute) {
        this.hour = hour;
        this.minute = minute;
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

    public Date getTime() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.SECOND, 0);

        return cal.getTime();
    }

    @Override
    public String toString() {
        return "L1Time{" +
                "hour=" + hour +
                ", minute=" + minute +
                '}';
    }
}
