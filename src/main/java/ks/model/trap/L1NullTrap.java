package ks.model.trap;

import ks.model.instance.L1TrapInstance;
import ks.model.pc.L1PcInstance;

public class L1NullTrap extends L1Trap {
    public L1NullTrap() {
        super(0, 0, false);
    }

    @Override
    public void onTrod(L1PcInstance from, L1TrapInstance trap) {
    }
}
