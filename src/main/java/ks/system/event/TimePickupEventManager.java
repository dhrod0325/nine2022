package ks.system.event;

import ks.app.LineageAppContext;
import ks.model.L1Location;
import ks.model.L1Teleport;
import ks.model.pc.L1PcInstance;
import ks.util.common.random.RandomUtils;
import org.springframework.stereotype.Component;

@Component
public class TimePickupEventManager {
    private TimePickupEvent event;

    public static TimePickupEventManager getInstance() {
        return LineageAppContext.getBean(TimePickupEventManager.class);
    }

    public void start() {
        if (event != null) {
            event.stop();
        }

        event = new TimePickupEvent();
        event.start();
    }

    public TimePickupEvent getEvent() {
        return event;
    }

    public void stop() {
        event.eventEnd();
        event.stop();
    }

    public void expendDrop() {
        event.expendDrop();
    }

    public void teleportToPickupEvent(L1PcInstance pc) {
        int ran = RandomUtils.nextInt(4);

        L1Location[] locations = new L1Location[]{
                new L1Location(32780, 32816, TimePickupEvent.MAP_ID),
                new L1Location(32761, 32829, TimePickupEvent.MAP_ID),
                new L1Location(32798, 32798, TimePickupEvent.MAP_ID),
                new L1Location(32791, 32837, TimePickupEvent.MAP_ID),
        };

        L1Location loc = locations[ran];
        L1Teleport.teleport(pc, loc.getX(), loc.getY(), (short) loc.getMapId(), pc.getHeading(), true);
    }

    public void clearDrop() {
        event.setDropItemState(0);
        event.clearDropItems();
    }
}
