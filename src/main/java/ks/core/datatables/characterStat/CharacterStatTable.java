package ks.core.datatables.characterStat;

import ks.app.LineageAppContext;
import ks.app.aop.LogTime;
import ks.util.common.SqlUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class CharacterStatTable {
    private final List<CharacterStat> list = new ArrayList<>();
    private final Map<String, Map<Integer, Integer>> map = new HashMap<>();

    public static CharacterStatTable getInstance() {
        return LineageAppContext.getBean(CharacterStatTable.class);
    }

    @LogTime
    public void load() {
        list.clear();
        list.addAll(selectList());

        map.clear();
        initDataMap();
    }

    private void initDataMap() {
        for (CharacterStat stat : list) {
            Map<Integer, Integer> dataMap = map.get(stat.getType());

            if (dataMap == null) {
                dataMap = new HashMap<>();
            }

            dataMap.put(stat.getStat(), stat.getBonus());

            map.put(stat.getType(), dataMap);
        }
    }

    public List<CharacterStat> selectList() {
        return SqlUtils.query("select * from stat_bonus", new BeanPropertyRowMapper<>(CharacterStat.class));
    }

    public int findBonus(String type, int stat) {
        Map<Integer, Integer> data = map.get(type);

        if (data == null) {
            return 0;
        }

        for (int i = stat; i > 0; i--) {
            Integer find = data.get(i);

            if (find != null) {
                return find;
            }
        }

        return 0;
    }
}
