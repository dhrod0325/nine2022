package ks.system.lastabard;

import ks.constants.L1ActionCodes;
import ks.core.datatables.DoorSpawnTable;
import ks.model.L1MonsterDeath;
import ks.model.instance.L1DoorInstance;
import ks.model.instance.L1MonsterInstance;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LastabardDeath extends L1MonsterDeath {
    private final Logger logger = LogManager.getLogger();

    private int doorId;

    private int countMapId;

    private int locX;

    private int locY;

    public LastabardDeath(L1MonsterInstance mob, int locX, int locY, int doorId, int countMapId) {
        super(mob, null);
        setDoorId(doorId);
        setCountMapId(countMapId);
        setLocX(locX);
        setLocY(locY);
    }

    public int getDoorId() {
        return this.doorId;
    }

    public void setDoorId(int doorId) {
        this.doorId = doorId;
    }

    public int getCountMapId() {
        return this.countMapId;
    }

    public void setCountMapId(int countMapId) {
        this.countMapId = countMapId;
    }

    @Override
    public void run() {
        try {
            int doorId = getDoorId();
            int countMapId = getCountMapId();

            if (doorId > 0) {
                LastabardListener.getInstance().addDelayTime(doorId, countMapId);
                openDoor(doorId);
            }
        } catch (Exception e) {
            logger.error("오류", e);
        }

        super.run();
    }

    public void openDoor(int doorId) {
        L1DoorInstance door = DoorSpawnTable.getInstance().getDoor(doorId);

        if (door != null) {
            synchronized (this) {
                if (door.getOpenStatus() == L1ActionCodes.ACTION_Close) {
                    door.setDead(false);
                    door.open();
                }
            }
        }
    }

    public int getLocX() {
        return locX;
    }

    public void setLocX(int locX) {
        this.locX = locX;
    }

    public int getLocY() {
        return locY;
    }

    public void setLocY(int locY) {
        this.locY = locY;
    }
}
