package ks.core.datatables.commonCode;

import ks.app.LineageAppContext;
import ks.app.aop.LogTime;
import ks.util.common.SqlUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class CommonCodeTable {
    private final Logger logger = LogManager.getLogger(getClass());

    private final Map<String, CommonCode> dataMap = new HashMap<>();

    public static CommonCodeTable getInstance() {
        return LineageAppContext.getBean(CommonCodeTable.class);
    }

    @LogTime
    public void load() {
        dataMap.clear();
        loadMap();
    }

    public void loadMap() {
        List<CommonCode> list = selectList();

        for (CommonCode code : list) {
            dataMap.put(code.getCode(), code);
        }
    }

    public String getString(String code, String defaultValue) {
        CommonCode t = getCode(code);

        if (t == null) {
            return defaultValue;
        }

        return t.getValue();
    }

    public Integer getInteger(String code, Integer defaultValue) {
        CommonCode t = getCode(code);

        if (t == null) {
            return defaultValue;
        }

        return Integer.parseInt(t.getValue());
    }

    public Double getDouble(String code, Double defaultValue) {
        CommonCode t = getCode(code);

        if (t == null) {
            return defaultValue;
        }

        return Double.parseDouble(t.getValue());
    }

    public Boolean getBoolean(String code, Boolean defaultValue) {
        CommonCode t = getCode(code);

        if (t == null) {
            return defaultValue;
        }

        return Boolean.parseBoolean(t.getValue());
    }

    public List<CommonCode> selectList() {
        String sql = "SELECT * FROM common_code";
        return SqlUtils.query(sql, new BeanPropertyRowMapper<>(CommonCode.class));
    }

    public void update(String key, String value) {
        String sql = "UPDATE common_code SET value=? where code=?";
        SqlUtils.update(sql, value, key);
    }

    private CommonCode getCode(String code) {
        CommonCode t = dataMap.get(code);

        if (t == null) {
            logger.warn("등록되지 않은 코드를 호출하였습니다 : " + code);

            return null;
        } else {
            logger.trace("LOAD CODE : " + ReflectionToStringBuilder.toString(t));
        }

        return t;
    }
}
