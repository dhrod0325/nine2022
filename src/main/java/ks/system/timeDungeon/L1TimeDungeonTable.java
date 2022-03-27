package ks.system.timeDungeon;

import ks.app.LineageAppContext;
import ks.util.common.SqlUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class L1TimeDungeonTable {
    private final List<L1TimeDungeon> timeDungeonList = new ArrayList<>();
    private final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

    public static L1TimeDungeonTable getInstance() {
        return LineageAppContext.getBean(L1TimeDungeonTable.class);
    }

    public void load() {
        timeDungeonList.clear();
        timeDungeonList.addAll(selectTimeDungeonList());
    }

    public List<L1TimeDungeon> selectTimeDungeonList() {
        String sql = "SELECT * FROM time_dungeon";
        return SqlUtils.query(sql, new BeanPropertyRowMapper<>(L1TimeDungeon.class));
    }

    public L1TimeDungeonData selectTimeDungeonData(int charId, int mapId, Date date) {
        String sql = "SELECT * FROM time_dungeon_data where charId=? AND mapId=? AND regDate=? ";

        List<L1TimeDungeonData> data = SqlUtils.query(sql, new BeanPropertyRowMapper<>(L1TimeDungeonData.class), charId, mapId, format.format(date));

        if (data.size() == 1) {
            return data.get(0);
        }

        return null;
    }

    public L1TimeDungeonData findByMapId(int mapId, int charId, Date date) {
        Map<Integer, L1TimeDungeonData> timeData = L1TimeDungeonTable.getInstance().selectTimeDungeonDataMap(charId, date);
        return timeData.get(mapId);
    }

    public Map<Integer, L1TimeDungeonData> selectTimeDungeonDataMap(int charId, Date date) {
        Map<Integer, L1TimeDungeonData> result = new HashMap<>();

        for (L1TimeDungeon timeDungeon : timeDungeonList) {
            L1TimeDungeonData data = selectTimeDungeonData(charId, timeDungeon.getMapId(), date);

            if (data == null) {
                data = new L1TimeDungeonData();
                data.setUseSecond(0);
                data.setCharId(charId);
                data.setMapId(timeDungeon.getMapId());
                data.setRegDate(new Date());
            }

            data.setTimeDungeon(timeDungeon);

            result.put(timeDungeon.getMapId(), data);
        }

        return result;
    }

    public void saveTimeDungeonData(int charId, int mapId, int useSecond, Date date) {
        String sql = "INSERT INTO time_dungeon_data (charId,mapId,useSecond,regDate) VALUES (?,?,?,?) ON DUPLICATE KEY UPDATE useSecond=?";
        SqlUtils.update(sql, charId, mapId, useSecond, format.format(date), useSecond);
    }
}
