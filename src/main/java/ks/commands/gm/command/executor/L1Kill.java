package ks.commands.gm.command.executor;

import ks.model.L1World;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_SystemMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class L1Kill implements L1CommandExecutor {
    @SuppressWarnings("unused")
    private static final Logger _log = LogManager.getLogger(L1Kill.class.getName());

    private L1Kill() {
    }

    public static L1CommandExecutor getInstance() {
        return new L1Kill();
    }

    public void execute(L1PcInstance pc, String cmdName, String arg) {
        try {
            L1PcInstance target = L1World.getInstance().getPlayer(arg);

            if (target != null) {
                target.setCurrentHp(0);
                target.death(null);
            }
        } catch (Exception e) {
            pc.sendPackets(new S_SystemMessage(cmdName + " [캐릭터명]으로 입력해 주세요. "));
        }
    }
}
