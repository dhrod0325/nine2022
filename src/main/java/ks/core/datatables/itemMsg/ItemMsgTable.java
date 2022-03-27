package ks.core.datatables.itemMsg;

import ks.app.LineageAppContext;
import ks.app.aop.LogTime;
import ks.util.common.SqlUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ItemMsgTable {
    private final Map<Integer, ItemMsg> dataMap = new HashMap<>();

    public static ItemMsgTable getInstance() {
        return LineageAppContext.getBean(ItemMsgTable.class);
    }

    @LogTime
    public void load() {
        dataMap.clear();
        dataMap.putAll(selectList());
    }

    public Map<Integer, ItemMsg> selectList() {
        String sql = "SELECT * FROM item_msg";
        List<ItemMsg> list = SqlUtils.query(sql, new BeanPropertyRowMapper<>(ItemMsg.class));

        for (ItemMsg v : list) {
            dataMap.put(v.getItemId(), v);
        }

        return dataMap;
    }

    public ItemMsg find(int itemId) {
        return dataMap.get(itemId);
    }
}
