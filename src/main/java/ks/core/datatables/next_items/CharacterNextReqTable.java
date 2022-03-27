package ks.core.datatables.next_items;

import ks.app.LineageAppContext;
import ks.app.config.prop.CodeConfig;
import ks.core.datatables.item.ItemTable;
import ks.model.instance.L1ItemInstance;
import ks.util.common.SqlUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CharacterNextReqTable {
    public static CharacterNextReqTable getInstance() {
        return LineageAppContext.getBean(CharacterNextReqTable.class);
    }

    public void insert(CharacterNextReq vo) {
        String sql = "INSERT INTO character_next_req (password,itemId,itemName,itemCount,regDate,serverState,itemEnchant, itemBless,itemAttr,itemObjId)" +
                " values " +
                "(:password,:itemId,:itemName,:itemCount,:regDate,:serverState,:itemEnchant,:itemBless,:itemAttr,:itemObjId)";
        SqlUtils.update(sql, new BeanPropertySqlParameterSource(vo));
    }

    public void updateCount(int id, int count) {
        String sql = "update character_next_req set itemCount=? where id=?";
        SqlUtils.update(sql, count, id);
    }

    public List<L1ItemInstance> getNextReqItemsByPassword(String password) {
        List<L1ItemInstance> result = new ArrayList<>();

        String sql = "SELECT * FROM character_next_req where password=? and serverState=?";
        List<CharacterNextReq> list = SqlUtils.query(sql, new BeanPropertyRowMapper<>(CharacterNextReq.class), password, CodeConfig.NEXT_REQ_SERVER_STATE + 1);

        for (CharacterNextReq v : list) {
            if (password.equals(v.getPassword())) {
                L1ItemInstance item = ItemTable.getInstance().createItem(v.getItemId());
                item.setId(v.getId());
                item.setIdentified(true);
                item.setEnchantLevel(v.getItemEnchant());
                item.setAttrEnchantLevel(v.getItemAttr());
                item.setBless(v.getItemBless());
                item.setCount(v.getItemCount());

                result.add(item);
            }
        }

        return result;
    }

    public void delete(int id) {
        String sql = "delete from character_next_req where id=?";
        SqlUtils.update(sql, id);
    }

    public void deleteByItemObjId(int itemObjId) {
        String sql = "delete from character_next_req where itemObjId=?";
        SqlUtils.update(sql, itemObjId);
    }

    public boolean isExists(int itemId) {
        String sql = "SELECT count(*) FROM character_next_req where itemObjId=?";

        return SqlUtils.selectInteger(sql, itemId) > 0;
    }

    public CharacterNextReturn getReturnItem(int itemId, int enchantLevel, int bless) {
        String sql = "SELECT * FROM character_next_return where itemId=? and enchantLevel <= ? and bless=?";

        return SqlUtils.select(sql, new BeanPropertyRowMapper<>(CharacterNextReturn.class), itemId, enchantLevel, bless);
    }
}
