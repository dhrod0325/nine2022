package basic.test.basic;

import basic.test.BaseTest;
import ks.app.LineageAppContext;
import ks.core.datatables.SprTable;
import ks.core.datatables.npc.NpcTable;
import ks.model.L1Npc;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SprCheckTest extends BaseTest {
    public void update() {
        List<Integer> npcIds = new ArrayList<>(Arrays.asList(
                81083,
                81090,
                81084,
                81091,
                81092,
                81210,
                81211,
                81212,
                81096,
                81215,
                81214,
                81213,
                81100,
                81094,
                81218,
                81217,
                81216,
                81088,
                81220,
                81221,
                81219,
                81223,
                81224,
                81222,
                81227,
                81226,
                81225,
                81228,
                81099,
                81053,
                81052,
                81051,
                81050,
                81102,
                81101,
                81233,
                81232,
                81231,
                81230,
                81229,
                81237,
                81236,
                81235,
                81234,
                81104,
                81240,
                81238,
                81239
        ));

        for (int npcId : npcIds) {
            test(npcId);
        }
    }

    public void test(int npcId) {
        L1Npc n = NpcTable.getInstance().getTemplate(npcId);

        int moveSpeed = SprTable.getInstance().getMoveSpeed(n.getGfxid(), 0);
        int attackSpeed = SprTable.getInstance().getAttackSpeed(n.getGfxid(), 1);

        int spell = SprTable.getInstance().getAttackSpeed(n.getGfxid(), 18);
        int altattack = SprTable.getInstance().getAttackSpeed(n.getGfxid(), 30);

        String sql = "UPDATE npc set passispeed=?,atkspeed=?,atk_magic_speed=?,sub_magic_speed=? where npcid=?";
        LineageAppContext.getBean(JdbcTemplate.class).update(sql, moveSpeed, attackSpeed, spell, altattack, npcId);
    }
}
