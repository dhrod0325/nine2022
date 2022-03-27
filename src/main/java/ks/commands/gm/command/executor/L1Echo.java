package ks.commands.gm.command.executor;

import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_SystemMessage;

@SuppressWarnings("unused")
public class L1Echo implements L1CommandExecutor {
    private L1Echo() {
    }

    public static L1CommandExecutor getInstance() {
        return new L1Echo();
    }

    public void execute(L1PcInstance pc, String cmdName, String arg) {
        pc.sendPackets(new S_SystemMessage(arg));
    }
}
