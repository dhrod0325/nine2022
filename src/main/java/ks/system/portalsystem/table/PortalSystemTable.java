package ks.system.portalsystem.table;

import ks.app.LineageAppContext;
import ks.app.aop.LogTime;
import ks.system.portalsystem.model.L1PortalData;
import ks.util.common.SqlUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class PortalSystemTable {

    private final List<L1PortalData> list = new ArrayList<>();

    public static PortalSystemTable getInstance() {
        return LineageAppContext.getBean(PortalSystemTable.class);
    }

    public List<L1PortalData> selectPortalDataList() {
        String sql = "SELECT * FROM portalsystem WHERE isUse=1";

        return SqlUtils.query(sql, new BeanPropertyRowMapper<>(L1PortalData.class));
    }

    public void update(L1PortalData vo) {
        String sql = "UPDATE portalsystem SET startTime=? WHERE id=?";
        SqlUtils.update(sql, vo.getStartTime(), vo.getId());
    }

    @LogTime
    public void load() {
        list.clear();
        list.addAll(selectPortalDataList());
    }

    public L1PortalData findById(int id) {
        for (L1PortalData data : list) {
            if (data.getId() == id) {
                return data;
            }
        }

        return null;
    }

    public L1PortalData findByNpcId(int npcId) {
        for (L1PortalData data : list) {
            if (data.getNpcId() == npcId) {
                return data;
            }
        }

        return null;
    }

    public List<L1PortalData> getList() {
        return list;
    }
}
