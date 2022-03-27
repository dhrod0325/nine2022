package ks.core.datatables.shopInfo;

import ks.app.LineageAppContext;
import ks.util.common.SqlUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class NpcShopInfoTable {
    private final List<NpcShopInfo> list = new ArrayList<>();

    public static NpcShopInfoTable getInstance() {
        return LineageAppContext.getBean(NpcShopInfoTable.class);
    }

    public void load() {
        list.clear();
        list.addAll(selectList());
    }

    public List<NpcShopInfo> selectList() {
        String sql = "SELECT * FROM npc_shop_info";
        return SqlUtils.query(sql, new BeanPropertyRowMapper<>(NpcShopInfo.class));
    }

    public List<NpcShopInfo> getList() {
        return list;
    }

    public NpcShopInfo selectByNpcId(int npcId) {
        for (NpcShopInfo vo : list) {
            if (vo.getNpcId() == npcId) {
                return vo;
            }
        }

        return null;
    }
}
