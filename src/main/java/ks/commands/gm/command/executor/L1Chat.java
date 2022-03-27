package ks.commands.gm.command.executor;

import ks.model.L1World;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_SystemMessage;

import java.util.StringTokenizer;

@SuppressWarnings("unused")
public class L1Chat implements L1CommandExecutor {
    public static L1CommandExecutor getInstance() {
        return new L1Chat();
    }

    public void execute(L1PcInstance pc, String cmdName, String arg) {
        try {
            StringTokenizer st = new StringTokenizer(arg);
            if (st.hasMoreTokens()) {
                String flag = st.nextToken();
                String msg;
                if (flag.compareToIgnoreCase("켬") == 0) {
                    L1World.getInstance().setWorldChatEnable(true);
                    msg = "월드 채팅을 유효하게 했습니다. ";
                } else if (flag.compareToIgnoreCase("끔") == 0) {
                    L1World.getInstance().setWorldChatEnable(false);
                    msg = "월드 채팅을 정지했습니다. ";
                } else {
                    throw new Exception();
                }

                pc.sendPackets(new S_SystemMessage(msg));
            } else {
                String msg;

                if (L1World.getInstance().isWorldChatEnable()) {
                    msg = "현재 월드 채팅은 유효합니다.. 채팅 끔 로 정지할 수 있습니다. ";
                } else {
                    msg = "현재 월드 채팅은 정지하고 있습니다.. 채팅 켬 로 유효하게 할 수 있습니다. ";
                }

                pc.sendPackets(new S_SystemMessage(msg));
            }
        } catch (Exception e) {
            pc.sendPackets(new S_SystemMessage(cmdName + " [켬, 끔]"));
        }
    }
}
