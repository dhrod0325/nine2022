package ks.commands.gm.command.executor;

import ks.core.datatables.account.AccountTable;
import ks.model.L1World;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_SystemMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SuppressWarnings("unused")
public class L1AccountBanKick implements L1CommandExecutor {
    private static final Logger logger = LogManager.getLogger();

    private L1AccountBanKick() {
    }

    public static L1CommandExecutor getInstance() {
        return new L1AccountBanKick();
    }

    public void execute(L1PcInstance pc, String cmdName, String arg) {
        try {
            L1PcInstance target = L1World.getInstance().getPlayer(arg);

            if (target != null) {
                AccountTable.getInstance().ban(target.getAccountName());
                L1World.getInstance().broadcastServerMessage("게임에 적합하지 않는 행동이기 때문에 " + target.getName() + "의 계정을 압류 하였습니다.");

                target.disconnect(target.getName() + " 벤처리");
            } else {
                pc.sendPackets(new S_SystemMessage("그러한 이름의 캐릭터는 월드내에는 존재하지 않습니다. "));
            }
        } catch (Exception e) {
            pc.sendPackets(new S_SystemMessage(cmdName + " [캐릭터명] 으로 입력해 주세요. "));
        }
    }
}
