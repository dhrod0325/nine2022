package ks.system.userShop;

import ks.system.userShop.table.L1UserShop;
import ks.system.userShop.table.L1UserShopTable;
import ks.util.common.SqlUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import java.util.ArrayList;
import java.util.List;

public class L1UserShopCommand {
    public static List<L1UserShop> findItem(int bless, String itemName, int enchantLvl, String type) {
        List<Object> params = new ArrayList<>();

        String sql = "SELECT * FROM ( SELECT *,(select char_name from characters where objid=charid) charName FROM usershop ) T WHERE 1=1  ";

        if (!StringUtils.isEmpty(itemName)) {
            params.add(itemName.replace(" ", ""));
            sql += "    AND replace(ITEMNAME,' ','') LIKE concat('%',?,'%')     ";
        }

        sql += "   and type=?   ";
        params.add(type);

        sql += "    AND bless=?     ";
        params.add(bless);

        if (enchantLvl > 0) {
            sql += "    AND enchantLvl=?     ";
            params.add(enchantLvl);
        }

        sql += "  order by price asc  ";

        List<L1UserShop> list = SqlUtils.query(sql, new BeanPropertyRowMapper<>(L1UserShop.class), params.toArray());

        if ("buy".equalsIgnoreCase(type)) {
            for (L1UserShop userShop : new ArrayList<>(list)) {
                L1UserShop b = L1UserShopTable.getInstance().selectShopBuy(
                        userShop.getCharId(),
                        userShop.getItemId(),
                        userShop.getEnchantLvl(),
                        userShop.getBless(),
                        userShop.getAttrLvl()
                );

                if (b != null && b.getCount() >= userShop.getTotalCount()) {
                    list.remove(userShop);
                }
            }
        }

        return list;
    }
}
