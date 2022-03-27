package ks.commands.gm.command.executor;

import ks.core.datatables.IpTable;
import ks.model.L1World;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_SystemMessage;

import java.util.Collection;
import java.util.StringTokenizer;

@SuppressWarnings("unused")
public class L1BanIp implements L1CommandExecutor {
    private L1BanIp() {
    }

    public static L1CommandExecutor getInstance() {
        return new L1BanIp();
    }

    public void execute(L1PcInstance pc, String cmdName, String arg) {
        try {
            StringTokenizer stringtokenizer = new StringTokenizer(arg);
            String s1 = stringtokenizer.nextToken();
            String s2 = null;
            try {
                s2 = stringtokenizer.nextToken();
            } catch (Exception e) {
            }

            IpTable iptable = IpTable.getInstance();
            boolean isBanned = iptable.isBanned(s1);

            Collection<L1PcInstance> players = L1World.getInstance().getAllPlayers();

            for (L1PcInstance tg : players) {
                if (s1.equals(tg.getClient().getIp())) {
                    String msg = "IP:" + s1 + " 로 접속중의 플레이어:" + tg.getName();
                    pc.sendPackets(new S_SystemMessage(msg));
                }
            }

            if ("add".equals(s2) && !isBanned) {
                iptable.insert(s1);
                String msg = "IP:" + s1 + " 를 BAN IP에 등록했습니다. ";
                pc.sendPackets(new S_SystemMessage(msg));
            } else if ("del".equals(s2) && isBanned) {
                if (iptable.delete(s1)) {
                    String msg = "IP:" + s1 + " 를 BAN IP로부터 삭제했습니다. ";
                    pc.sendPackets(new S_SystemMessage(msg));
                }
            } else {
                if (isBanned) {
                    String msg = "IP:" + s1 + " 는 BAN IP에 등록되어 있습니다. ";
                    pc.sendPackets(new S_SystemMessage(msg));
                } else {
                    String msg = "IP:" + s1 + " 는 BAN IP에 등록되어 있지 않습니다. ";
                    pc.sendPackets(new S_SystemMessage(msg));
                }
            }
        } catch (Exception e) {
            pc.sendPackets(new S_SystemMessage(cmdName + " IP [ add | del ]라고 입력해 주세요. "));
        }
    }
}