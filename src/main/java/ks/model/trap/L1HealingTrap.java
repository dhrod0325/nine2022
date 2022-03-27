package ks.model.trap;

import ks.core.storage.TrapStorage;
import ks.model.instance.L1TrapInstance;
import ks.model.pc.L1PcInstance;

public class L1HealingTrap extends L1Trap {
    private final L1Dice _dice;

    private final int _base;

    private final int _diceCount;

    public L1HealingTrap(TrapStorage storage) {
        super(storage);

        _dice = new L1Dice(storage.getInt("dice"));
        _base = storage.getInt("base");
        _diceCount = storage.getInt("diceCount");
    }

    @Override
    public void onTrod(L1PcInstance from, L1TrapInstance trap) {
        sendEffect(trap);

        int pt = _dice.roll(_diceCount) + _base;

        from.healHp(pt);
    }
}
