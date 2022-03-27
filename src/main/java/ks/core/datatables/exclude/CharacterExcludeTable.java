package ks.core.datatables.exclude;

import ks.app.LineageAppContext;
import ks.util.common.SqlUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CharacterExcludeTable {
    public static CharacterExcludeTable getInstance() {
        return LineageAppContext.getBean(CharacterExcludeTable.class);
    }

    public void insert(CharacterExclude vo) {
        String sql = "insert into characters_exclude " +
                "(charId, targetName) " +
                "   values " +
                "(:charId, :targetName)";

        SqlUtils.update(sql, new BeanPropertySqlParameterSource(vo));
    }

    public void delete(CharacterExclude vo) {
        String sql = "delete from characters_exclude where charId=:charId and targetName=:targetName ";
        SqlUtils.update(sql, new BeanPropertySqlParameterSource(vo));
    }

    public List<CharacterExclude> findByCharId(int charId) {
        String sql = "select * from characters_exclude where charId=?";

        return SqlUtils.query(sql, new BeanPropertyRowMapper<>(CharacterExclude.class), charId);
    }
}
