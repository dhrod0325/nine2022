package ks.commands.gm.command.executor;

import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_SystemMessage;


@SuppressWarnings("unused")
public class L1GM implements L1CommandExecutor {
    private L1GM() {
    }

    public static L1CommandExecutor getInstance() {
        return new L1GM();
    }

    public void execute(L1PcInstance pc, String cmdName, String arg) {
        pc.setGm(!pc.isGm());
        pc.sendPackets(new S_SystemMessage("GM 세팅 = " + pc.isGm()));
    }
}
