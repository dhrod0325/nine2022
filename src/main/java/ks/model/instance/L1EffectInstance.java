package ks.model.instance;

import ks.model.L1Npc;
import ks.model.pc.L1PcInstance;
import ks.scheduler.npc.NpcRestScheduler;

public class L1EffectInstance extends L1NpcInstance {
    private L1PcInstance spawner;

    public L1EffectInstance(L1Npc template) {
        super(template);

        if (getTemplate().getNpcId() == 81157) { // FW
            NpcRestScheduler.getInstance().addFireWall(this);
        }
    }

    public L1PcInstance getSpawner() {
        return spawner;
    }

    public void setSpawner(L1PcInstance spawner) {
        this.spawner = spawner;
    }

    @Override
    public void onAction(L1PcInstance pc) {
    }

    @Override
    public void onNpcAI() {
        if (getNpcId() == 460000188) {
            if (getPassiSpeed() <= 0)
                return;
            if (isAiRunning()) {
                return;
            }

            setActivated(false);
            startAI();
        } else {
            super.onNpcAI();
        }
    }

    @Override
    public boolean toAi() {
        return super.toAi();
    }

    @Override
    public void deleteMe() {
        allTargetClear();

        master = null;

        this.setSpawner(null);

        super.deleteSpawn();
    }
}
