package ks.system.boss.table;

import ks.app.LineageAppContext;
import ks.app.aop.LogTime;
import ks.system.boss.model.L1Boss;
import ks.util.common.SqlUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class L1BossSpawnListHotTable {
    private final List<L1Boss> list = new CopyOnWriteArrayList<>();

    public static L1BossSpawnListHotTable getInstance() {
        return LineageAppContext.getBean(L1BossSpawnListHotTable.class);
    }

    public boolean isBoss(int npcId) {
        return findByNpcId(npcId) != null;
    }

    public L1Boss findById(int id) {
        for (L1Boss b : list) {
            if (b.getId() == id) {
                return b;
            }
        }

        return null;
    }

    public L1Boss findByNpcId(int npcId) {
        for (L1Boss b : list) {
            if (b.getNpcId() == npcId) {
                return b;
            }
        }

        return null;
    }

    @LogTime
    public void load() {
        list.clear();

        List<L1Boss> spawnList = selectList();

        for (L1Boss bs : spawnList) {
            bs.buildTimeList();
        }

        list.addAll(spawnList);
    }

    public List<L1Boss> selectList() {
        return SqlUtils.query("SELECT * FROM spawnlist_boss_hot WHERE isyn=1", new BeanPropertyRowMapper<>(L1Boss.class));
    }

    public List<L1Boss> getList() {
        return list;
    }
}
