package ks.core.datatables;

import ks.model.L1Castle;
import ks.scheduler.WarTimeScheduler;
import ks.util.common.DateUtils;
import ks.util.common.SqlUtils;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

public class CastleTable {
    private static final CastleTable instance = new CastleTable();

    private final Map<Integer, L1Castle> castles = new HashMap<>();

    public static CastleTable getInstance() {
        return instance;
    }

    public void load() {
        castles.clear();

        List<L1Castle> list = selectList();

        for (L1Castle castle : list) {
            castles.put(castle.getId(), castle);
        }

        WarTimeScheduler.getInstance().timeCheck();
    }

    public List<L1Castle> selectList() {
        return SqlUtils.query("SELECT * FROM castle", (rs, i) -> {
            L1Castle castle = new L1Castle(rs.getInt("castle_id"), rs.getString("name"));
            castle.setWarTime(DateUtils.timestampToCalendar((Timestamp) rs.getObject("war_time")));
            castle.setTaxRate(rs.getInt("tax_rate"));
            castle.setPublicMoney(rs.getInt("public_money"));
            castle.setPublicReadyMoney(rs.getInt("public_ready_money"));
            castle.setShowMoney(rs.getInt("show_money"));
            castle.setWarBaseTime(rs.getInt("war_basetime"));
            castle.setCastleSecurity(rs.getInt("security"));

            return castle;
        });
    }

    public boolean isTodayWar() {
        String sql = "select count(*) from castle where date_format(war_time,'%Y-%m-%d') = ?";

        return SqlUtils.selectInteger(sql, new SimpleDateFormat("yyyy-MM-dd").format(new Date())) > 0;
    }

    public void updateWarTime(String name, Calendar cal) {
        if (name == null) {
            return;
        }

        if (name.length() == 0) {
            return;
        }

        for (int id : castles.keySet()) {
            L1Castle castle = castles.get(id);
            if (castle.getName().startsWith(name)) {
                castle.setWarTime((Calendar) cal.clone());
                updateCastle(castle);
            }
        }
    }

    public Collection<L1Castle> getCastleTableList() {
        return castles.values();
    }

    public L1Castle getCastleTable(int id) {
        return castles.get(id);
    }

    public void updateCastle(L1Castle castle) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String fm = sdf.format(castle.getWarTime().getTime());
        int money = Math.min(castle.getPublicMoney(), 1000000000);

        SqlUtils.update("UPDATE castle SET name=?, war_time=?, tax_rate=?, public_money=?, public_ready_money=?, show_money=?, war_basetime=?, security=? WHERE castle_id=?",
                castle.getName(),
                fm,
                castle.getTaxRate(),
                money,
                castle.getPublicReadyMoney(),
                castle.getShowMoney(),
                castle.getWarBaseTime(),
                castle.getCastleSecurity(),
                castle.getId()
        );

        castles.put(castle.getId(), castle);
    }
}
