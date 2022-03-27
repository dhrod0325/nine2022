package ks.test;

import ks.base.AbstractTest;
import ks.core.datatables.npc.NpcTable;
import ks.core.datatables.pc.CharacterTable;
import ks.model.L1Character;
import ks.model.L1Location;
import ks.model.L1World;
import ks.model.instance.L1NpcInstance;
import ks.model.pc.L1PcInstance;
import ks.packets.clientpackets.C_SelectCharacter;
import ks.util.L1CharPosUtils;
import org.junit.jupiter.api.Assertions;

import java.util.HashMap;
import java.util.Map;

public class MoveTest extends AbstractTest {
    public void test() {
        try {
            Map<String, L1Location[]> dontMoveAbleLocation = new HashMap<>();

            dontMoveAbleLocation.put("공격불가능한위치", new L1Location[]{
                    new L1Location(32743, 32750, 813),
                    new L1Location(32741, 32745, 813)
            });

            Map<String, L1Location[]> moveAbleLocation = new HashMap<>();

            moveAbleLocation.put("본던7층입구->우측끝", new L1Location[]{
                    new L1Location(32728, 32724, 813),
                    new L1Location(32728, 32739, 813)
            });

            moveAbleLocation.put("본던7층구석->좌상구석", new L1Location[]{
                    new L1Location(32732, 32750, 813),
                    new L1Location(32730, 32752, 813)
            });
            moveAbleLocation.put("좌모닥우측->좌모닥", new L1Location[]{
                    new L1Location(32795, 32736, 53),
                    new L1Location(32801, 32741, 53)
            });

            moveAbleLocation.put("던전7층버그방1", new L1Location[]{
                    new L1Location(32735, 32789, 812),
                    new L1Location(32735, 32782, 812)
            });

            moveAbleLocation.put("던전7층버그방2", new L1Location[]{
                    new L1Location(32763, 32791, 813),
                    new L1Location(32772, 32789, 813)
            });


            L1NpcInstance attacker = NpcTable.getInstance().newNpcInstance(5000111);

            L1PcInstance target = new CharacterTable().loadCharacter("메티스");
            C_SelectCharacter.init(target);

            logger.info("움직일수 있는 동선 체크 테스트 시작");
            for (String key : moveAbleLocation.keySet()) {
                L1Location[] locations = moveAbleLocation.get(key);
                boolean moveCheck = moveCheck(attacker, target, locations);
                logger.info("key : {} , moveCheck : {} totalCount : {}", key, moveCheck, attacker.getMove().getTotalCheckCount());
                Assertions.assertTrue(moveCheck);
            }

            logger.info("움직일수 있는 동선 체크 테스트 종료");

            System.out.println();

            logger.info("움직일수 없는 동선 체크 테스트 시작");
            for (String key : dontMoveAbleLocation.keySet()) {
                L1Location[] locations = dontMoveAbleLocation.get(key);
                boolean moveCheck = moveCheck(attacker, target, locations);

                logger.info("key : {} , moveCheck : {} totalCount : {}", key, moveCheck, attacker.getMove().getTotalCheckCount());

                Assertions.assertFalse(moveCheck);
            }
            logger.info("움직일수 없는 동선 체크 테스트 종료");
        } catch (Exception e) {
            logger.error("오류", e);
        }
    }

    private boolean moveCheck(L1NpcInstance attacker, L1Character target, L1Location[] locations) {
        attacker.targetClear();
        attacker.setLocation(locations[0]);
        attacker.setHomeX(attacker.getX());
        attacker.setHomeY(attacker.getY());
        L1World.getInstance().addVisibleObject(attacker);
        target.setLocation(locations[1]);
        ((L1PcInstance) target).tell();


        boolean isAttack = false;

        for (int i = 0; i < 150; i++) {
            attacker.searchTarget();
            attacker.onTarget();

//            if (i == 55) {
//                L1CharPosUtils.setDirectionMove(target, 2);
//            }

            if (L1CharPosUtils.isAttackPosition(attacker, target, attacker.getTemplate().getRanged())) {
                isAttack = true;
                break;
            }
        }

        return isAttack;
    }
}
