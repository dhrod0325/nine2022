package ks.util.common;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateUtils {
    public static int getYear(long time) {
        return new Date(time).getYear();
    }

    public static int getMonth(long time) {
        return new Date(time).getMonth() + 1;
    }

    public static int getDate(long time) {
        return new Date(time).getDate();
    }

    public static String currentTime() {
        return new SimpleDateFormat("yy/MM/dd").format(new Date());
    }

    public static Calendar timestampToCalendar(Timestamp ts) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(ts.getTime());
        return cal;
    }

    public static Calendar getRealTimeCalendar() {
        return Calendar.getInstance(TimeZone.getTimeZone("GMT+9"));
    }

    public static int getTodayDate() {
        SimpleDateFormat s = new SimpleDateFormat("yyyyMMdd", Locale.KOREA);
        return Integer.parseInt(s.format(Calendar.getInstance().getTime()));
    }
}
