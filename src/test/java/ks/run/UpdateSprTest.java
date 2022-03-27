package ks.run;

import ks.base.AbstractTest;
import ks.core.datatables.SprTable;
import ks.core.datatables.npc.NpcTable;
import ks.model.L1Npc;
import ks.util.common.SqlUtils;

public class UpdateSprTest extends AbstractTest {
    public void test(Integer... npcIds) {
        /*
        List<Integer> npcIds = new ArrayList<>(Arrays.asList(
                460000283
        ));
         */

        for (int npcId : npcIds) {
            updateSpr(npcId);
        }
    }

    private void updateSpr(int npcId) {
        L1Npc n = NpcTable.getInstance().getTemplate(npcId);

        int moveSpeed = SprTable.getInstance().getMoveSpeed(n.getGfxid(), 0);
        int attackSpeed = SprTable.getInstance().getAttackSpeed(n.getGfxid(), 1);

        int spell = SprTable.getInstance().getAttackSpeed(n.getGfxid(), 18);
        int altAttack = SprTable.getInstance().getAttackSpeed(n.getGfxid(), 30);

        String sql = "UPDATE npc set passispeed=?,atkspeed=?,atk_magic_speed=?,sub_magic_speed=? where npcid=?";
        SqlUtils.update(sql, moveSpeed, attackSpeed, spell, altAttack, npcId);
    }
}
