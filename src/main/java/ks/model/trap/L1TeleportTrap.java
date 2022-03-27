package ks.model.trap;

import ks.core.storage.TrapStorage;
import ks.model.L1Location;
import ks.model.L1Teleport;
import ks.model.instance.L1TrapInstance;
import ks.model.pc.L1PcInstance;

public class L1TeleportTrap extends L1Trap {
    private final L1Location loc;

    public L1TeleportTrap(TrapStorage storage) {
        super(storage);

        int x = storage.getInt("teleportX");
        int y = storage.getInt("teleportY");
        int mapId = storage.getInt("teleportMapId");
        loc = new L1Location(x, y, mapId);
    }

    @Override
    public void onTrod(L1PcInstance from, L1TrapInstance trap) {
        sendEffect(trap);

        L1Teleport.teleport(from, loc.getX(), loc.getY(), (short) loc.getMapId(), 5, true);
    }
}
