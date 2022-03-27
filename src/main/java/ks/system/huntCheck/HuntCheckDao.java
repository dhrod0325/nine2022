package ks.system.huntCheck;

import ks.app.LineageAppContext;
import ks.system.huntCheck.vo.HuntCheck;
import ks.system.huntCheck.vo.HuntCheckItem;
import ks.util.common.SqlUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class HuntCheckDao {
    private final Logger logger = LogManager.getLogger();

    public static HuntCheckDao getInstance() {
        return LineageAppContext.getBean(HuntCheckDao.class);
    }

    public List<HuntCheck> selectHuntCheckList(HuntCheck vo) {
        String sql = "SELECT * FROM HUNTCHECK WHERE 1=1 ";

        if (vo.getId() != 0) {
            sql += "  AND id = :id  ";
        }

        if (!StringUtils.isEmpty(vo.getSearchStartDate())) {
            sql += "  and date_format(regDate,'%Y-%m-%d %H:%i') >= :searchStartDate  ";
        }

        return SqlUtils.query(sql, new BeanPropertySqlParameterSource(vo), new BeanPropertyRowMapper<>(HuntCheck.class));
    }

    public HuntCheck selectHuntCheck(int id) {
        HuntCheck c = new HuntCheck();
        c.setId(id);

        List<HuntCheck> result = selectHuntCheckList(c);

        if (!result.isEmpty()) {
            return result.get(0);
        }

        return null;
    }

    public int insertHunt(HuntCheck vo) {
        try {
            KeyHolder keyHolder = new GeneratedKeyHolder();

            StringBuilder sb = new StringBuilder();
            sb.append("   insert into huntcheck    ");
            sb.append("   	(weaponId,   ");
            sb.append("   	weaponEnchant,   ");
            sb.append("   	ac,   ");
            sb.append("   	mr,   ");
            sb.append("   	mobId,   ");
            sb.append("   	charName,   ");
            sb.append("   	charId,   ");
            sb.append("   	regDate,   ");
            sb.append("   	exp,   ");
            sb.append("   	mapId,   ");
            sb.append("   	locX,   ");
            sb.append("   	locY   ");
            sb.append("   ) values (   ");
            sb.append("   	:weaponId,   ");
            sb.append("   	:weaponEnchant,   ");
            sb.append("   	:ac,   ");
            sb.append("   	:mr,   ");
            sb.append("   	:mobId,   ");
            sb.append("   	:charName,   ");
            sb.append("   	:charId,   ");
            sb.append("   	:regDate,   ");
            sb.append("   	:exp,   ");
            sb.append("   	:mapId,   ");
            sb.append("   	:locX,   ");
            sb.append("   	:locY   ");
            sb.append("   )   ");

            SqlUtils.update(sb.toString(), keyHolder, new BeanPropertySqlParameterSource(vo));

            if (keyHolder.getKey() == null) {
                return 0;
            }

            vo.setId(keyHolder.getKey().intValue());

            return keyHolder.getKey().intValue();
        } catch (Exception e) {
            logger.error("오류", e);
        }

        return 0;
    }

    public void insertHuntItem(HuntCheckItem vo) {
        try {
            String sql = "INSERT INTO huntcheck_item ( huntId, itemId, count )\n" +
                    "VALUES\n" +
                    "	(\n" +
                    "		:huntId,\n" +
                    "		:itemId,\n" +
                    "	  :count \n" +
                    "	)";

            SqlUtils.update(sql, new BeanPropertySqlParameterSource(vo));
        } catch (Exception e) {
            logger.error("오류", e);
        }
    }
}
