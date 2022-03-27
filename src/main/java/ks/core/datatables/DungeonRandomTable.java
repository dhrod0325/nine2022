package ks.core.datatables;

import ks.model.L1Teleport;
import ks.model.attack.utils.L1MagicUtils;
import ks.model.pc.L1PcInstance;
import ks.util.common.SqlUtils;
import ks.util.common.random.RandomUtils;

import java.util.HashMap;
import java.util.Map;

public class DungeonRandomTable {
    private static final DungeonRandomTable instance = new DungeonRandomTable();

    private static final Map<String, NewDungeonRandom> dungeonMap = new HashMap<>();

    public static DungeonRandomTable getInstance() {
        return instance;
    }

    public void load() {
        dungeonMap.clear();

        SqlUtils.query("SELECT * FROM dungeon_random", (rs, i) -> {
            int srcMapId = rs.getInt("src_mapid");
            int srcX = rs.getInt("src_x");
            int srcY = rs.getInt("src_y");
            String key = String.valueOf(srcMapId) + srcX + srcY;
            int[] newX = new int[5];
            int[] newY = new int[5];
            short[] newMapId = new short[5];
            newX[0] = rs.getInt("new_x1");
            newY[0] = rs.getInt("new_y1");
            newMapId[0] = rs.getShort("new_mapid1");
            newX[1] = rs.getInt("new_x2");
            newY[1] = rs.getInt("new_y2");
            newMapId[1] = rs.getShort("new_mapid2");
            newX[2] = rs.getInt("new_x3");
            newY[2] = rs.getInt("new_y3");
            newMapId[2] = rs.getShort("new_mapid3");
            newX[3] = rs.getInt("new_x4");
            newY[3] = rs.getInt("new_y4");
            newMapId[3] = rs.getShort("new_mapid4");
            newX[4] = rs.getInt("new_x5");
            newY[4] = rs.getInt("new_y5");
            newMapId[4] = rs.getShort("new_mapid5");
            int heading = rs.getInt("new_heading");

            NewDungeonRandom newDungeonRandom = new NewDungeonRandom(newX, newY, newMapId, heading);
            dungeonMap.put(key, newDungeonRandom);

            return null;
        });
    }

    public boolean dg(int locX, int locY, int mapId, L1PcInstance pc) {
        String key = String.valueOf(mapId) + locX + locY;

        if (dungeonMap.containsKey(key)) {
            int rnd = RandomUtils.nextInt(5);
            NewDungeonRandom newDungeonRandom = dungeonMap.get(key);
            short newMap = newDungeonRandom.newMapId[rnd];
            int newX = newDungeonRandom.newX[rnd];
            int newY = newDungeonRandom.newY[rnd];
            int heading = newDungeonRandom.heading;

            L1MagicUtils.startAbsoluteBarrier(pc, 2000);

            L1Teleport.teleport(pc, newX, newY, newMap, heading, true);

            return true;
        }

        return false;
    }

    private static class NewDungeonRandom {
        int[] newX = new int[5];
        int[] newY = new int[5];
        short[] newMapId = new short[5];

        int heading;

        private NewDungeonRandom(int[] newX, int[] newY, short[] newMapId, int heading) {
            for (int i = 0; i < 5; i++) {
                this.newX[i] = newX[i];
                this.newY[i] = newY[i];
                this.newMapId[i] = newMapId[i];
            }
            this.heading = heading;
        }
    }
}
