package ks.commands.gm.command.executor;

import ks.commands.gm.command.L1Commands;
import ks.model.L1Command;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_SystemMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

@SuppressWarnings("unused")
public class L1CommandHelp implements L1CommandExecutor {
    @SuppressWarnings("unused")
    private static final Logger _log = LogManager.getLogger(L1CommandHelp.class.getName());

    private L1CommandHelp() {
    }

    public static L1CommandExecutor getInstance() {
        return new L1CommandHelp();
    }

    private String join(List<L1Command> list) {
        StringBuilder result = new StringBuilder();
        for (L1Command cmd : list) {
            if (result.length() > 0) {
                result.append(", ");
            }
            result.append(cmd.getName());
        }
        return result.toString();
    }

    public void execute(L1PcInstance pc, String cmdName, String arg) {
        List<L1Command> list = L1Commands.availableCommandList(pc.getAccessLevel());
        pc.sendPackets(new S_SystemMessage(join(list)));
    }
}
