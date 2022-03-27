package ks.commands.gm.command.executor;

import ks.core.datatables.IpTable;
import ks.core.datatables.account.AccountTable;
import ks.core.datatables.blacklist.BlackList;
import ks.core.datatables.blacklist.BlackListTable;
import ks.model.L1World;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_SystemMessage;
import ks.system.adenBoard.database.AdenBankAccountTable;
import ks.system.adenBoard.model.AdenBankAccount;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class L1PowerKick implements L1CommandExecutor {
    private final Logger logger = LogManager.getLogger();

    private L1PowerKick() {
    }

    public static L1CommandExecutor getInstance() {
        return new L1PowerKick();
    }

    public void execute(L1PcInstance pc, String cmdName, String arg) {
        try {
            L1PcInstance target = L1World.getInstance().getPlayer(arg);

            IpTable iptable = IpTable.getInstance();

            if (target != null) {
                AccountTable.getInstance().ban(target.getAccountName());
                iptable.insert(target.getClient().getIp());

                BlackList vo = new BlackList();
                vo.setIp(target.getAccount().getIp());
                vo.setAccountName(target.getAccountName());

                AdenBankAccount bankAccount = AdenBankAccountTable.getInstance().getBankAccount(target.getAccountName());

                if (bankAccount != null) {
                    vo.setPhone(bankAccount.getPhone());
                    vo.setBankNo(bankAccount.getBank_no());
                    vo.setBankName(bankAccount.getBank_name());
                    vo.setBankOwner(bankAccount.getBank_owner_name());
                }

                vo.setReason("영구추방");

                BlackListTable.getInstance().insert(vo);

                pc.sendPackets(new S_SystemMessage("[영구추방] : " + target.getName()));

                target.getClient().disconnectNow();
            } else {
                pc.sendPackets(new S_SystemMessage("그러한 이름의 캐릭터는 월드내에는 존재하지 않습니다. "));
            }
        } catch (Exception e) {
            logger.error("오류", e);
            pc.sendPackets(new S_SystemMessage(cmdName + " [캐릭터명]으로 입력해 주세요. "));
        }
    }
}
