package ks.system.dogFight;

import ks.app.LineageAppContext;
import ks.util.common.SqlUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class L1DogFightResultTable {
    private final List<L1DogFightResult> list = new ArrayList<>();

    public static L1DogFightResultTable getInstance() {
        return LineageAppContext.getBean(L1DogFightResultTable.class);
    }

    public void load() {
        list.clear();
        list.addAll(selectList());
    }

    public List<L1DogFightResult> selectList() {
        return SqlUtils.query("select * from dog_fight_result a,(SELECT\n" +
                "	winnerNpcId,\n" +
                "	count( winnerNpcId ) * 100 / SUM(\n" +
                "	count( winnerNpcId )) over () winPer \n" +
                "FROM\n" +
                "	dog_fight_result \n" +
                "GROUP BY\n" +
                "	winnerNpcId) b where a.winnerNpcId = b.winnerNpcId order by round desc limit 0,5", new BeanPropertyRowMapper<>(L1DogFightResult.class));
    }

    public void insert(L1DogFightResult vo) {
        SqlUtils.update("insert into dog_fight_result (round,winnerNpcId,allotmentPercentage,type) values (:round,:winnerNpcId,:allotmentPercentage,:type)", new BeanPropertySqlParameterSource(vo));
    }

    public List<L1DogFightResult> getList() {
        return list;
    }
}
