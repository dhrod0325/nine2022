package ks.model.attack.magic.impl.prob;

import ks.core.datatables.SkillsTable;
import ks.model.L1Character;
import ks.model.L1Skills;
import ks.model.attack.magic.impl.L1MagicProbability;
import ks.model.attack.physics.L1AttackRun;
import ks.model.instance.L1NpcInstance;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class L1MagicProbNpcStun implements L1MagicProbability {
    private final Logger logger = LogManager.getLogger();

    private final L1NpcInstance attacker;

    private final L1Character target;

    public L1MagicProbNpcStun(L1NpcInstance attacker, L1Character target) {
        this.attacker = attacker;
        this.target = target;
    }

    @Override
    public int calcProbability(int skillId) {
        L1Skills skill = SkillsTable.getInstance().getTemplate(skillId);

        int probability = 0;

        try {
            L1AttackRun attack = new L1AttackRun(attacker, target);
            boolean isHit = attack.getAttackParam().isHitUp();

            if (isHit) {
                probability = skill.getProbabilityValue();
                probability += new L1MagicProbDiffLevel(attacker, target, 2, 0).calcProbability(skillId);
            }
        } catch (Exception e) {
            logger.error("오류", e);
        }

        return probability;
    }
}