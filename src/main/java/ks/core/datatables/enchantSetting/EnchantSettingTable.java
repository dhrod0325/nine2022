package ks.core.datatables.enchantSetting;

import ks.app.LineageAppContext;
import ks.util.common.SqlUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class EnchantSettingTable {
    private final List<EnchantSetting> list = new ArrayList<>();

    private final List<EnchantSettingDetail> detailList = new ArrayList<>();

    public static EnchantSettingTable getInstance() {
        return LineageAppContext.getBean(EnchantSettingTable.class);
    }

    public void load() {
        list.clear();
        list.addAll(selectList());

        detailList.clear();
        detailList.addAll(selectListDetail());
    }

    private List<EnchantSettingDetail> selectListDetail() {
        String sql = "SELECT * FROM enchant_setting_detail";
        return SqlUtils.query(sql, new BeanPropertyRowMapper<>(EnchantSettingDetail.class));
    }

    public List<EnchantSetting> selectList() {
        String sql = "SELECT * FROM enchant_setting";
        return SqlUtils.query(sql, new BeanPropertyRowMapper<>(EnchantSetting.class));
    }

    public List<EnchantSetting> getList() {
        return list;
    }

    public int getEnchantPer(int nextEnchantLevel, int safeEnchant, String scrollType) {
        for (EnchantSetting o : list) {

            if (o.getEnchantLevel() == nextEnchantLevel && o.getSafeEnchant() == safeEnchant && o.getScrollType().equalsIgnoreCase(scrollType)) {
                return o.getPer();
            }
        }

        return 0;
    }

    public int getEnchantPerDetail(int nextEnchantLevel, int itemId, String scrollType) {
        for (EnchantSettingDetail o : detailList) {

            if (o.getEnchantLevel() == nextEnchantLevel && o.getItemId() == itemId && o.getScrollType().equalsIgnoreCase(scrollType)) {
                return o.getPer();
            }
        }

        return 0;
    }
}
