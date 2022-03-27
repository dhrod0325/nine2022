package ks.test;

import ks.core.datatables.item.ItemTable;
import ks.core.datatables.npc.NpcTable;
import ks.core.datatables.pc.CharacterTable;
import ks.model.L1Location;
import ks.model.attack.physics.L1AttackRun;
import ks.model.attack.physics.impl.vo.L1AttackParam;
import ks.model.instance.L1ItemInstance;
import ks.model.instance.L1NpcInstance;
import ks.model.pc.L1PcInstance;
import ks.packets.clientpackets.C_SelectCharacter;
import ks.base.AbstractTest;
import org.junit.jupiter.api.Assertions;

public class WeaponAttrDamageTest extends AbstractTest {
    public void test() {
        try {
            logger.info("무기 속성대미지체크 시작");

            L1PcInstance attacker = new CharacterTable().loadCharacter("메티스");
            C_SelectCharacter.init(attacker);

            attacker.setLocation(new L1Location(32743, 32750, 813));

            L1NpcInstance target = NpcTable.getInstance().newNpcInstance(5000111);
            target.setLocation(attacker.getLocation());

            //오리하루콘단검
            L1ItemInstance weapon1 = ItemTable.getInstance().createItem(9);
            weapon1.setEnchantLevel(7);

            L1ItemInstance weapon2 = ItemTable.getInstance().createItem(9);
            weapon2.setEnchantLevel(7);
            weapon2.setAttrEnchantLevel(10);//수령 5단

            attacker.setWeapon(weapon1);

            int attackCount = 100;
            int dmgSum1 = 0;

            for (int i = 0; i < attackCount; i++) {
                L1AttackRun attackRun = new L1AttackRun(attacker, target);
                int dmg = attackRun.getAttack().getDamage().calcDamage(new L1AttackParam());

                dmgSum1 += dmg;
            }

            attacker.setWeapon(weapon2);

            int dmgSum2 = 0;

            for (int i = 0; i < attackCount; i++) {
                L1AttackRun attackRun = new L1AttackRun(attacker, target);
                int dmg = attackRun.getAttack().getDamage().calcDamage(new L1AttackParam());

                dmgSum2 += dmg;
            }

            int check1 = dmgSum1 / attackCount;
            int check2 = dmgSum2 / attackCount;

            logger.info("무속성 7오단 평균 대미지 : " + check1);
            logger.info("속성5단 7오단 평균 대미지 : " + check2);

            Assertions.assertTrue(check2 > check1);

            logger.info("무기 속성대미지체크 종료");
        } catch (Exception e) {
            logger.error("오류", e);
        }
    }
}
