package ks.core.datatables.enchantBonus;

import ks.app.LineageAppContext;
import ks.app.aop.LogTime;
import ks.model.instance.L1ItemInstance;
import ks.util.common.SqlUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class EnchantBonusTable {
    private final List<EnchantBonus> list = new ArrayList<>();

    public static EnchantBonusTable getInstance() {
        return LineageAppContext.getBean(EnchantBonusTable.class);
    }

    @LogTime
    public void load() {
        list.clear();
        list.addAll(selectList());
    }

    public List<EnchantBonus> selectList() {
        String sql = "SELECT * FROM enchant_bonus";
        return SqlUtils.query(sql, new BeanPropertyRowMapper<>(EnchantBonus.class));
    }

    public List<EnchantBonus> getList() {
        return list;
    }

    public EnchantBonus getEnchantBonus(L1ItemInstance item) {
        return getEnchantBonus(item.getItemId(), item.getEnchantLevel());
    }

    public EnchantBonus getEnchantBonus(int itemId, int enchantLevel) {
        return list.stream()
                .filter(o -> o.getEnchantLevel() == enchantLevel && o.getItemId() == itemId)
                .findFirst()
                .orElse(null);
    }
}
