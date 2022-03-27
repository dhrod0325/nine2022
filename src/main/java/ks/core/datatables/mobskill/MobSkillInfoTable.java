package ks.core.datatables.mobskill;

import ks.app.LineageAppContext;
import ks.app.aop.LogTime;
import ks.util.common.SqlUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class MobSkillInfoTable {
    public static MobSkillInfoTable getInstance() {
        return LineageAppContext.getBean(MobSkillInfoTable.class);
    }

    private final Map<Integer, L1MobSkillInfo> mobSkillInfoMap = new HashMap<>();

    @LogTime
    public void load() {
        mobSkillInfoMap.clear();

        List<L1MobSkillInfo> list = selectList();

        for (L1MobSkillInfo info : list) {
            mobSkillInfoMap.put(info.getMobSkillId(), info);
        }
    }

    public List<L1MobSkillInfo> selectList() {
        String sql = "select * from mobskill_info";

        return SqlUtils.query(sql, new BeanPropertyRowMapper<>(L1MobSkillInfo.class));
    }

    public L1MobSkillInfo findByMobSkillId(int mobSkillId) {
        return mobSkillInfoMap.get(mobSkillId);
    }
}
