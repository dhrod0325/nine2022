package ks.core.datatables.balance;

import ks.app.LineageAppContext;
import ks.app.aop.LogTime;
import ks.util.common.SqlUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class MapBalanceTable {
    private final Map<Integer, MapBalance> data = new HashMap<>();

    public static MapBalanceTable getInstance() {
        return LineageAppContext.getBean(MapBalanceTable.class);
    }

    @LogTime
    public void load() {
        data.clear();

        List<MapBalance> list = selectList();

        for (MapBalance v : list) {
            data.put(v.getMapId(), v);
        }
    }

    private List<MapBalance> selectList() {
        return SqlUtils.query("select * from map_balance", new BeanPropertyRowMapper<>(MapBalance.class));
    }

    public MapBalance getData(int mapId) {
        return data.get(mapId);
    }

    public Collection<MapBalance> getList() {
        return data.values();
    }
}
