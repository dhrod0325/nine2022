package ks.commands.gm.command.executor;

import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_DoActionGFX;
import ks.packets.serverpackets.S_SystemMessage;

import java.util.StringTokenizer;

@SuppressWarnings("unused")
public class L1Action implements L1CommandExecutor {
    private L1Action() {
    }

    public static L1CommandExecutor getInstance() {
        return new L1Action();
    }

    public void execute(L1PcInstance pc, String cmdName, String arg) {
        try {
            StringTokenizer st = new StringTokenizer(arg);
            int actId = Integer.parseInt(st.nextToken(), 10);
            pc.sendPackets(new S_DoActionGFX(pc.getId(), actId));
        } catch (Exception exception) {
            pc.sendPackets(new S_SystemMessage(cmdName + " [actid] 라고 입력해 주세요. "));
        }
    }
}
