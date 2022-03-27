package ks.model.instance;

import ks.model.L1Npc;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_SignboardPack;

@SuppressWarnings("unused")
public class L1SignboardInstance extends L1NpcInstance {
    public L1SignboardInstance(L1Npc template) {
        super(template);
    }

    @Override
    public void onAction(L1PcInstance pc) {
    }

    @Override
    public void onPerceive(L1PcInstance perceivedFrom) {
        if (perceivedFrom == null)
            return;

        perceivedFrom.getNearObjects().addKnownObject(this);
        perceivedFrom.sendPackets(new S_SignboardPack(this));
    }
}
