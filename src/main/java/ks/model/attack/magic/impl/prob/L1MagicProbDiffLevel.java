package ks.model.attack.magic.impl.prob;

import ks.core.datatables.SkillsTable;
import ks.model.L1Character;
import ks.model.L1Skills;
import ks.model.attack.magic.impl.L1MagicProbability;
import ks.model.skill.utils.L1SkillUtils;

public class L1MagicProbDiffLevel implements L1MagicProbability {
    private final L1Character attacker;

    private final L1Character target;

    private final int diffProbability;

    private final int oneLevelDiffProbability;

    public L1MagicProbDiffLevel(L1Character attacker, L1Character target, int diffProbability, int oneLevelDiffProbability) {
        this.attacker = attacker;
        this.target = target;
        this.diffProbability = diffProbability;
        this.oneLevelDiffProbability = oneLevelDiffProbability;
    }

    @Override
    public int calcProbability(int skillId) {
        L1Skills skill = SkillsTable.getInstance().getTemplate(skillId);

        int probability = skill.getProbabilityValue();
        probability += L1SkillUtils.calcProbability(attacker.getLevel() - target.getLevel(), diffProbability, oneLevelDiffProbability);

        return probability;
    }
}
