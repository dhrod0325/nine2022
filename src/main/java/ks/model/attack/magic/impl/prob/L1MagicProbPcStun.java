package ks.model.attack.magic.impl.prob;

import ks.core.datatables.SkillsTable;
import ks.model.L1Character;
import ks.model.L1Skills;
import ks.model.attack.magic.impl.L1MagicProbability;
import ks.model.pc.L1PcInstance;
import ks.model.skill.utils.L1SkillUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class L1MagicProbPcStun implements L1MagicProbability {
    private final Logger logger = LogManager.getLogger();
    private final L1PcInstance attacker;

    private final L1Character target;

    public L1MagicProbPcStun(L1PcInstance attacker, L1Character target) {
        this.attacker = attacker;
        this.target = target;
    }

    @Override
    public int calcProbability(int skillId) {
        L1Skills skill = SkillsTable.getInstance().getTemplate(skillId);

        int probability = 0;

        try {
            probability = skill.getProbabilityValue();
            probability += L1SkillUtils.calcProbability(attacker.getLevel() - target.getLevel(), 2);
            probability += attacker.getAddStunHit();
        } catch (Exception e) {
            logger.error("오류", e);
        }

        return probability;
    }
}