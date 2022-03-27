package ks.commands.gm.command.executor;

import ks.model.L1Teleport;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_SystemMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.StringTokenizer;

public class L1Move implements L1CommandExecutor {
    @SuppressWarnings("unused")
    private static final Logger _log = LogManager.getLogger(L1Move.class.getName());

    private L1Move() {
    }

    public static L1CommandExecutor getInstance() {
        return new L1Move();
    }

    public void execute(L1PcInstance pc, String cmdName, String arg) {
        try {
            StringTokenizer st = new StringTokenizer(arg);
            int locx = Integer.parseInt(st.nextToken());
            int locy = Integer.parseInt(st.nextToken());
            short mapid;
            if (st.hasMoreTokens()) {
                mapid = Short.parseShort(st.nextToken());
            } else {
                mapid = pc.getMapId();
            }
            L1Teleport.teleport(pc, locx, locy, mapid, 5, false);
            pc.sendPackets(new S_SystemMessage("좌표 " + locx + ", " + locy
                    + ", " + mapid + "로 이동 했습니다. "));
        } catch (Exception e) {
            pc.sendPackets(new S_SystemMessage(cmdName
                    + " [X좌표] [Y좌표] [맵ID] 라고 입력해 주세요. "));
        }
    }
}
