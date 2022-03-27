package ks.core.datatables.enchant;

import ks.app.LineageAppContext;
import ks.model.instance.L1ItemInstance;
import ks.model.pc.L1PcInstance;
import ks.util.common.SqlUtils;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class CharacterEnchantTable {
    public static CharacterEnchantTable getInstance() {
        return LineageAppContext.getBean(CharacterEnchantTable.class);
    }

    public void insert(CharacterEnchant vo) {
        String sql = "insert into character_enchant " +
                "(charId, charName,itemObjId, itemId, itemName, bless, attrLevel, enchant, nextEnchant, enchantType, success, regDate) " +
                "   values " +
                "(:charId, :charName,:itemObjId, :itemId, :itemName, :bless, :attrLevel, :enchant, :nextEnchant, :enchantType, :success, :regDate)";

        SqlUtils.update(sql, new BeanPropertySqlParameterSource(vo));
    }

    public void insert(L1PcInstance pc, L1ItemInstance item, int oldEnchantLvl, int newEnchantLvl, boolean success) {
        insert(pc, item, oldEnchantLvl, newEnchantLvl, success, "");
    }

    public void insert(L1PcInstance pc, L1ItemInstance item, int oldEnchantLvl, int newEnchantLvl, boolean success, String enchantType) {
        CharacterEnchant vo = new CharacterEnchant();
        vo.setAttrLevel(item.getAttrEnchantLevel());
        vo.setBless(item.getBless());
        vo.setAttrLevel(item.getAttrEnchantLevel());
        vo.setCharId(pc.getId());
        vo.setCharName(pc.getName());
        vo.setItemObjId(item.getId());
        vo.setItemId(item.getItemId());
        vo.setItemName(item.getName());
        vo.setEnchant(oldEnchantLvl);
        vo.setNextEnchant(newEnchantLvl);
        vo.setSuccess(success);
        vo.setRegDate(new Date());
        vo.setEnchantType(enchantType);

        insert(vo);
    }
}
