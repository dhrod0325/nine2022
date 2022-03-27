package ks.scheduler.timer;

import ks.app.LineageAppContext;
import ks.listener.TimeListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.ParameterizedType;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class TimeScheduler<T extends BaseTime> {
    protected final Logger logger = LogManager.getLogger();

    private final List<TimeListener> listeners = new CopyOnWriteArrayList<>();
    protected volatile T currentTime;
    protected BaseTime previousTime = null;

    public TimeScheduler() {
        try {
            currentTime = createClass();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public T createClass() {
        try {
            return ((Class<T>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0]).newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<TimeListener> getListeners() {
        return listeners;
    }

    public void run() {
        try {
            if (!LineageAppContext.isRun()) {
                return;
            }

            previousTime = currentTime;
            currentTime = createClass();

            notifyChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isFieldChanged(int field) {
        return previousTime.get(field) != currentTime.get(field);
    }

    public void notifyChanged() {
        if (isFieldChanged(Calendar.MONTH)) {
            for (TimeListener listener : listeners) {
                listener.onMonthChanged(currentTime);
            }
        }

        if (isFieldChanged(Calendar.DAY_OF_MONTH)) {
            for (TimeListener listener : listeners) {
                listener.onDayChanged(currentTime);
            }
        }

        if (isFieldChanged(Calendar.HOUR_OF_DAY)) {
            for (TimeListener listener : listeners) {
                listener.onHourChanged(currentTime);
            }
        }

        if (isFieldChanged(Calendar.MINUTE)) {
            for (TimeListener listener : listeners) {
                listener.onMinuteChanged(currentTime);
            }
        }

        if (isFieldChanged(Calendar.SECOND)) {
            for (TimeListener listener : listeners) {
                listener.onSecondChanged(currentTime);
            }
        }
    }

    public T getTime() {
        return currentTime;
    }

    public void addListener(TimeListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
            logger.info("리스너 추가 : {}", listener);
        }
    }

    public void removeListener(TimeListener listener) {
        listeners.remove(listener);
        logger.info("리스너 제거 : {}", listener);
    }
}
