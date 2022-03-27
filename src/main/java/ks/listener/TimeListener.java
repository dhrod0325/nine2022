package ks.listener;

import ks.scheduler.timer.BaseTime;

public interface TimeListener {
    void onMonthChanged(BaseTime time);

    void onDayChanged(BaseTime time);

    void onHourChanged(BaseTime time);

    void onMinuteChanged(BaseTime time);

    void onSecondChanged(BaseTime time);
}
