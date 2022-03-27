package ks.core.datatables.hunt;

import ks.app.LineageAppContext;
import ks.app.aop.LogTime;
import ks.util.common.SqlUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class HuntPriceTable {
    private final List<HuntPrice> list = new ArrayList<>();

    public static HuntPriceTable getInstance() {
        return LineageAppContext.getBean(HuntPriceTable.class);
    }

    @LogTime
    public void load() {
        list.clear();
        list.addAll(selectList());
    }

    public List<HuntPrice> selectList() {
        return SqlUtils.query("select * from hunt_price order by level desc", new BeanPropertyRowMapper<>(HuntPrice.class));
    }

    public HuntPrice findByLevel(int targetLevel) {
        for (HuntPrice o : list) {
            if (targetLevel >= o.getLevel()) {
                return o;
            }
        }

        return null;
    }
}
