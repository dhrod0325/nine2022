package ks.model.instance;

import ks.model.L1Npc;
import ks.model.L1Teleport;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_NPCPack;

@SuppressWarnings("unused")
public class L1NearTeleporterInstance extends L1TeleporterInstance {
    public L1NearTeleporterInstance(L1Npc template) {
        super(template);
    }

    @Override
    public void onNpcAI() {
        if (isAiRunning()) {
            return;
        }
        setActivated(false);
        startAI();
    }

    @Override
    public void onPerceive(L1PcInstance perceivedFrom) {
        perceivedFrom.getNearObjects().addKnownObject(this);
        perceivedFrom.sendPackets(new S_NPCPack(this));

        getNearObjects().addKnownObject(perceivedFrom);

        onNpcAI();
    }

    @Override
    public boolean toAi() {
        for (L1PcInstance pc : getNearObjects().getKnownPlayers()) {
            if (pc.getLocation().getTileDistance(getLocation()) <= getTemplate().getRanged()) {
                onTalkAction(pc);
                getNearObjects().removeKnownObject(pc);
            }
        }

        return false;
    }

    @Override
    public void onTalkAction(L1PcInstance pc) {
        if (getNpcId() == 4500100) {
            L1Teleport.teleport(pc, 32639, 32876, (short) 780, 2, false);
        } else if (getNpcId() == 460000093) {
            L1Teleport.teleport(pc, 32793, 32754, (short) 783, 2, false);
        }
    }
}
