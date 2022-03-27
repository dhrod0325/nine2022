package ks.commands.gm.command.executor;

import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_SystemMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.StringTokenizer;

public class L1PacketBox implements L1CommandExecutor {
    private static final Logger _log = LogManager.getLogger(L1PacketBox.class.getName());

    public static L1CommandExecutor getInstance() {
        return new L1PacketBox();
    }

    @Override
    public void execute(L1PcInstance pc, String cmdName, String arg) {
//        String clanName = pc.getClanName();
//        L1Clan clan = L1World.getInstance().getClan(clanName);
//		try {
//			StringTokenizer st = new StringTokenizer(arg);
//			int id = Integer.parseInt(st.nextToken(), 10);
//			pc.sendPackets(new S_PacketBox(id));
//		} catch (Exception exception) {
//			pc.sendPackets(new S_SystemMessage("[Command] .패킷박스 [id] 입력"));
//		}
        try {
            StringTokenizer st = new StringTokenizer(arg);
            int subcode = 180;
//            Integer.parseInt(st.nextToken());
            int start = Integer.parseInt(st.nextToken());
            int count = Integer.parseInt(st.nextToken());

            int end = start + count;

        } catch (Exception exception) {
            pc.sendPackets(new S_SystemMessage("[Command] .패킷박스 [id] [type] 입력"));
        }
    }
}
