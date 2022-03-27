package ks.model.attack.magic.impl.prob;

import ks.model.attack.magic.impl.L1MagicProbability;
import ks.model.instance.L1NpcInstance;

public class L1MagicProbNpcToNpc implements L1MagicProbability {
    private final L1NpcInstance attacker;
    private final L1NpcInstance target;

    public L1MagicProbNpcToNpc(L1NpcInstance attacker, L1NpcInstance target) {
        this.attacker = attacker;
        this.target = target;
    }

    @Override
    public int calcProbability(int skillId) {
        int probability = new L1MagicProbDiffLevel(attacker, target, 1, 2).calcProbability(skillId);

        if (probability > 95) {
            probability = 95;
        }

        return probability;
    }
}