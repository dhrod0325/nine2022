package basic.test.basic;

import ks.core.datatables.SkillsTable;
import ks.model.L1Skills;
import ks.model.attack.utils.L1MagicUtils;
import basic.test.BaseTest;

public class Test6 extends BaseTest {
    public static void main(String[] args) {
        SkillsTable.getInstance().load();

        int skillId = 45;
        int mr = 150;

        L1Skills skill = SkillsTable.getInstance().getTemplate(skillId);

        int damageDice = skill.getDamageDice();
        int diceCount = skill.getDamageDiceCount();
        int damageValue = skill.getDamageValue();
        int magicDamage = 0;

        double coefficient; // PC마법상수

        for (int i = 0; i < diceCount; i++) {
//            magicDamage += RandomUtils.nextInt(damageDice) + 1;
            magicDamage += damageDice + 1;
        }

        magicDamage += damageValue;

        int powerSp = 28;
        int powerInt = 18;

        coefficient = (1.0 + (powerSp * 0.02) + ((powerInt - 9) * 0.13));

        if (coefficient < 1) {
            coefficient = 1;
        }

        magicDamage *= coefficient;
        magicDamage -= magicDamage * L1MagicUtils.reduceDamageByMr(mr);

        System.out.println(magicDamage);
    }
}
