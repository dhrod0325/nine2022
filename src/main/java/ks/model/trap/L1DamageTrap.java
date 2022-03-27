package ks.model.trap;

import ks.core.storage.TrapStorage;
import ks.model.instance.L1TrapInstance;
import ks.model.pc.L1PcInstance;

public class L1DamageTrap extends L1Trap {
    private final L1Dice dice;

    private final int base;

    private final int diceCount;

    public L1DamageTrap(TrapStorage storage) {
        super(storage);

        dice = new L1Dice(storage.getInt("dice"));
        base = storage.getInt("base");
        diceCount = storage.getInt("diceCount");
    }

    @Override
    public void onTrod(L1PcInstance from, L1TrapInstance trap) {
        sendEffect(trap);

        int dmg = dice.roll(diceCount) + base;
        from.receiveDamage(from, dmg);
    }
}
