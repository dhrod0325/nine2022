package ks.core.datatables.huktBook;

import ks.app.LineageAppContext;
import ks.app.aop.LogTime;
import ks.util.common.SqlUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class HuntBookTable {
    private final List<HuntBook> list = new ArrayList<>();

    public static HuntBookTable getInstance() {
        return LineageAppContext.getBean(HuntBookTable.class);
    }

    @LogTime
    public void load() {
        list.clear();
        list.addAll(selectList());

    }

    public List<HuntBook> selectList() {
        String sql = "SELECT * FROM hunt_book where `use`=1 order by ord";
        return SqlUtils.query(sql, new BeanPropertyRowMapper<>(HuntBook.class));
    }

    public List<HuntBook> getList() {
        return list;
    }

    public List<String> getNameList() {
        List<String> result = new ArrayList<>();

        for (HuntBook o : list) {
            result.add(o.getName());
        }

        return result;
    }

    public HuntBook find(int actNum) {
        if (actNum > list.size() - 1) {
            return null;
        }

        return list.get(actNum);
    }
}
