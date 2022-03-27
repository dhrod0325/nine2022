package ks.model.attack.magic.impl.prob;

import ks.core.datatables.SkillsTable;
import ks.model.L1Character;
import ks.model.L1Skills;
import ks.model.attack.magic.impl.L1MagicProbability;
import ks.model.pc.L1PcInstance;
import ks.util.common.random.RandomUtils;

public class L1MagicProbDefaultPc implements L1MagicProbability {
    private final L1PcInstance attacker;

    private final L1Character target;

    public L1MagicProbDefaultPc(L1PcInstance attacker, L1Character target) {
        this.attacker = attacker;
        this.target = target;
    }

    @Override
    public int calcProbability(int skillId) {
        L1Skills skill = SkillsTable.getInstance().getTemplate(skillId);

        int probability = 0;

        int dice1 = skill.getProbabilityDice();
        int magicLevel = attacker.getAbility().getMagicLevel();
        int magicBonus = attacker.getAbility().getMagicBonus();

        int targetMr = target.getResistance().getEffectedMrBySkill();

        int diceCount;

        if (attacker.isWizard()) {
            diceCount = magicBonus + magicLevel + 1;
        } else if (attacker.isElf()) {
            diceCount = magicBonus + magicLevel - 1;
        } else if (attacker.isDragonKnight()) {
            diceCount = magicBonus + magicLevel;
        } else {
            diceCount = magicBonus + magicLevel - 1;
        }

        if (diceCount < 1) {
            diceCount = 1;
        }

        if (dice1 > 0) {
            for (int i = 0; i < diceCount; i++) {
                probability += (RandomUtils.nextInt(dice1) + 1);
            }
        }

        probability -= targetMr;

        return probability;
    }
}
