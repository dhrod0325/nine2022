package ks.commands.gm.command.executor;

import ks.model.L1Teleport;
import ks.model.L1World;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_SystemMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class L1ToPC implements L1CommandExecutor {
    @SuppressWarnings("unused")
    private static final Logger _log = LogManager.getLogger(L1ToPC.class.getName());

    private L1ToPC() {
    }

    public static L1CommandExecutor getInstance() {
        return new L1ToPC();
    }

    public void execute(L1PcInstance pc, String cmdName, String charName) {
        try {
            L1PcInstance target = L1World.getInstance().getPlayer(charName);

            if (target != null) {
                L1Teleport.teleport(pc, target.getX(), target.getY(), target.getMapId(), 5, false);
            } else {
                pc.sendPackets(new S_SystemMessage((new StringBuilder()).append(charName).append(" : 해당 캐릭터는 없습니다. ").toString()));
            }
        } catch (Exception e) {
            pc.sendPackets(new S_SystemMessage(cmdName + " [캐릭터명]으로 입력해 주세요. "));
        }
    }
}
