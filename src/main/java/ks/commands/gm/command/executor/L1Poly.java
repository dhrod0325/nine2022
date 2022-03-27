package ks.commands.gm.command.executor;

import ks.model.L1PolyMorph;
import ks.model.L1World;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_ServerMessage;
import ks.packets.serverpackets.S_SystemMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.StringTokenizer;

public class L1Poly implements L1CommandExecutor {
    private static final Logger _log = LogManager.getLogger(L1Poly.class.getName());

    public L1Poly() {
    }

    public static L1CommandExecutor getInstance() {
        return new L1Poly();
    }

    public void execute(L1PcInstance pc, String cmdName, String arg) {
        try {
            StringTokenizer st = new StringTokenizer(arg);
            String name = st.nextToken();
            int polyid = Integer.parseInt(st.nextToken());

            L1PcInstance tg = L1World.getInstance().getPlayer(name);

            if (tg == null) {
                pc.sendPackets(new S_ServerMessage(73, name)); // \f1%0은 게임을 하고
            } else {
                try {
                    L1PolyMorph.doPoly(tg, polyid, 7200, L1PolyMorph.MORPH_BY_GM);
                } catch (Exception exception) {
                    pc.sendPackets(new S_SystemMessage(".변신 [캐릭터명] [그래픽ID] 라고 입력해 주세요. "));
                }
            }
        } catch (Exception e) {
            pc.sendPackets(new S_SystemMessage(cmdName + " [캐릭터명] [그래픽ID] 라고 입력해 주세요. "));
        }
    }
}
