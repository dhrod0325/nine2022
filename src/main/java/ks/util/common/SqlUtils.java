package ks.util.common;

import ks.app.LineageAppContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.KeyHolder;

import java.util.List;
import java.util.Map;

public class SqlUtils {
    private static final Logger logger = LogManager.getLogger();

    private static JdbcTemplate jdbcTemplate() {
        return LineageAppContext.getBean(JdbcTemplate.class);
    }

    private static NamedParameterJdbcTemplate namedParameterJdbcTemplate() {
        return LineageAppContext.getBean(NamedParameterJdbcTemplate.class);
    }

    public static Map<String, Object> queryForMap(String sql, Object... params) {
        try {
            return jdbcTemplate().queryForMap(sql, params);
        } catch (EmptyResultDataAccessException ignored) {
        } catch (Exception e) {
            logger.error("오류", e);
        }

        return null;
    }

    public static <T> List<T> queryForList(String sql, Class<T> clazz, Object... params) {
        return jdbcTemplate().queryForList(sql, clazz, params);
    }

    public static List<Map<String, Object>> queryForList(String sql, Object... params) {
        return jdbcTemplate().queryForList(sql, params);
    }

    public static List<Map<String, Object>> queryForListParamMap(String sql, Map<String, Object> params) {
        return namedParameterJdbcTemplate().queryForList(sql, params);
    }

    public static <T> List<T> query(String sql, RowMapper<T> mapper, Object... params) {
        return jdbcTemplate().query(sql, mapper, params);
    }

    public static <T> List<T> query(String sql, RowMapper<T> mapper, SqlParameterSource source) {
        return jdbcTemplate().query(sql, mapper, source);
    }

    public static <T> T select(String sql, Class<T> clazz, Object... params) {
        try {
            return jdbcTemplate().queryForObject(sql, clazz, params);
        } catch (EmptyResultDataAccessException ignored) {
        } catch (Exception e) {
            logger.error("오류", e);
        }

        return null;
    }

    public static Integer selectInteger(String sql, Object... params) {
        Integer result = select(sql, Integer.class, params);
        return result == null ? 0 : result;
    }

    public static <T> T select(String sql, RowMapper<T> mapper, Object... params) {
        try {
            return jdbcTemplate().queryForObject(sql, mapper, params);
        } catch (EmptyResultDataAccessException ignored) {
        } catch (Exception e) {
            logger.error("오류", e);
        }

        return null;
    }

    public static int update(String sql, KeyHolder keyHolder, SqlParameterSource source) {
        return namedParameterJdbcTemplate().update(sql, source, keyHolder);
    }

    public static int update(String sql, Object... params) {
        return jdbcTemplate().update(sql, params);

    }

    public static void update(String sql, SqlParameterSource source) {
        namedParameterJdbcTemplate().update(sql, source);
    }

    public static <T> List<T> query(String sql, BeanPropertySqlParameterSource parameterSource, BeanPropertyRowMapper<T> rowMapper) {
        return namedParameterJdbcTemplate().query(sql, parameterSource, rowMapper);
    }
}
