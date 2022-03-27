package ks.core.datatables.exp;

import ks.app.LineageAppContext;
import ks.app.aop.LogTime;
import ks.app.config.prop.CodeConfig;
import ks.util.common.SqlUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ExpTable {
    public static final int MAX_LEVEL = 99;
    public static final int MAX_EXP = 0x6ecf16da;
    private final List<Exp> list = new ArrayList<>();
    private final Map<Integer, Exp> map = new HashMap<>();

    public static ExpTable getInstance() {
        return LineageAppContext.getBean(ExpTable.class);
    }

    @LogTime
    public void load() {
        list.clear();
        list.addAll(selectList());

        map.clear();

        initDataMap();
    }

    private void initDataMap() {
        for (Exp v : list) {
            map.put(v.getLvl(), v);
        }
    }

    public List<Exp> selectList() {
        return SqlUtils.query("select * from exp_setting", new BeanPropertyRowMapper<>(Exp.class));
    }

    public Exp findByLvl(int level) {
        return map.get(level);
    }

    public int getExpByLevel(int level) {
        return findByLvl(level - 1).getExp();
    }

    public int getStartExp() {
        return getExpByLevel(CodeConfig.START_LEVEL);
    }

    public int getNeedExpNextLevel(int level) {
        return getExpByLevel(level + 1) - getExpByLevel(level);
    }

    public int getLevelByExp(int exp) {
        for (Exp e : list) {
            if (exp < e.getExp()) {
                return Math.min(e.getLvl(), MAX_LEVEL);
            }
        }

        return 1;
    }

    public int getExpPercentage(int level, int exp) {
        return (int) (100.0 * ((double) (exp - getExpByLevel(level)) / (double) getNeedExpNextLevel(level)));
    }

    public double getPenaltyRate(int level) {
        if (level < 50) {
            return 1.0;
        }

        return 1.0 / findByLvl(level).getPenalty();
    }
}
