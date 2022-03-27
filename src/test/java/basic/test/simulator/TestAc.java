package basic.test.simulator;

import ks.core.datatables.pc.CharacterTable;
import ks.model.attack.magic.L1MagicRun;
import ks.model.attack.physics.L1AttackRun;
import ks.model.pc.L1PcInstance;
import ks.packets.clientpackets.C_SelectCharacter;
import basic.test.BaseTest;

public class TestAc extends BaseTest {
    public static void main(String[] args) throws Exception {

//        L1MonsterInstance attacker = (L1MonsterInstance) NpcTable.getInstance().newNpcInstance(5000114);

        L1PcInstance attacker = new CharacterTable().loadCharacter("테스트2");
        C_SelectCharacter.init(attacker);

        L1PcInstance target = new CharacterTable().loadCharacter("테스트2");
        C_SelectCharacter.init(target);

        attacker.setLocation(target.getLocation());

        attacker.setLevel(70);
        attacker.addHitUp(45);
        attacker.getAbility().setDex(17);

        target.setLevel(70);

        attacker.setLevel(target.getLevel());
        target.getAC().setAc(-50);

        attacker.setLocation(target.getLocation());

        for (int i = 0; i < 30; i++) {
            L1AttackRun attackRun = new L1AttackRun(attacker, target);
            int hit = attackRun.getAttackParam().getHitRate();

            System.out.println("공격자 명중 : " + attacker.getTotalHitUp() + " AC : " + target.getAC().getAc() + ", 명중 : " + hit);

            target.getAC().addAc(-5);
        }

        System.exit(0);
    }

    private static class Result {
        public String name;
        public int mr;
        public int dmg;

        public Result(String name, int mr, int dmg) {
            this.name = name;
            this.mr = mr;
            this.dmg = dmg;
        }
    }

    public static int avgDmg(L1MagicRun magic, int skillId) {
        int count = 100;

        int avg = 0;

        for (int cnt = 0; cnt < count; cnt++) {
            int damage = magic.calcMagicDamage(skillId);
            avg += damage;
        }

        return avg / count;
    }
}
