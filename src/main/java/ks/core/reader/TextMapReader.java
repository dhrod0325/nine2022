package ks.core.reader;

import ks.app.config.prop.CodeConfig;
import ks.core.datatables.MapsTable;
import ks.model.map.L1Map;
import ks.model.map.L1V1Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class TextMapReader extends MapReader {
    public static final int MAPINFO_MAP_NO = 0;
    public static final int MAPINFO_START_X = 1;
    public static final int MAPINFO_END_X = 2;
    public static final int MAPINFO_START_Y = 3;
    public static final int MAPINFO_END_Y = 4;

    private static final String MAP_DIR = CodeConfig.MAP_DIR;

    private static final Logger logger = LogManager.getLogger(TextMapReader.class.getName());
    private static int[][] MAP_INFO = {};

    public TextMapReader() {
        Map<Integer, MapsTable.MapData> maps = MapsTable.getInstance().getMaps();
        MAP_INFO = new int[maps.keySet().size()][5];

        int i = 0;

        for (MapsTable.MapData map : maps.values()) {
            MAP_INFO[i][0] = map.mapId;
            MAP_INFO[i][1] = map.startX;
            MAP_INFO[i][2] = map.endX;
            MAP_INFO[i][3] = map.startY;
            MAP_INFO[i][4] = map.endY;

            i++;
        }
    }

    public byte[][] read(final int mapId, final int xSize, final int ySize) throws IOException {
        byte[][] map = new byte[xSize][ySize];

        try {
            LineNumberReader in = new LineNumberReader(new BufferedReader(new FileReader(MAP_DIR + mapId + ".txt")));

            int x, y = 0;
            String line;
            byte tile;
            StringTokenizer tok;

            while ((line = in.readLine()) != null) {
                if (line.trim().length() == 0 || line.startsWith("#") || line.startsWith("3")) {
                    continue;
                }

                x = 0;

                tok = new StringTokenizer(line, ",");

                while (tok.hasMoreTokens()) {
                    String t = tok.nextToken();

                    tile = (byte) Short.parseShort(t);

                    map[x][y] = tile;

                    x++;
                }

                y++;
            }

            in.close();

            return map;
        } catch (Exception e) {
            //logger.error("오류", e);
            logger.error("map load fail : " + mapId);
            return map;
        }

    }

    @Override
    public L1Map read(final int id) throws IOException {
        for (int[] info : MAP_INFO) {
            int mapId = info[MAPINFO_MAP_NO];
            int xSize = info[MAPINFO_END_X] - info[MAPINFO_START_X] + 1;
            int ySize = info[MAPINFO_END_Y] - info[MAPINFO_START_Y] + 1;

            if (mapId == id) {
                return new L1V1Map((short) mapId, read(mapId, xSize, ySize),
                        info[MAPINFO_START_X],
                        info[MAPINFO_START_Y],
                        MapsTable.getInstance().isUnderwater(mapId),
                        MapsTable.getInstance().isMarkAble(mapId),
                        MapsTable.getInstance().isTeleportAble(mapId),
                        MapsTable.getInstance().isEscapeAble(mapId),
                        MapsTable.getInstance().isUseResurrection(mapId),
                        MapsTable.getInstance().isUsePainWand(mapId),
                        MapsTable.getInstance().isEnabledDeathPenalty(mapId),
                        MapsTable.getInstance().isTakePets(mapId),
                        MapsTable.getInstance().isRecallPets(mapId),
                        MapsTable.getInstance().isUseAbleItem(mapId),
                        MapsTable.getInstance().isUseAbleSkill(mapId)
                );
            }
        }

        return null;
    }

    @Override
    public Map<Integer, L1Map> read() throws IOException {
        Map<Integer, L1Map> maps = new HashMap<>();

        for (int[] info : MAP_INFO) {
            int mapId = info[MAPINFO_MAP_NO];
            int xSize = info[MAPINFO_END_X] - info[MAPINFO_START_X] + 1;
            int ySize = info[MAPINFO_END_Y] - info[MAPINFO_START_Y] + 1;

            try {
                L1V1Map map = new L1V1Map(
                        (short) mapId,
                        read(mapId, xSize, ySize),
                        info[MAPINFO_START_X],
                        info[MAPINFO_START_Y],
                        MapsTable.getInstance().isUnderwater(mapId),
                        MapsTable.getInstance().isMarkAble(mapId),
                        MapsTable.getInstance().isTeleportAble(mapId),
                        MapsTable.getInstance().isEscapeAble(mapId),
                        MapsTable.getInstance().isUseResurrection(mapId),
                        MapsTable.getInstance().isUsePainWand(mapId),
                        MapsTable.getInstance().isEnabledDeathPenalty(mapId),
                        MapsTable.getInstance().isTakePets(mapId),
                        MapsTable.getInstance().isRecallPets(mapId),
                        MapsTable.getInstance().isUseAbleItem(mapId),
                        MapsTable.getInstance().isUseAbleSkill(mapId));

                maps.put(mapId, map);
            } catch (IOException e) {
                logger.error("TextMapReader.java에서 에러가 발생했습니다.", e);
            }
        }

        return maps;
    }
}
