package ks.commands.gm.command.executor;

import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_SystemMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.StringTokenizer;

public class L1PacketBox2 implements L1CommandExecutor {
    @SuppressWarnings("unused")
    private static final Logger _log = LogManager.getLogger(L1PacketBox2.class.getName());

    private L1PacketBox2() {
    }

    public static L1CommandExecutor getInstance() {
        return new L1PacketBox2();
    }

    @Override
    public void execute(L1PcInstance pc, String cmdName, String arg) {
        try {
            StringTokenizer st = new StringTokenizer(arg);

            int start = Integer.parseInt(st.nextToken());
            int count = Integer.parseInt(st.nextToken());
            int end = start + count;
        } catch (Exception exception) {
            pc.sendPackets(new S_SystemMessage("[Command] .패킷박스2 [id] [type] 입력"));
        }
    }
}
