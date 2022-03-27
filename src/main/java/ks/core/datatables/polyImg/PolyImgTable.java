package ks.core.datatables.polyImg;

import ks.app.LineageAppContext;
import ks.app.aop.LogTime;
import ks.util.common.SqlUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class PolyImgTable {
    private final List<PolyImg> list = new ArrayList<>();

    public static PolyImgTable getInstance() {
        return LineageAppContext.getBean(PolyImgTable.class);
    }

    @LogTime
    public void load() {
        list.clear();
        list.addAll(selectList());
    }

    public List<PolyImg> selectList() {
        String sql = "SELECT * from polymorps_img";
        return SqlUtils.query(sql, new BeanPropertyRowMapper<>(PolyImg.class));
    }

    public int getImg(int polyId) {
        for (PolyImg m : list) {
            if (m.getPolyId() == polyId) {
                return m.getPolyImg();
            }
        }

        return 0;
    }
}
