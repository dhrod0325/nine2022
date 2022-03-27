package ks.commands.gm.command.executor;

import ks.core.GameServer;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_SystemMessage;

public class L1Shutdown implements L1CommandExecutor {
    private L1Shutdown() {
    }

    public static L1CommandExecutor getInstance() {
        return new L1Shutdown();
    }

    public void execute(L1PcInstance pc, String cmdName, String arg) {
        try {
            if (arg.equalsIgnoreCase("지금")) {
                GameServer.getInstance().shutdown();
                return;
            }

            if (arg.equalsIgnoreCase("취소")) {
                GameServer.getInstance().abortShutdown();
                return;
            }

            int sec = Math.max(5, Integer.parseInt(arg));
            GameServer.getInstance().shutdownWithCountdown(sec);
        } catch (Exception e) {
            pc.sendPackets(new S_SystemMessage(".shutdown [종료대기초,지금,취소] 라고 입력해 주세요. "));
        }
    }
}