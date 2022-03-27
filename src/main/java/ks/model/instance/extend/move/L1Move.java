package ks.model.instance.extend.move;

import ks.model.L1Character;

public interface L1Move {
    void targetResetting(L1Character attacker, L1Character target);

    void targetRemove(L1Character target);

    void targetInit(L1Character attacker, L1Character target);

    void validateTarget(L1Character attacker, L1Character target);

    int calcDirection(L1Character attacker, L1Character target);

    int getTotalCheckCount();
}
