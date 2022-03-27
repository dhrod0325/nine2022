package ks.model.instance;

import ks.model.L1Npc;
import ks.model.pc.L1PcInstance;

public class L1ModelInstance extends L1NpcInstance {

    public L1ModelInstance(L1Npc template) {
        super(template);
    }

    @Override
    public void onAction(L1PcInstance pc) {
    }

    @Override
    public void deleteMe() {
    }
}
