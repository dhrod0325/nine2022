package ks.model.instance.extend.move;

import ks.model.L1Character;
import ks.util.L1CharPosUtils;

public class L1DefaultMove implements L1Move {
    @Override
    public void targetResetting(L1Character attacker, L1Character target) {

    }

    @Override
    public void targetRemove(L1Character target) {

    }

    @Override
    public void targetInit(L1Character attacker, L1Character target) {

    }

    @Override
    public void validateTarget(L1Character attacker, L1Character target) {

    }

    @Override
    public int calcDirection(L1Character attacker, L1Character target) {
        return L1CharPosUtils.calcMoveDirection(attacker, target.getX(), target.getY());
    }

    @Override
    public int getTotalCheckCount() {
        return 0;
    }
}
