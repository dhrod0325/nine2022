package ks.model.pc.timeDungeon;

import ks.model.pc.L1PcInstance;
import ks.system.timeDungeon.L1TimeDungeonData;
import ks.system.timeDungeon.L1TimeDungeonTable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class L1TimeDungeon {
    private final Logger logger = LogManager.getLogger();

    private final L1PcInstance pc;
    private final Map<Integer, L1TimeDungeonData> timeDungeonDataMap = new HashMap<>();

    public L1TimeDungeon(L1PcInstance pc) {
        this.pc = pc;
    }

    public Map<Integer, L1TimeDungeonData> getTimeDungeonDataMap() {
        return timeDungeonDataMap;
    }

    public L1TimeDungeonData getTimeDungeonData(int mapId) {
        for (L1TimeDungeonData o : timeDungeonDataMap.values()) {
            if (o.getTimeDungeon().getMaps().contains(mapId)) {
                return o;
            }
        }

        return null;
    }

    public boolean isTimeDungeon(int mapId) {
        L1TimeDungeonData data = getTimeDungeonData(mapId);

        if (data == null) {
            return false;
        }

        return data.getMapId() == mapId;
    }

    public void loadTimeDungeon() {
        try {
            timeDungeonDataMap.clear();
            timeDungeonDataMap.putAll(L1TimeDungeonTable.getInstance().selectTimeDungeonDataMap(pc.getId(), new Date()));
        } catch (Exception e) {
            logger.error("오류", e);
        }
    }

    public void saveTimeDungeonData() {
        for (L1TimeDungeonData data : timeDungeonDataMap.values()) {
            if (!data.isTimeOver() && data.getUseSecond() > 0) {
                L1TimeDungeonTable.getInstance().saveTimeDungeonData(pc.getId(), data.getMapId(), data.getUseSecond(), new Date());
            }
        }
    }
}
