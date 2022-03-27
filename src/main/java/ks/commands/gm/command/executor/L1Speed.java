package ks.commands.gm.command.executor;

import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_SystemMessage;
import ks.util.L1StatusUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class L1Speed implements L1CommandExecutor {
    @SuppressWarnings("unused")
    private static final Logger _log = LogManager.getLogger(L1Speed.class.getName());

    private L1Speed() {
    }

    public static L1CommandExecutor getInstance() {
        return new L1Speed();
    }

    public void execute(L1PcInstance pc, String cmdName, String arg) {
        try {
            L1StatusUtils.haste(pc, 3600 * 1000);
            L1StatusUtils.brave(pc, 3600 * 1000);
        } catch (Exception e) {
            pc.sendPackets(new S_SystemMessage(".속도업 커멘드 에러"));
        }
    }
}
