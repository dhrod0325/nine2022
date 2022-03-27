package ks.model.item.characterTrade;

import ks.app.LineageAppContext;
import ks.core.datatables.pc.CharacterTable;
import ks.model.pc.L1PcInstance;
import ks.util.common.SqlUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Component;

@Component
public class CharacterTradeDao {
    private final Logger logger = LogManager.getLogger();

    public static CharacterTradeDao getInstance() {
        return LineageAppContext.getBean(CharacterTradeDao.class);
    }

    public void updateItemObjectId(int itemObjectId, int newItemObjectId) {
        try {
            String sql = "update characters_trade_info set itemObjectId=? where itemObjectId=?";
            SqlUtils.update(sql, itemObjectId, newItemObjectId);
        } catch (Exception e) {
            logger.error("오류", e);
        }
    }

    public void save(int charId, int itemObjectId, String name) {
        try {
            String sql = "insert into characters_trade select * from characters where objid=? ";
            SqlUtils.update(sql, charId);

            sql = "delete from characters where objid=? ";
            SqlUtils.update(sql, charId);

            sql = "insert into characters_trade_info (itemObjectId,targetCharId,targetName) values (?,?,?)";
            SqlUtils.update(sql, itemObjectId, charId, name);
        } catch (Exception e) {
            logger.error("오류", e);
        }
    }

    public boolean isExistCharTrade(String name) {
        try {
            String sql = "SELECT count(*) cnt FROM characters_trade where char_name=?";
            Integer count = SqlUtils.selectInteger(sql, name);

            if (count != -1)
                return count > 0;
        } catch (Exception e) {
            return false;
        }

        return false;
    }

    public CharacterTradeInfo getInfo(int itemObjectId) {
        try {
            String sql = "SELECT * FROM characters_trade_info where itemObjectId=?";

            CharacterTradeInfo characterTradeInfo = SqlUtils.select(sql, new BeanPropertyRowMapper<>(CharacterTradeInfo.class), itemObjectId);

            if (characterTradeInfo != null) {
                L1PcInstance targetPc = CharacterTable.getInstance().loadCharacterTrade(characterTradeInfo.getTargetName());
                characterTradeInfo.setTargetPc(targetPc);
            }

            return characterTradeInfo;
        } catch (EmptyResultDataAccessException ignored) {

        } catch (Exception e) {
            logger.error("오류", e);
        }

        return null;
    }

    public void delete(int itemObjectId, int charId) {
        String sql = "delete from characters_trade_info where itemObjectId=? ";
        SqlUtils.update(sql, itemObjectId);

        sql = "delete from characters_trade where objid=? ";
        SqlUtils.update(sql, charId);
    }
}
