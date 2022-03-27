package ks.core.datatables.spr;

import ks.app.LineageAppContext;
import ks.app.aop.LogTime;
import ks.util.common.SqlUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class SprStrictTable {
    public static SprStrictTable getInstance() {
        return LineageAppContext.getBean(SprStrictTable.class);
    }

    private final Map<Integer, SprStrict> map = new HashMap<>();

    @LogTime
    public void load() {
        map.clear();
        map.putAll(selectMap());
    }

    public Map<Integer, SprStrict> selectMap() {
        Map<Integer, SprStrict> result = new HashMap<>();
        List<SprStrict> list = SqlUtils.query("select * from spr_strict", new BeanPropertyRowMapper<>(SprStrict.class));

        for (SprStrict o : list) {
            result.put(o.getGfxId(), o);
        }

        return result;
    }

    public SprStrict findStrictByGfxId(int gfxId) {
        return map.get(gfxId);
    }

    public int findStrictValueByGfxId(int gfxId) {
        SprStrict o = findStrictByGfxId(gfxId);

        if (o == null) {
            return 0;
        } else {
            return o.getStrictValue();
        }
    }
}
