package basic.test.simulator;

import ks.core.datatables.npc.NpcTable;
import ks.core.datatables.pc.CharacterTable;
import ks.model.instance.L1NpcInstance;
import ks.model.pc.L1PcInstance;
import ks.packets.clientpackets.C_SelectCharacter;
import basic.test.BaseTest;

public class TestArrowPass extends BaseTest {
    public static void main(String[] args) throws Exception {
        L1PcInstance attacker = new CharacterTable().loadCharacter("요정테스트");
        C_SelectCharacter.init(attacker);

        L1NpcInstance target1 = NpcTable.getInstance().newNpcInstance(460000240);//해골궁수

        attacker.setLocation(32730, 32879, 108);
        target1.setLocation(32727, 32873, 108);

        //L1CharPosUtils.isBothAttackPosition(target1, attacker);

        System.exit(0);
    }
}
