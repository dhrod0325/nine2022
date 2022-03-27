package ks.system.timeDungeon;

import java.util.Date;

public class L1TimeDungeonData {
    private int charId;
    private int mapId;
    private int useSecond;
    private Date regDate;

    private L1TimeDungeon timeDungeon;

    public int getCharId() {
        return charId;
    }

    public void setCharId(int charId) {
        this.charId = charId;
    }

    public int getMapId() {
        return mapId;
    }

    public void setMapId(int mapId) {
        this.mapId = mapId;
    }

    public int getUseSecond() {
        return useSecond;
    }

    public void setUseSecond(int useSecond) {
        this.useSecond = useSecond;
    }

    public int getUseMinute() {
        return useSecond / 60;
    }

    public Date getRegDate() {
        return regDate;
    }

    public void setRegDate(Date regDate) {
        this.regDate = regDate;
    }

    public boolean isTimeOver() {
        return useSecond / 60 >= timeDungeon.getMaxMinute();
    }

    public L1TimeDungeon getTimeDungeon() {
        return timeDungeon;
    }

    public void setTimeDungeon(L1TimeDungeon timeDungeon) {
        this.timeDungeon = timeDungeon;
    }

    public int getRemainingMinute() {
        return (timeDungeon.getMaxMinute() - getUseMinute());
    }
}