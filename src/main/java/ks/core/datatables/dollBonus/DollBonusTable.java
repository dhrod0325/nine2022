package ks.core.datatables.dollBonus;

import ks.app.LineageAppContext;
import ks.app.aop.LogTime;
import ks.model.instance.L1ItemInstance;
import ks.util.common.SqlUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DollBonusTable {
    private final List<DollBonus> list = new ArrayList<>();

    public static DollBonusTable getInstance() {
        return LineageAppContext.getBean(DollBonusTable.class);
    }

    @LogTime
    public void load() {
        list.clear();
        list.addAll(selectList());
    }

    public List<DollBonus> selectList() {
        String sql = "SELECT * FROM doll_bonus";
        return SqlUtils.query(sql, new BeanPropertyRowMapper<>(DollBonus.class));
    }

    public List<DollBonus> getList() {
        return list;
    }

    public DollBonus getEnchantBonus(L1ItemInstance item) {
        return getEnchantBonus(item.getItemId(), item.getEnchantLevel());
    }

    public DollBonus getEnchantBonus(int itemId, int enchantLevel) {
        for (DollBonus o : list) {
            if (o.getEnchantLevel() == enchantLevel && o.getItemId() == itemId) {
                return o;
            }
        }

        return null;
    }
}
