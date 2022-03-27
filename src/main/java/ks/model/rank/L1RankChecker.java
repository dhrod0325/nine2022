package ks.model.rank;

import ks.model.pc.L1PcInstance;
import ks.util.common.SqlUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import java.util.*;

public class L1RankChecker {
    private static final L1RankChecker instance = new L1RankChecker();
    private final Map<Integer, List<L1Rank>> rankers = new LinkedHashMap<>();
    private final List<L1Rank> allRankers = new ArrayList<>();

    public static L1RankChecker getInstance() {
        return instance;
    }

    public void load() {
        rankers.clear();
        allRankers.clear();

        String sb = "   SELECT   " +
                "   	*   " +
                "   FROM   " +
                "   	( SELECT dense_rank() over ( ORDER BY exp DESC ) ranking,char_name charName,type FROM characters WHERE AccessLevel = 0 ) T    " +
                "   	LIMIT 0,100   ";

        allRankers.addAll(SqlUtils.query(sb, new BeanPropertyRowMapper<>(L1Rank.class)));

        for (L1Rank ranker : allRankers) {
            List<L1Rank> list = rankers.computeIfAbsent(ranker.getType(), k -> new ArrayList<>());
            list.add(ranker);
        }
    }

    public Integer getClassRank(L1PcInstance pc) {
        List<L1Rank> typeList = getClassRankList(pc.getType());

        for (L1Rank rank : typeList) {
            if (rank.getCharName().equalsIgnoreCase(pc.getName())) {
                return rank.getClassRank();
            }
        }

        return 0;
    }

    public List<L1Rank> getClassRankList(int type) {
        List<L1Rank> list = rankers.get(type);

        if (list == null)
            return Collections.emptyList();

        int i = 1;

        for (L1Rank rank : list) {
            rank.setClassRank(i);
            i++;
        }

        sort(list);

        return list;
    }

    public List<L1Rank> getAllRankerList() {
        List<L1Rank> result = new ArrayList<>();

        for (List<L1Rank> o : rankers.values()) {
            result.addAll(o);
        }

        sort(result);

        return result;
    }

    private void sort(List<L1Rank> list) {
        list.sort(Comparator.comparingInt(L1Rank::getRanking));
    }
}
