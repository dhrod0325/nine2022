package ks.system.lastabard;

import ks.model.L1Npc;
import ks.model.L1Spawn;
import ks.model.instance.L1MonsterInstance;
import ks.model.instance.L1NpcInstance;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LastabardSpawn extends L1Spawn {
    private final Logger logger = LogManager.getLogger();

    private int countMapId;

    public LastabardSpawn(L1Npc mobTemplate) throws Exception {
        super(mobTemplate);
    }

    @Override
    public L1NpcInstance doSpawn(int spawnNumber, int objectId, boolean isReUse) {
        try {
            L1NpcInstance npc = super.doSpawn(spawnNumber, objectId, isReUse);

            if (npc instanceof L1MonsterInstance) {
                L1MonsterInstance mon = (L1MonsterInstance) npc;
                mon.setDeath(new LastabardDeath(mon, getLocX(), getLocY(), getDoorId(), getCountMapId()));
            }
        } catch (Exception e) {
            logger.error(e);
        }

        return null;
    }

    public int getCountMapId() {
        return countMapId;
    }

    public void setCountMapId(int countMapId) {
        this.countMapId = countMapId;
    }
}
