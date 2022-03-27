package ks.core.datatables.blacklist;

import ks.util.common.SqlUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;

import java.util.List;

public class BlackListTable {
    private static final BlackListTable instance = new BlackListTable();

    public static BlackListTable getInstance() {
        return instance;
    }

    public void insert(BlackList vo) {
        SqlUtils.update("insert into character_blacklist (ip,accountName,bankNo,phone,bankName,bankOwner,reason,regDate) values (:ip,:accountName,:bankNo,:phone,:bankName,:bankOwner,:reason,:regDate)", new BeanPropertySqlParameterSource(vo));
    }

    public List<BlackList> selectList() {
        return SqlUtils.query("select * from character_blacklist", new BeanPropertyRowMapper<>(BlackList.class));
    }
}
