package ks.model.instance;

import ks.model.L1Npc;
import ks.model.pc.L1PcInstance;

public class L1FieldObjectInstance extends L1NpcInstance {
    public L1FieldObjectInstance(L1Npc template) {
        super(template);
    }

    @Override
    public void onAction(L1PcInstance pc) {
    }

    @Override
    public void onTalkAction(L1PcInstance pc) {
    }

    @Override
    public void onPerceive(L1PcInstance perceivedFrom) {
        super.onPerceive(perceivedFrom);
    }

    @Override
    public void deleteMe() {
        super.deleteSpawn();
    }
}
