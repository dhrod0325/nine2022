package ks.commands.gm.command.executor;

import ks.model.L1World;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_Message_YN;
import ks.packets.serverpackets.S_ServerMessage;
import ks.packets.serverpackets.S_SystemMessage;


@SuppressWarnings("unused")
public class L1MultiTrade implements L1CommandExecutor {
    public static L1CommandExecutor getInstance() {
        return new L1MultiTrade();
    }

    private static boolean checkPc(L1PcInstance pc, String arg) {
        L1PcInstance target = L1World.getInstance().getPlayer(arg);

        if (pc.getOnlineStatus() == 0)
            return true;

        if (pc.getOnlineStatus() != 1)
            return true;

        if (!pc.isGm() && pc.isInvisible()) {
            pc.sendPackets(new S_ServerMessage(334));
            return true;
        }

        if (pc.getAccountName().equalsIgnoreCase(target.getAccountName())) {
            return true;
        }

        if (pc.getId() == target.getId()) {
            return true;
        } else if (pc.getId() != target.getId()) {
            return pc.getAccountName().equalsIgnoreCase(target.getAccountName());
        }

        return false;
    }

    @Override
    public void execute(L1PcInstance pc, String cmdName, String arg) {
        L1PcInstance target = L1World.getInstance().getPlayer(arg);
        try {
            if (checkPc(pc, arg))
                return;

            if (target != null) {
                if (!target.isParalyzed()) {
                    pc.setTradeID(target.getId());
                    target.setTradeID(pc.getId());
                    target.sendPackets(new S_Message_YN(252, pc.getName()));
                    pc.sendPackets(new S_SystemMessage("" + target.getName() + " 님에게 원격교환을 요청하였습니다."));
                }
            } else {
                pc.sendPackets(new S_SystemMessage("상대방이 접속중이 아닙니다. 다시 확인 바랍니다."));
            }
        } catch (Exception e) {
            pc.sendPackets(new S_SystemMessage(".원격교환 캐릭터명 으로 입력해 주세요."));
        }
    }
}
