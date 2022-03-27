package ks.core.datatables.favPoly;

import ks.app.LineageAppContext;
import ks.model.pc.L1PcInstance;
import ks.util.common.SqlUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FavPolyTable {
    public static FavPolyTable getInstance() {
        return LineageAppContext.getBean(FavPolyTable.class);
    }

    public List<FavPoly> selectList(int charId) {
        String sql = "SELECT * from character_poly_fav where charId=? order by idx asc";
        return SqlUtils.query(sql, new BeanPropertyRowMapper<>(FavPoly.class), charId);
    }

    public void save(FavPoly vo) {
        String sql = "INSERT INTO character_poly_fav (charId,polyId,idx) values (:charId,:polyId,:idx)";
        SqlUtils.update(sql, new BeanPropertySqlParameterSource(vo));
    }

    public void saveAll(List<FavPoly> list) {
        int idx = 0;
        for (FavPoly vo : list) {
            vo.setIdx(idx);

            save(vo);

            idx++;
        }
    }

    public void clear(L1PcInstance pc) {
        String sql = "delete from character_poly_fav where charId=?";
        SqlUtils.update(sql, pc.getId());
    }
}
