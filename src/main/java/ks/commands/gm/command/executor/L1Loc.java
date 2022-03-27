package ks.commands.gm.command.executor;

import ks.model.map.L1WorldMap;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_SystemMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class L1Loc implements L1CommandExecutor {
    private static final Logger _log = LogManager.getLogger(L1Loc.class.getName());

    private L1Loc() {
    }

    public static L1CommandExecutor getInstance() {
        return new L1Loc();
    }

    public void execute(L1PcInstance pc, String cmdName, String arg) {
        try {
            int locx = pc.getX();
            int locy = pc.getY();
            short mapid = pc.getMapId();
            int gab = L1WorldMap.getInstance().getMap(mapid).getOriginalTile(
                    locx, locy);

            String msg = String.format(
                    "위치 : X - %d, Y - %d, MAP - %d / Tile - %d", locx, locy,
                    mapid, gab);
            pc.sendPackets(new S_SystemMessage(msg));
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }
}
