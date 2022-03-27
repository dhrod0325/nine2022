package ks.commands.gm.command.executor;

import ks.model.attack.utils.L1MagicUtils;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_SystemMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class L1Invisible implements L1CommandExecutor {
    @SuppressWarnings("unused")
    private static final Logger _log = LogManager.getLogger(L1Invisible.class.getName());

    private L1Invisible() {
    }

    public static L1CommandExecutor getInstance() {
        return new L1Invisible();
    }

    public void execute(L1PcInstance pc, String cmdName, String arg) {
        try {
            if (!pc.isGmInvis()) {
                pc.setGmInvis(true);

                L1MagicUtils.startInvisible(pc);

                pc.sendPackets("투명상태가 되었습니다");
            } else {
                pc.setGmInvis(false);
                L1MagicUtils.stopInvisible(pc);

                pc.sendPackets("투명상태를 해제했습니다");
            }

        } catch (Exception e) {
            pc.sendPackets(new S_SystemMessage(cmdName + " 커멘드 에러"));
        }
    }
}
