package ks.core.datatables.mapEvent;

import ks.app.LineageAppContext;
import ks.app.aop.LogTime;
import ks.model.L1Drop;
import ks.model.instance.L1NpcInstance;
import ks.util.common.SqlUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class MapEventDropTable {
    private final Map<Integer, List<MapEventDrop>> dataMap = new HashMap<>();

    public static MapEventDropTable getInstance() {
        return LineageAppContext.getBean(MapEventDropTable.class);
    }

    @LogTime
    public void load() {
        dataMap.clear();
        dataMap.putAll(selectList());
    }

    public Map<Integer, List<MapEventDrop>> selectList() {
        String sql = "SELECT * FROM map_event_drop";

        List<MapEventDrop> list = SqlUtils.query(sql, new BeanPropertyRowMapper<>(MapEventDrop.class));

        for (MapEventDrop e : list) {
            int key = e.getMapId();

            List<MapEventDrop> dataList = dataMap.get(key);

            if (dataList == null) {
                dataList = new ArrayList<>();
            }

            dataList.add(e);

            dataMap.put(key, dataList);
        }

        return dataMap;
    }

    public List<MapEventDrop> findItems(int mapId) {
        return dataMap.get(mapId);
    }

    public List<L1Drop> findItemsToDropList(L1NpcInstance npc) {
        List<L1Drop> dropList = new ArrayList<>();
        List<MapEventDrop> eventItems = findItems(npc.getMapId());

        if (eventItems != null && !eventItems.isEmpty()) {
            for (MapEventDrop d : eventItems) {
                L1Drop drop = new L1Drop();
                drop.setMobId(npc.getNpcId());
                drop.setItemId(d.getItemId());
                drop.setMin(d.getMin());
                drop.setMax(d.getMax());
                drop.setChance(d.getChance());

                dropList.add(drop);
            }
        }

        return dropList;
    }
}
