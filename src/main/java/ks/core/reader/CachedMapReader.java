package ks.core.reader;

import ks.app.config.prop.CodeConfig;
import ks.core.datatables.MapsTable;
import ks.model.map.L1Map;
import ks.model.map.L1V1Map;
import ks.util.common.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.*;

public class CachedMapReader extends MapReader {
    private static final String MAP_DIR = CodeConfig.MAP_DIR;
    private static final String CACHE_DIR = "./data/mapcache/";
    private final Logger logger = LogManager.getLogger(getClass());

    private List<Integer> listMapIds() {
        List<Integer> ids = new ArrayList<>();

        File mapDir = new File(MAP_DIR);

        for (String name : Objects.requireNonNull(mapDir.list())) {
            File mapFile = new File(mapDir, name);

            if (!mapFile.exists()) {
                continue;
            }
            if (!FileUtils.getExtension(mapFile).equalsIgnoreCase("txt")) {
                continue;
            }

            int id;

            try {
                String idStr = FileUtils.getNameWithoutExtension(mapFile);
                id = Integer.parseInt(idStr);
            } catch (NumberFormatException e) {
                continue;
            }

            ids.add(id);
        }
        return ids;
    }

    private L1V1Map cacheMap(final int mapId) throws IOException {
        File file = new File(CACHE_DIR);

        if (!file.exists()) {
            file.mkdir();
        }

        L1V1Map map = (L1V1Map) new TextMapReader().read(mapId);

        if (map == null) {
            return null;
        }

        logger.info("load map : " + mapId);

        DataOutputStream out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(CACHE_DIR + mapId + ".map")));

        out.writeInt(map.getId());
        out.writeInt(map.getX());
        out.writeInt(map.getY());
        out.writeInt(map.getWidth());
        out.writeInt(map.getHeight());

        for (byte[] line : map.getRawTiles()) {
            for (byte tile : line) {
                out.writeByte(tile);
            }
        }

        out.flush();
        out.close();

        return map;
    }

    @Override
    public L1Map read(final int mapId) throws IOException {
        File file = new File(CACHE_DIR + mapId + ".map");

        if (!file.exists()) {
            return cacheMap(mapId);
        }

        DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(CACHE_DIR + mapId + ".map")));

        int id = in.readInt();

        if (mapId != id) {
            throw new FileNotFoundException();
        }

        int xLoc = in.readInt();
        int yLoc = in.readInt();
        int width = in.readInt();
        int height = in.readInt();

        byte[][] tiles = new byte[width][height];

        for (byte[] line : tiles) {
            in.read(line);
        }

        in.close();

        return new L1V1Map(id, tiles, xLoc, yLoc,
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
    }

    @Override
    public Map<Integer, L1Map> read() throws IOException {
        Map<Integer, L1Map> maps = new HashMap<>();

        for (int id : listMapIds()) {
            L1Map map = read(id);

            if (map != null)
                maps.put(id, read(id));
        }

        return maps;
    }
}
