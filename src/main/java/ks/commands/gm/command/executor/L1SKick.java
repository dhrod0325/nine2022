package ks.commands.gm.command.executor;

import ks.core.datatables.account.AccountTable;
import ks.model.L1World;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_SystemMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class L1SKick implements L1CommandExecutor {
    @SuppressWarnings("unused")
    private static final Logger _log = LogManager.getLogger(L1SKick.class.getName());

    private L1SKick() {
    }

    public static L1CommandExecutor getInstance() {
        return new L1SKick();
    }

    public void execute(L1PcInstance pc, String cmdName, String arg) {
        try {
            L1PcInstance target = L1World.getInstance().getPlayer(arg);
            if (target != null) {
                pc.sendPackets(new S_SystemMessage((new StringBuilder()).append(target.getName()).append(" 를 강력추방 했습니다. ").toString()));
                AccountTable.getInstance().ban(target.getAccountName());
                L1World.getInstance().removeObject(target);
                target.disconnect();
            } else {
                pc.sendPackets(new S_SystemMessage("그러한 이름의 캐릭터는 월드내에는 존재하지 않습니다. "));
            }
        } catch (Exception e) {
            pc.sendPackets(new S_SystemMessage(cmdName + " [캐릭터명]으로 입력해 주세요. "));
        }
    }
}
