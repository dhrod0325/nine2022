package ks.commands.gm.command.executor;

import ks.model.L1World;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_SystemMessage;
import ks.util.L1TeleportUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collection;

public class L1Recall implements L1CommandExecutor {
    @SuppressWarnings("unused")
    private static final Logger _log = LogManager.getLogger(L1Recall.class.getName());

    private L1Recall() {
    }

    public static L1CommandExecutor getInstance() {
        return new L1Recall();
    }

    public void execute(L1PcInstance pc, String cmdName, String arg) {
        try {
            Collection<L1PcInstance> targets;
            if (arg.equalsIgnoreCase("전체")) {
                targets = L1World.getInstance().getAllPlayers();
            } else {
                targets = new ArrayList<>();
                L1PcInstance tg = L1World.getInstance().getPlayer(arg);

                if (tg == null) {
                    pc.sendPackets(new S_SystemMessage("그러한 캐릭터는 없습니다. "));
                    return;
                }
                targets.add(tg);
            }

            for (L1PcInstance target : targets) {
                if (!pc.isGm()) {
                    pc.sendPackets(new S_SystemMessage("상점 캐릭은 소환할수 없습니다."));
                    return;
                }

                if (target.isDead()) {
                    pc.sendPackets("대상이 죽어있습니다");
                    return;
                }

                boolean success = L1TeleportUtils.teleportToTargetFront(target, pc, 2);

                if (success) {
                    pc.sendPackets(new S_SystemMessage(target.getName() + " 를 소환했습니다. "));
                    target.sendPackets(new S_SystemMessage("게임 마스터에 소환되었습니다. "));
                } else {
                    pc.sendPackets("헤딩 위치 교체 또는 자리 이동이 필요합니다");
                }
            }
        } catch (Exception e) {
            pc.sendPackets(new S_SystemMessage(cmdName + " [전체, 캐릭터명]으로 입력해 주세요. "));
        }
    }
}
