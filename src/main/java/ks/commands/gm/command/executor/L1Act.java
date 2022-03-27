package ks.commands.gm.command.executor;

import ks.model.Broadcaster;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_AttackPacketForNpc;
import ks.packets.serverpackets.S_SystemMessage;

import java.util.StringTokenizer;

@SuppressWarnings("unused")
public class L1Act implements L1CommandExecutor {
    private L1Act() {
    }

    public static L1CommandExecutor getInstance() {
        return new L1Act();
    }

    public void execute(L1PcInstance pc, String cmdName, String arg) {
        try {
            StringTokenizer st = new StringTokenizer(arg);
            int actId = Integer.parseInt(st.nextToken(), 10);
            pc.sendPackets(new S_AttackPacketForNpc(pc, pc.getId(), actId));
            Broadcaster.broadcastPacket(pc, new S_AttackPacketForNpc(pc, pc.getId(), actId));
        } catch (Exception exception) {
            pc.sendPackets(new S_SystemMessage(cmdName + " [actid] 라고 입력해 주세요. "));
        }
    }
}
