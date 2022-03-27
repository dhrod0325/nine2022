package ks.model.action.custom.impl.npc;

import ks.model.L1Object;
import ks.model.action.custom.L1AbstractNpcAction;
import ks.model.pc.L1PcInstance;
import ks.system.dogFight.L1DogFight;
import ks.system.race.L1RaceManager;

public class ActionRaceStatus extends L1AbstractNpcAction {
    public ActionRaceStatus(String action, L1PcInstance pc, L1Object obj) {
        super(action, pc, obj);
    }

    @Override
    public void execute() {
        if (npcId == 70035 || npcId == 70041 || npcId == 70042) {
            L1RaceManager.getInstance().talkStatus(pc, objId);
        } else if (L1DogFight.getInstance().getManager().getNpcId() == npcId) {
            L1DogFight.getInstance().talkStatus(pc, objId);
        }
    }
}
