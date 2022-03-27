package ks.core.datatables;

import ks.model.L1Teleport;
import ks.model.pc.L1PcInstance;
import ks.scheduler.timer.gametime.GameTimeScheduler;
import ks.util.common.SqlUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class DungeonTable {
    private static final DungeonTable instance = new DungeonTable();

    private static final Map<String, NewDungeon> dungeonMap = new HashMap<>();

    public static DungeonTable getInstance() {
        return instance;
    }

    private final Logger logger = LogManager.getLogger();

    public void load() {
        dungeonMap.clear();

        SqlUtils.query("SELECT * FROM dungeon", (rs, i) -> {
            int srcMapId = rs.getInt("src_mapid");
            int srcX = rs.getInt("src_x");
            int srcY = rs.getInt("src_y");

            String key = String.valueOf(srcMapId) + srcX + srcY;

            int newX = rs.getInt("new_x");
            int newY = rs.getInt("new_y");
            int newMapId = rs.getInt("new_mapid");
            int heading = rs.getInt("new_heading");

            DungeonType dungeonType = DungeonType.NONE;

            if ((srcX >= 33423 && srcX <= 33426) && srcY == 33502 && srcMapId == 4 || (srcX >= 32733 && srcX <= 32736) && srcY == 32794 && srcMapId == 83) {
                dungeonType = DungeonType.SHIP_FOR_FI;
            } else if ((srcX >= 32935 && srcX <= 32937)
                    && srcY == 33058
                    && srcMapId == 70
                    || (srcX >= 32732 && srcX <= 32735) && srcY == 32796
                    && srcMapId == 84) {
                dungeonType = DungeonType.SHIP_FOR_HEINE;
            } else if ((srcX >= 32750 && srcX <= 32752)
                    && srcY == 32874
                    && srcMapId == 445
                    || (srcX >= 32731 && srcX <= 32733) && srcY == 32796
                    && srcMapId == 447) {
                dungeonType = DungeonType.SHIP_FOR_PI;
            } else if ((srcX >= 32296 && srcX <= 32298)
                    && srcY == 33087
                    && srcMapId == 440
                    || (srcX >= 32735 && srcX <= 32737) && srcY == 32794
                    && srcMapId == 446) {
                dungeonType = DungeonType.SHIP_FOR_HIDDENDOCK;
            } else if ((srcX >= 32630 && srcX <= 32632)
                    && srcY == 32983
                    && srcMapId == 0
                    || (srcX >= 32733 && srcX <= 32735) && srcY == 32796
                    && srcMapId == 5) {
                dungeonType = DungeonType.SHIP_FOR_GLUDIN;
            } else if ((srcX >= 32540 && srcX <= 32545)
                    && srcY == 32728
                    && srcMapId == 4
                    || (srcX >= 32734 && srcX <= 32737) && srcY == 32794
                    && srcMapId == 6) {
                dungeonType = DungeonType.SHIP_FOR_TI;
            }

            NewDungeon newDungeon = new NewDungeon(newX, newY, (short) newMapId, heading, dungeonType);

            dungeonMap.put(key, newDungeon);

            return null;
        });
    }

    public boolean dg(int locX, int locY, int mapId, L1PcInstance pc) {
        int serverTime = GameTimeScheduler.getInstance().getTime().getSeconds();
        int nowTime = serverTime % 86400;

        String key = String.valueOf(mapId) + locX + locY;

        if (dungeonMap.containsKey(key)) {
            NewDungeon newDungeon = dungeonMap.get(key);
            short newMap = newDungeon.newMapId;
            int newX = newDungeon.newX;
            int newY = newDungeon.newY;
            int heading = newDungeon.heading;
            DungeonType dungeonType = newDungeon.dungeonType;
            boolean teleportable = false;

            logger.debug("nowTime : {}", (nowTime / 360));

            //-78 = 22:38

            if (dungeonType == DungeonType.NONE) {
                teleportable = true;
            } else {
                if (nowTime >= 15 * 60 * 6 && nowTime < 25 * 360 // 1.30~2.30
                        || nowTime >= 45 * 360 && nowTime < 55 * 360 // 4.30~5.30
                        || nowTime >= 75 * 360 && nowTime < 85 * 360 // 7.30~8.30
                        || nowTime >= 105 * 360 && nowTime < 115 * 360 // 10.30~11.30
                        || nowTime >= 135 * 360 && nowTime < 145 * 360// 13.30~14.30
                        || nowTime >= 165 * 360 && nowTime < 175 * 360// 16.30~17.30
                        || nowTime >= 195 * 360 && nowTime < 205 * 360// 19.30~20.30
                        || nowTime >= 225 * 360 && nowTime < 235 * 360// 22.30~23.30
                ) {
                    if ((pc.getInventory().checkItem(40299, 1) && dungeonType == DungeonType.SHIP_FOR_GLUDIN) // TalkingIslandShiptoAdenMainland
                            || (pc.getInventory().checkItem(40301, 1) && dungeonType == DungeonType.SHIP_FOR_HEINE) // AdenMainlandShiptoForgottenIsland
                            || (pc.getInventory().checkItem(40302, 1) && dungeonType == DungeonType.SHIP_FOR_PI)) { // ShipPirateislandtoHiddendock
                        teleportable = true;
                    }
                } else if (nowTime >= 0 && nowTime < 10 * 360
                        || nowTime >= 30 * 360 && nowTime < 40 * 360
                        || nowTime >= 60 * 360 && nowTime < 70 * 360
                        || nowTime >= 90 * 360 && nowTime < 100 * 360
                        || nowTime >= 120 * 360 && nowTime < 130 * 360
                        || nowTime >= 150 * 360 && nowTime < 160 * 360
                        || nowTime >= 180 * 360 && nowTime < 190 * 360
                        || nowTime >= 210 * 360 && nowTime < 220 * 360
                ) {
                    if ((pc.getInventory().checkItem(40298, 1) && dungeonType == DungeonType.SHIP_FOR_TI) // AdenMainlandShiptoTalkingIsland
                            || (pc.getInventory().checkItem(40300, 1) && dungeonType == DungeonType.SHIP_FOR_FI) // 잊섬 배
                            || (pc.getInventory().checkItem(40303, 1) && dungeonType == DungeonType.SHIP_FOR_HIDDENDOCK)) { // ShipHiddendocktoPirateisland
                        teleportable = true;
                    }
                }
            }

            if (teleportable) {
                if (!pc.getTimer().isTimeOver("dg")) {
                    return false;
                }

                pc.getTimer().setWaitTime("dg", 1000);

                L1Teleport.teleport(pc, newX, newY, newMap, heading, false);

                return true;
            }
        }
        return false;
    }

    private enum DungeonType {
        NONE, SHIP_FOR_FI, SHIP_FOR_HEINE, SHIP_FOR_PI, SHIP_FOR_HIDDENDOCK, SHIP_FOR_GLUDIN, SHIP_FOR_TI
    }

    private static class NewDungeon {
        int newX;
        int newY;
        short newMapId;
        int heading;

        DungeonType dungeonType;

        private NewDungeon(int newX, int newY, short newMapId, int heading, DungeonType dungeonType) {
            this.newX = newX;
            this.newY = newY;
            this.newMapId = newMapId;
            this.heading = heading;
            this.dungeonType = dungeonType;
        }
    }
}
