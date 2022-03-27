package ks.core.datatables;

import ks.model.L1GetBackRestart;
import ks.util.common.SqlUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetBackRestartTable {
    private static final GetBackRestartTable instance = new GetBackRestartTable();

    private final Map<Integer, L1GetBackRestart> getbackRestart = new HashMap<>();

    public static GetBackRestartTable getInstance() {
        return instance;
    }

    public void load() {
        getbackRestart.clear();

        List<L1GetBackRestart> list = selectList();

        for (L1GetBackRestart o : list) {
            getbackRestart.put(o.getArea(), o);
        }
    }

    public List<L1GetBackRestart> selectList() {
        return SqlUtils.query("SELECT * FROM getback_restart", (rs, i) -> {
            L1GetBackRestart gbr = new L1GetBackRestart();
            int area = rs.getInt("area");
            gbr.setArea(area);
            gbr.setLocX(rs.getInt("locx"));
            gbr.setLocY(rs.getInt("locy"));
            gbr.setMapId(rs.getShort("mapid"));

            return gbr;
        });
    }

    public Collection<L1GetBackRestart> getGetBackRestartTableList() {
        return getbackRestart.values();
    }
}
