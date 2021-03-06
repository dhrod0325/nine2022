package ks.commands.gm.command.executor;

import ks.model.map.L1WorldMap;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_SystemMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class L1Tile implements L1CommandExecutor {
    private static final Logger _log = LogManager.getLogger(L1Tile.class.getName());

    private L1Tile() {
    }

    public static L1CommandExecutor getInstance() {
        return new L1Tile();
    }

    public void execute(L1PcInstance pc, String cmdName, String arg) {
        try {
            int locX = pc.getX();
            int locY = pc.getY();
            short mapId = pc.getMapId();
            int tile0 = L1WorldMap.getInstance().getMap(mapId).getOriginalTile(
                    locX, locY - 1);
            int tile1 = L1WorldMap.getInstance().getMap(mapId).getOriginalTile(
                    locX + 1, locY - 1);
            int tile2 = L1WorldMap.getInstance().getMap(mapId).getOriginalTile(
                    locX + 1, locY);
            int tile3 = L1WorldMap.getInstance().getMap(mapId).getOriginalTile(
                    locX + 1, locY + 1);
            int tile4 = L1WorldMap.getInstance().getMap(mapId).getOriginalTile(
                    locX, locY + 1);
            int tile5 = L1WorldMap.getInstance().getMap(mapId).getOriginalTile(
                    locX - 1, locY + 1);
            int tile6 = L1WorldMap.getInstance().getMap(mapId).getOriginalTile(
                    locX - 1, locY);
            int tile7 = L1WorldMap.getInstance().getMap(mapId).getOriginalTile(
                    locX - 1, locY - 1);
            String msg = String.format(
                    "0:%d 1:%d 2:%d 3:%d 4:%d 5:%d 6:%d 7:%d", tile0, tile1,
                    tile2, tile3, tile4, tile5, tile6, tile7);
            pc.sendPackets(new S_SystemMessage(msg));
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }
}
