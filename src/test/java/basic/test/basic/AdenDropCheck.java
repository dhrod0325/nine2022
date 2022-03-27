package basic.test.basic;

import basic.test.BaseTest;
import ks.core.datatables.npc.NpcRowMapper;
import ks.core.datatables.npc.NpcTable;
import ks.core.datatables.transform.NpcTransformTable;
import ks.model.L1Npc;
import ks.util.common.SqlUtils;

import java.util.List;

public class AdenDropCheck extends BaseTest {
    public static void main(String[] args) {
        NpcTransformTable.getInstance().load();
        NpcTable.getInstance().load();

        String sql = "select * from npc where npcid in (\n" +
                "select npc_templateid from spawnlist where mapid in (813) group by npc_templateid\n" +
                ")";

        List<L1Npc> list = SqlUtils.query(sql, new NpcRowMapper(NpcTable.getInstance().getFamilyTypes()));

        int adenMin = 1200;

        double xTime = 23;//버그베어 방수
        double yTime = 14;

        checkAdenCount(adenMin, xTime, yTime);
    }

    public static CheckAden checkAdenCount(int adenMin, double xTime, double yTime) {
        double per = (yTime - xTime) / xTime * 100;

        double t = (per / 100d) + 1;

        int min = (int) (adenMin * t);
        int max = (int) (min * 1.2);

        return new CheckAden(min, max, t);
    }

    public static class CheckAden {
        public int min;
        public int max;
        public double per;

        public CheckAden(int min, int max, double per) {
            this.min = min;
            this.max = max;
            this.per = per;
        }
    }
}
