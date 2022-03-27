package ks.model.map;

import ks.app.LineageAppContext;
import ks.app.aop.LogTime;
import ks.core.reader.MapReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class L1WorldMap {
    private final Logger logger = LogManager.getLogger();


    private final Map<Integer, L1Map> maps = new HashMap<>();

    public static L1WorldMap getInstance() {
        return LineageAppContext.getBean(L1WorldMap.class);
    }

    @LogTime
    public void load() {
        MapReader in = MapReader.getDefaultReader();

        try {
            maps.putAll(in.read());

            if (maps.isEmpty()) {
                throw new RuntimeException("MAP의 read에 실패");
            }
        } catch (Exception e) {
            logger.error("오류", e);
            System.exit(0);
        }
    }

    public L1Map getMap(short mapId) {
        return maps.getOrDefault((int) mapId, new L1NullMap());
    }

    public void cloneMap(int targetId, int newId) {
        L1Map copyMap = maps.get(targetId).copyMap(newId);
        maps.put(newId, copyMap);
    }
}
