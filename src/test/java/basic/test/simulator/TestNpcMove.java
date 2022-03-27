package basic.test.simulator;

import ks.core.datatables.npc.NpcTable;
import ks.core.datatables.pc.CharacterTable;
import ks.model.instance.L1MonsterInstance;
import ks.model.pc.L1PcInstance;
import ks.packets.clientpackets.C_SelectCharacter;
import basic.test.BaseTest;
import ks.util.L1CharPosUtils;
import ks.util.common.random.RandomUtils;

public class TestNpcMove extends BaseTest {
    public static void main(String[] args) throws Exception {

        L1MonsterInstance attacker = (L1MonsterInstance) NpcTable.getInstance().newNpcInstance(5000111);
        attacker.setLocation(32724, 32754, 813);

        L1PcInstance target = new CharacterTable().loadCharacter("기사2");
        C_SelectCharacter.init(target);
        target.setLocation(32724, 32752, 813);


        /*
        L1NpcInstance attacker = NpcTable.getInstance().newNpcInstance(5000111);
        attacker.setLocation(32725, 32744, 813);
        L1PcInstance target = new MySqlCharacterStorage().loadCharacter("기사2");
        C_SelectCharacter.init(target);
        target.setLocation(32728, 32736, 813);
 */

        for (int i = 0; i < 1000; i++) {
            if (L1CharPosUtils.isAttackPosition(attacker, target.getX(), target.getY(), 1)) {
                System.out.println("공격이 가능한 위치에 도착. 종료함 서치 횟수 : " + i);
                break;
            }

            int dir = L1CharPosUtils.calcMoveDirection(attacker, target.getX(), target.getY());

            if (dir == -1) {
                L1CharPosUtils.setDirectionMove(attacker, RandomUtils.nextInt(0, 7));
            } else {
                L1CharPosUtils.setDirectionMove(attacker, dir);
            }
        }

        //System.out.println(attacker.getMap().isPassable(32767, 32842)+",");


        /*
        int dir = L1CharPosUtils.checkObject(attacker.getX(), attacker.getY(), attacker.getMapId(), randomMoveDirection);

        if (dir != -1) {
            L1CharPosUtils.toMoveDirection(attacker, dir);
        }

        attacker.searchTarget();
*/

        System.exit(0);
    }
}
