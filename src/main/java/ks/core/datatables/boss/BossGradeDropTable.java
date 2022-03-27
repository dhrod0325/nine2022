package ks.core.datatables.boss;

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
public class BossGradeDropTable {
    public static BossGradeDropTable getInstance() {
        return LineageAppContext.getBean(BossGradeDropTable.class);
    }

    private final Map<Integer, List<BossGradeDrop>> data = new HashMap<>();

    private final List<BossGradeDrop> list = new ArrayList<>();

    @LogTime
    public void load() {
        list.clear();
        list.addAll(selectList());

        data.clear();
        for (BossGradeDrop o : list) {
            List<BossGradeDrop> dropList = data.computeIfAbsent(o.getBossGrade(), k -> new ArrayList<>());
            dropList.add(o);
        }
    }

    public List<BossGradeDrop> selectList() {
        return SqlUtils.query("select * from boss_grade_drop", new BeanPropertyRowMapper<>(BossGradeDrop.class));
    }

    public List<BossGradeDrop> findByGrade(int grade) {
        return data.get(grade);
    }
}
