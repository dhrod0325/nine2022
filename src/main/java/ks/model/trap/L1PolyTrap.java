package ks.model.trap;

import ks.core.storage.TrapStorage;
import ks.model.L1PolyMorph;
import ks.model.instance.L1TrapInstance;
import ks.model.pc.L1PcInstance;

public class L1PolyTrap extends L1Trap {
    private final int polyId;

    private final int polySeconds;

    public L1PolyTrap(TrapStorage storage) {
        super(storage);

        polyId = storage.getInt("polyId");
        polySeconds = storage.getInt("polySeconds");
    }

    @Override
    public void onTrod(L1PcInstance from, L1TrapInstance trap) {
        sendEffect(trap);

        L1PolyMorph.doPoly(from, polyId, polySeconds, L1PolyMorph.MORPH_BY_GM);
    }
}
