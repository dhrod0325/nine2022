package ks.core.datatables;

import ks.model.L1Soldier;
import ks.util.common.SqlUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SoldierTable {
    private static final SoldierTable instance = new SoldierTable();

    private final Map<Integer, L1Soldier> soldiers = new ConcurrentHashMap<>();

    public static SoldierTable getInstance() {
        return instance;
    }

    public void load() {
        soldiers.clear();

        List<L1Soldier> list = selectList();

        for (L1Soldier soldier : list) {
            soldiers.put(soldier.getId(), soldier);
        }
    }

    public List<L1Soldier> selectList() {
        return SqlUtils.query("SELECT * FROM castle_soldier", (rs, i) -> {
            L1Soldier soldier = new L1Soldier(rs.getInt(1));
            soldier.setSoldier1(rs.getInt(2));
            soldier.setSoldier1NpcId(rs.getInt(3));
            soldier.setSoldier1Name(rs.getString(4));
            soldier.setSoldier2(rs.getInt(5));
            soldier.setSoldier2NpcId(rs.getInt(6));
            soldier.setSoldier2Name(rs.getString(7));
            soldier.setSoldier3(rs.getInt(8));
            soldier.setSoldier3NpcId(rs.getInt(9));
            soldier.setSoldier3Name(rs.getString(10));
            soldier.setSoldier4(rs.getInt(11));
            soldier.setSoldier4NpcId(rs.getInt(12));
            soldier.setSoldier4Name(rs.getString(13));

            return soldier;
        });
    }


    public L1Soldier getSoldierTable(int id) {
        return soldiers.get(id);
    }

    public void updateSoldier(L1Soldier soldier) {
        SqlUtils.update("UPDATE castle_soldier SET soldier1=?, soldier2=?, soldier3=?, soldier4=? WHERE castle_id=?",
                soldier.getSoldier1(),
                soldier.getSoldier2(),
                soldier.getSoldier3(),
                soldier.getSoldier4(),
                soldier.getId()
        );
    }
}
