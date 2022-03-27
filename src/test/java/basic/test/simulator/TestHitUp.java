package basic.test.simulator;

import ks.core.datatables.npc.NpcTable;
import ks.core.datatables.pc.CharacterTable;
import ks.model.attack.physics.L1AttackRun;
import ks.model.instance.L1NpcInstance;
import ks.model.pc.L1PcInstance;
import ks.packets.clientpackets.C_SelectCharacter;
import basic.test.BaseTest;

public class TestHitUp extends BaseTest {
    public static void main(String[] args) throws Exception {
        L1PcInstance attacker = new CharacterTable().loadCharacter("테스트");
        C_SelectCharacter.init(attacker);

        L1NpcInstance target1 = NpcTable.getInstance().newNpcInstance(5000114);//킹버그베어
        L1NpcInstance target2 = NpcTable.getInstance().newNpcInstance(460000243);//좀비

        target2.setLocation(attacker.getLocation());
        target1.setLocation(attacker.getLocation());

        for (int i = 0; i < 10; i++) {
            L1AttackRun attackRun = new L1AttackRun(attacker, target1);
            attackRun.action();
            attackRun.commit();
        }

        System.out.println();
        System.out.println();

        for (int i = 0; i < 10; i++) {
            L1AttackRun attackRun = new L1AttackRun(attacker, target2);
            attackRun.action();
            attackRun.commit();
        }

        System.exit(0);
    }
}
