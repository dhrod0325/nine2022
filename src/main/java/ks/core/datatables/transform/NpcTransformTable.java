package ks.core.datatables.transform;

import ks.app.LineageAppContext;
import ks.app.aop.LogTime;
import ks.util.common.SqlUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class NpcTransformTable {
    private final List<NpcTransform> list = new ArrayList<>();

    public static NpcTransformTable getInstance() {
        return LineageAppContext.getBean(NpcTransformTable.class);
    }

    @LogTime
    public void load() {
        list.clear();
        list.addAll(selectList());
    }

    public List<NpcTransform> selectList() {
        String sql = "SELECT * FROM npc_transform";
        return SqlUtils.query(sql, new BeanPropertyRowMapper<>(NpcTransform.class));
    }

    public List<NpcTransform> getList() {
        return list;
    }

    public List<NpcTransform> findList(int npcId) {
        List<NpcTransform> result = new ArrayList<>();
        for (NpcTransform o : list) {
            if (o.getNpcId() == npcId) {
                result.add(o);
            }
        }

        return result;
    }


}
