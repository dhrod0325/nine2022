package ks.core.datatables.pet;

import ks.app.LineageAppContext;
import ks.core.datatables.pet.model.PetInventoryItem;
import ks.model.instance.L1ItemInstance;
import ks.util.common.SqlUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PetInventoryTable {
    public static PetInventoryTable getInstance() {
        return LineageAppContext.getBean(PetInventoryTable.class);
    }

    public List<PetInventoryItem> selectListByObjId(int objId) {
        return SqlUtils.query("select * from pets_items where objId=?", new BeanPropertyRowMapper<>(PetInventoryItem.class), objId);
    }

    public void insert(L1ItemInstance item) {

        PetInventoryItem vo = new PetInventoryItem();

    }

    public void insert(PetInventoryItem vo) {
        SqlUtils.update("insert into pets_items (objId, itemObjId, itemId, itemName, count, bless, enchant, attrEnchant) values (" +
                ":objId, :itemObjId, :itemId, :itemName, :count, :bless, :enchant, :attrEnchant) ON DUPLICATE KEY UPDATE count=:count", new BeanPropertySqlParameterSource(vo));
    }

    public void delete(PetInventoryItem vo) {
        SqlUtils.update("delete from pets_items where objId=? and itemObjId=?", vo.getObjId(), vo.getItemObjId());
    }

    public void update(PetInventoryItem vo) {
        SqlUtils.update("update pets_items set count=:count where itemObjId=:itemObjId", new BeanPropertySqlParameterSource(vo));
    }
}
