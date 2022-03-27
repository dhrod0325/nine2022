package ks.system.infinityWar.table;


import ks.app.LineageAppContext;
import ks.app.aop.LogTime;
import ks.system.infinityWar.model.InfinityWar;
import ks.system.infinityWar.model.InfinityWarItem;
import ks.system.infinityWar.model.InfinityWarSpawn;
import ks.util.common.SqlUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class InfinityWarTable {
    public static InfinityWarTable getInstance() {
        return LineageAppContext.getBean(InfinityWarTable.class);
    }

    private final List<InfinityWar> list = new ArrayList<>();

    @LogTime
    public void load() {
        list.clear();
        list.addAll(selectList());

        for (InfinityWar war : list) {
            war.setMaxPattern(selectSpawnMaxPattern(war.getId()));
            war.getNpc().addAll(selectNpcListById(war.getId()));
            war.getTimes().addAll(selectTimeListById(war.getId()));
            war.getSpawnList().addAll(selectSpawnListById(war.getId()));
            war.getItems().addAll(selectItemsById(war.getId()));
        }
    }

    public List<InfinityWar> selectList() {
        return SqlUtils.query("select * from infinity_war", new BeanPropertyRowMapper<>(InfinityWar.class));
    }

    private List<InfinityWarSpawn> selectSpawnListById(int id) {
        return SqlUtils.query("select * from infinity_war_spawn where infinityId=? order by id", new BeanPropertyRowMapper<>(InfinityWarSpawn.class), id);
    }

    public List<Integer> selectNpcListById(int id) {
        return SqlUtils.queryForList("select npcId from infinity_war_npc where infinityId=?", Integer.class, id);
    }

    public List<String> selectTimeListById(int id) {
        return SqlUtils.queryForList("select startTime from infinity_war_time where infinityId=?", String.class, id);
    }

    private List<InfinityWarItem> selectItemsById(int id) {
        return SqlUtils.query("select * from infinity_war_item", new BeanPropertyRowMapper<>(InfinityWarItem.class), id);
    }

    public int selectSpawnMaxPattern(int id) {
        return SqlUtils.selectInteger("select max(pattern) from infinity_war_spawn where infinityId=?", id);
    }

    public List<InfinityWar> getList() {
        return list;
    }
}
