package ks.model;

import ks.core.ObjectIdFactory;
import ks.core.datatables.npc.NpcTable;
import ks.model.instance.L1NpcInstance;
import ks.util.L1InstanceFactory;

public class L1WarSpawn {
    private static final L1WarSpawn instance = new L1WarSpawn();

    public static L1WarSpawn getInstance() {
        return instance;
    }

    public void spawnTower(int castleId) {
        int npcId = 81111;

        if (castleId == L1CastleLocation.ADEN_CASTLE_ID) {
            npcId = 81189;
        }

        L1Npc l1npc = NpcTable.getInstance().getTemplate(npcId);

        int[] loc = L1CastleLocation.getTowerLoc(castleId);

        SpawnWarObject(l1npc, loc[0], loc[1], (short) (loc[2]));

        if (castleId == L1CastleLocation.ADEN_CASTLE_ID) {
            spawnSubTower();
        }
    }

    private void spawnSubTower() {
        L1Npc l1npc;
        int[] loc;

        for (int i = 1; i <= 4; i++) {
            l1npc = NpcTable.getInstance().getTemplate(81189 + i);
            loc = L1CastleLocation.getSubTowerLoc(i);
            SpawnWarObject(l1npc, loc[0], loc[1], (short) (loc[2]));
        }
    }

    public void SpawnCrown(int castleId) {
        L1Npc l1npc = NpcTable.getInstance().getTemplate(81125);
        int[] loc = L1CastleLocation.getTowerLoc(castleId);
        SpawnWarObject(l1npc, loc[0], loc[1], (short) (loc[2]));
    }

    public void spawnFlag(int castleId) {
        L1Npc l1npc = NpcTable.getInstance().getTemplate(81122);
        int[] loc = L1CastleLocation.getWarArea(castleId);
        int x;
        int y;
        int locx1 = loc[0];
        int locx2 = loc[1];
        int locy1 = loc[2];
        int locy2 = loc[3];
        short mapid = (short) loc[4];

        try {
            for (x = locx1, y = locy1; x <= locx2; x += 8) {
                SpawnWarObject(l1npc, x, y, mapid);
                Thread.sleep(300);
            }
        } catch (Exception e1) {

        }
        try {
            for (x = locx2, y = locy1; y <= locy2; y += 8) {
                SpawnWarObject(l1npc, x, y, mapid);
                Thread.sleep(300);
            }
        } catch (Exception e1) {
        }

        try {
            for (x = locx2, y = locy2; x >= locx1; x -= 8) {
                SpawnWarObject(l1npc, x, y, mapid);
                Thread.sleep(300);

            }
        } catch (Exception ignored) {

        }

        try {
            for (x = locx1, y = locy2; y >= locy1; y -= 8) {
                SpawnWarObject(l1npc, x, y, mapid);
                Thread.sleep(300);
            }
        } catch (Exception ignored) {

        }
    }

    public void SpawnWarObject(L1Npc l1npc, int locx, int locy, short mapid) {
        try {
            if (l1npc != null) {
                L1NpcInstance npc = L1InstanceFactory.createInstance(l1npc);
                npc.setId(ObjectIdFactory.getInstance().nextId());
                npc.setX(locx);
                npc.setY(locy);
                npc.setHomeX(locx);
                npc.setHomeY(locy);
                npc.setHeading(0);
                npc.setMap(mapid);
                L1World.getInstance().storeObject(npc);
                L1World.getInstance().addVisibleObject(npc);
            }
        } catch (Exception ignored) {
        }
    }

}
