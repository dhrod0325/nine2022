package ks.commands.gm.command.executor;

import ks.model.L1Party;
import ks.model.L1Teleport;
import ks.model.L1World;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_SystemMessage;

import java.util.List;

public class L1PartyRecall implements L1CommandExecutor {
    public static L1CommandExecutor getInstance() {
        return new L1PartyRecall();
    }

    public void execute(L1PcInstance pc, String cmdName, String arg) {
        L1PcInstance target = L1World.getInstance().getPlayer(arg);

        if (target != null) {
            L1Party party = target.getParty();

            if (party != null) {
                int x = pc.getX();
                int y = pc.getY() + 2;
                short map = pc.getMapId();

                List<L1PcInstance> players = party.getMembers();

                for (L1PcInstance pc2 : players) {
                    if (pc2.isDead()) {
                        pc.sendPackets("대상이 죽어있습니다");
                        return;
                    }

                    L1Teleport.teleport(pc2, x, y, map, 5, true);
                    pc2.sendPackets(new S_SystemMessage("게임 마스터에 소환되었습니다. "));
                }
            } else {
                pc.sendPackets(new S_SystemMessage("파티 멤버가 아닙니다. "));
            }
        } else {
            pc.sendPackets(new S_SystemMessage("그러한 캐릭터는 없습니다. "));
        }
    }
}
