package ks.system.lastabard;

import ks.app.LineageAppContext;
import ks.core.datatables.DoorSpawnTable;
import ks.listener.TimeListenerAdapter;
import ks.model.instance.L1DoorInstance;
import ks.scheduler.timer.BaseTime;
import ks.scheduler.timer.realTime.RealTimeScheduler;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class LastabardListener extends TimeListenerAdapter {
    private final List<LastabardTime> timeList = new CopyOnWriteArrayList<>();

    public static LastabardListener getInstance() {
        return LineageAppContext.getBean(LastabardListener.class);
    }

    @Override
    public void onSecondChanged(BaseTime time) {
        for (LastabardTime lastabardTime : timeList) {
            if (lastabardTime.isTimeOver(time.getSeconds())) {
                closeTime(lastabardTime);
            }
        }
    }

    public void closeTimeAll() {
        for (LastabardTime lastabardTime : timeList) {
            closeTime(lastabardTime);
        }
    }

    public void closeTime(LastabardTime lastabardTime) {
        reset(lastabardTime);
        timeList.remove(lastabardTime);
    }

    private void reset(LastabardTime lastabardTime) {
        int mapId = lastabardTime.getMapId();
        int relatedDoor = lastabardTime.getRelatedDoor();

        if (mapId != 0) {
            LastabardData.doHomeTeleport(mapId);
        }

        if (relatedDoor == 0) {
            return;
        }

        L1DoorInstance door = DoorSpawnTable.getInstance().getDoor(relatedDoor);

        if (door != null) {
            door.setDead(false);
            door.close();
        }
    }

    public void addDelayTime(int doorId, int countMapId) {
        int delayTime = LastabardData.getDelayTime(countMapId);

        if (delayTime <= 0) {
            return;
        }

        if (countMapId == 0) {
            return;
        }

        BaseTime t = RealTimeScheduler.getInstance().getTime();

        int sec = t.getSeconds();

        LastabardTime time = new LastabardTime(countMapId, sec, delayTime, doorId);
        timeList.add(time);

        additionalDelayTime(countMapId);
    }

    private void additionalDelayTime(int mapId) {
        if (mapId == 0) {
            return;
        }

        int relatedMapId = LastabardData.relatedTime(mapId);

        if (relatedMapId != 0) {
            addDelayTime(relatedMapId, 0);
        }
    }
}
