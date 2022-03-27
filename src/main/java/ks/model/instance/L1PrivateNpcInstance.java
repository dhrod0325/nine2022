package ks.model.instance;

import ks.model.L1Npc;
import ks.model.attack.physics.L1AttackRun;
import ks.model.pc.L1PcInstance;

@SuppressWarnings("unused")
public class L1PrivateNpcInstance extends L1NpcInstance {
    public L1PrivateNpcInstance(L1Npc template) {
        super(template);
    }

    public void onAction(L1PcInstance pc) {
        L1AttackRun attack = new L1AttackRun(pc, this);
        attack.action();
    }
}
