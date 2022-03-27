package ks.model.instance;

import ks.model.L1Character;
import ks.model.L1Npc;
import ks.model.L1World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class L1AntCaveGuardianInstance extends L1MonsterInstance {
    private final Logger logger = LogManager.getLogger();

    public L1AntCaveGuardianInstance(L1Npc template) {
        super(template);
    }

    @Override
    public void dead(L1Character attacker) {
        super.dead(attacker);

        int doorId = getSpawn().getDoorId();

        logger.info("doorId:" + doorId);

        if (doorId > 0) {
            L1DoorInstance door = L1World.getInstance().findDoor(doorId);
            door.open();
        }
    }
}
