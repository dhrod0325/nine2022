package ks.system.userShop.table;

import ks.app.LineageAppContext;
import ks.constants.L1ItemId;
import ks.model.instance.L1ItemInstance;
import ks.system.userShop.L1UserShopNpcInstance;
import ks.util.common.SqlUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class L1UserShopTable {
    private final Logger logger = LogManager.getLogger();

    public static L1UserShopTable getInstance() {
        return LineageAppContext.getBean(L1UserShopTable.class);
    }

    public void deleteShopLoc(int charId) {
        SqlUtils.update("DELETE FROM userShopLoc WHERE charId=?", charId);
    }

    public int selectCurrentBuyCountByUserShop(L1UserShop vo) {
        int result = SqlUtils.selectInteger("select `count` from usershop_buy where charId=? and itemId=? and enchantLvl=? and bless=? and attrLvl=?",
                vo.getCharId(),
                vo.getItemId(),
                vo.getEnchantLvl(),
                vo.getBless(),
                vo.getAttrLvl()
        );

        if (result == -1) {
            return 0;
        }

        return result;
    }

    public int selectCurrentBuyCountByItem(int charId, L1ItemInstance item) {
        L1UserShop vo = new L1UserShop();
        vo.setCharId(charId);
        vo.setItemId(item.getItemId());
        vo.setEnchantLvl(item.getEnchantLevel());
        vo.setBless(item.getBless());
        vo.setAttrLvl(item.getAttrEnchantLevel());

        return selectCurrentBuyCountByUserShop(vo);
    }

    public void saveShopLoc(int charId, int locX, int locY, int locMap) {
        SqlUtils.update("INSERT INTO usershoploc (charId, locX, locY, locMap) VALUES (?,?,?,?) ON DUPLICATE KEY UPDATE charId=?,locX=?,locY=?,locMap=?",
                charId,
                locX,
                locY,
                locMap,
                charId,
                locX,
                locY,
                locMap
        );
    }

    public void clearShopItems(int charId, String type) {
        SqlUtils.update("delete from usershop where type=? and charId=?", type, charId);
    }

    public void deleteShopItem(L1UserShop vo) {
        try {
            String sql = "delete from usershop where " +
                    "charId=:charId " +
                    "and type=:type " +
                    "and itemId=:itemId " +
                    "and enchantLvl=:enchantLvl " +
                    "and durability=:durability " +
                    "and bless=:bless " +
                    "and attrLvl=:attrLvl  " +
                    "and itemObjectId=:itemObjectId  ";

            SqlUtils.update(sql, new BeanPropertySqlParameterSource(vo));
        } catch (Exception e) {
            logger.error("오류", e);
        }
    }

    public L1UserShop selectShopBuy(int charId, int itemId, int enchantLvl, int bless, int attrLvl) {
        String sql = "SELECT * FROM userShop_buy WHERE charId=? and itemId=? and enchantLvl=? and bless=? and attrLvl=?";
        return SqlUtils.select(sql, new BeanPropertyRowMapper<>(L1UserShop.class), charId, itemId, enchantLvl, bless, attrLvl);
    }

    public int selectShopCount(int charId, String type, int itemId, int enchantLvl, int bless, int attrLvl) {
        String sql = "SELECT count(*) FROM userShop WHERE charId=? and type=? and itemId=? and enchantLvl=? and bless=? and attrLvl=?";

        return SqlUtils.selectInteger(sql, charId, type, itemId, enchantLvl, bless, attrLvl);
    }

    public List<L1UserShop> selectUserShopList(int charId, String type) {
        String sql = "SELECT * FROM userShop WHERE charId=? and type=?";

        L1UserShop param = new L1UserShop();
        param.setCharId(charId);

        return SqlUtils.query(sql, (rs, i) -> {
            L1UserShop bean = new L1UserShop();
            bean.setCharId(rs.getInt("charId"));
            bean.setType(rs.getString("type"));
            bean.setItemId(rs.getInt("itemId"));
            bean.setEnchantLvl(rs.getInt("enchantLvl"));
            bean.setDurability(rs.getInt("durability"));
            bean.setBless(rs.getInt("bless"));
            bean.setAttrLvl(rs.getInt("attrLvl"));
            bean.setTotalCount(rs.getInt("totalCount"));
            bean.setPrice(rs.getInt("price"));
            bean.setCount(rs.getInt("count"));
            bean.setItemObjectId(rs.getInt("itemObjectId"));
            bean.setItemName(rs.getString("itemName"));

            return bean;
        }, charId, type);
    }

    public List<L1UserShop> selectUserShopBuyList(int charId) {
        String sql = "SELECT * FROM userShop_buy WHERE charId=?";

        L1UserShop param = new L1UserShop();
        param.setCharId(charId);

        return SqlUtils.query(sql, (rs, i) -> {
            L1UserShop bean = new L1UserShop();

            bean.setCharId(rs.getInt("charId"));
            bean.setItemObjectId(rs.getInt("itemObjectId"));
            bean.setItemId(rs.getInt("itemId"));
            bean.setEnchantLvl(rs.getInt("enchantLvl"));
            bean.setDurability(rs.getInt("durability"));
            bean.setBless(rs.getInt("bless"));
            bean.setAttrLvl(rs.getInt("attrLvl"));
            bean.setPrice(rs.getInt("price"));
            bean.setCount(rs.getInt("count"));

            return bean;
        }, charId);
    }

    public List<Map<String, Object>> selectUserShopLocList() {
        String sql = "SELECT *,(select char_name from characters WHERE objId=charId) charName FROM usershoploc";
        return SqlUtils.queryForList(sql);
    }

    public void saveShopItem(L1UserShop vo) {
        try {
            String sql = "insert into userShop " +
                    "(`charId`,`totalCount`, `price`, `count`, `type`,itemId,itemName,enchantLvl,durability,bless,attrLvl,itemObjectId) " +
                    "VALUES (" +
                    ":charId, " +
                    ":totalCount, " +
                    ":price, " +
                    ":count, " +
                    ":type," +
                    ":itemId, " +
                    ":itemName, " +
                    ":enchantLvl, " +
                    ":durability, " +
                    ":bless, " +
                    ":attrLvl," +
                    ":itemObjectId) " +
                    "ON DUPLICATE KEY UPDATE " +
                    "charId=:charId," +
                    "totalCount=:totalCount," +
                    "price=:price," +
                    "count=:count, " +
                    "itemId=:itemId, " +
                    "itemName=:itemName, " +
                    "enchantLvl=:enchantLvl, " +
                    "durability=:durability, " +
                    "bless=:bless," +
                    "attrLvl=:attrLvl," +
                    "itemObjectId=:itemObjectId";

            SqlUtils.update(sql, new BeanPropertySqlParameterSource(vo));
        } catch (Exception e) {
            logger.error("오류", e);
        }
    }

    public void updateShopLoc(L1UserShopNpcInstance shop) {
        L1ItemInstance adena = shop.getInventory().findItemId(L1ItemId.ADENA);
        if (adena != null) {
            updateShopLoc(adena.getCount(), shop.getMasterObjId());
        }
    }

    public void updateShopLoc(int adena, int charId) {
        String sql = "UPDATE usershoploc SET adena=? WHERE charId=?";
        SqlUtils.update(sql, adena, charId);
    }

    public void updateShopChat(int charId, String chat) {
        String sql = "UPDATE usershoploc SET chat=? WHERE charId=?";
        SqlUtils.update(sql, charId, chat);
    }

    public void saveShopBuyItem(L1UserShop vo) {
        String sql = "insert into userShop_buy " +
                "(`charId`, `price`, `count`,itemId,enchantLvl,durability,bless,attrLvl,itemObjectId) " +
                "VALUES (" +
                ":charId, " +
                ":price, " +
                ":count, " +
                ":itemId, " +
                ":enchantLvl, " +
                ":durability, " +
                ":bless, " +
                ":attrLvl," +
                ":itemObjectId) " +
                "ON DUPLICATE KEY UPDATE " +
                "charId=:charId," +
                "price=:price," +
                "count=:count, " +
                "itemId=:itemId, " +
                "enchantLvl=:enchantLvl, " +
                "durability=:durability, " +
                "bless=:bless," +
                "attrLvl=:attrLvl," +
                "itemObjectId=:itemObjectId";

        SqlUtils.update(sql, new BeanPropertySqlParameterSource(vo));
    }

    public void deleteShopBuyItem(int charId) {
        String sql = "DELETE FROM userShop_buy WHERE charId=?";
        SqlUtils.update(sql, charId);
    }

    public void deleteShopBuy(int charId) {
        String sql = "DELETE FROM userShop WHERE charId=? and type='buy'";
        SqlUtils.update(sql, charId);
    }
}
