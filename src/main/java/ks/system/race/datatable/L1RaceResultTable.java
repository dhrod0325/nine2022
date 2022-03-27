package ks.system.race.datatable;

import ks.app.LineageAppContext;
import ks.system.race.model.L1RaceResult;
import ks.util.common.SqlUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class L1RaceResultTable {
    private final List<L1RaceResult> list = new ArrayList<>();

    public static L1RaceResultTable getInstance() {
        return LineageAppContext.getBean(L1RaceResultTable.class);
    }

    public void load() {
        list.clear();
        list.addAll(selectList());
    }

    public List<L1RaceResult> selectList() {
        return SqlUtils.query("select * from race_result a,(SELECT\n" +
                "	winnerNpcId,\n" +
                "	count( winnerNpcId ) * 100 / SUM(\n" +
                "	count( winnerNpcId )) over () winPer \n" +
                "FROM\n" +
                "	race_result \n" +
                "GROUP BY\n" +
                "	winnerNpcId) b where a.winnerNpcId = b.winnerNpcId order by round desc limit 0,5", new BeanPropertyRowMapper<>(L1RaceResult.class));
    }

    public void insert(L1RaceResult vo) {
        SqlUtils.update("insert into race_result (round,winnerNpcId,allotmentPercentage,type) values (:round,:winnerNpcId,:allotmentPercentage,:type)", new BeanPropertySqlParameterSource(vo));
    }

    public List<L1RaceResult> getList() {
        return list;
    }
}
