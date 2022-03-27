package ks.system.boss.table;

import ks.app.LineageAppContext;
import ks.system.boss.model.L1Boss;
import ks.util.common.SqlUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Component
public class L1BossSpawnListHotCurrentTable {
    public static L1BossSpawnListHotCurrentTable getInstance() {
        return LineageAppContext.getBean(L1BossSpawnListHotCurrentTable.class);
    }

    public void insert(L1Boss bs) {
        String sql = "insert into spawnlist_boss_hot_current (id,reg_date) VALUES (?,?) ON DUPLICATE KEY UPDATE reg_date=?";
        SqlUtils.update(sql, bs.getId(), new Date(), new Date());
    }

    public void deleteById(L1Boss bs) {
        String sql = "delete from spawnlist_boss_hot_current where id = ?";
        SqlUtils.update(sql, bs.getId());
    }

    public List<L1Boss> selectList() {
        List<L1Boss> bossList = new ArrayList<>();

        String sql = "SELECT * FROM spawnlist_boss_hot_current";
        List<Map<String, Object>> list = SqlUtils.queryForList(sql);

        for (Map<String, Object> map : list) {
            int id = (int) map.get("id");
            Date regDate = (Date) map.get("reg_date");

            L1Boss bs = L1BossSpawnListHotTable.getInstance().findById(id);
            bs.setRegDate(regDate);

            bossList.add(L1BossSpawnListHotTable.getInstance().findById(id));
        }

        return bossList;
    }
}
