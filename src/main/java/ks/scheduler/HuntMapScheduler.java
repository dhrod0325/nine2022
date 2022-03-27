package ks.scheduler;

import ks.app.LineageAppContext;
import ks.app.config.prop.CodeConfig;
import ks.core.datatables.MapsTable;
import ks.model.L1World;
import ks.packets.serverpackets.S_SystemMessage;
import ks.system.robot.is.L1RobotInstance;
import ks.util.L1TeleportUtils;
import ks.util.log.L1LogUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class HuntMapScheduler {
    private static final Logger logger = LogManager.getLogger(HuntMapScheduler.class);

    @Scheduled(fixedDelay = 2000)
    public void run() {
        if (!LineageAppContext.isRun()) {
            return;
        }

        L1World.getInstance().getAllPlayers().forEach(pc -> {
            try {
                if (pc instanceof L1RobotInstance)
                    return;

                if (pc.isDead()) {
                    return;
                }

                if (CodeConfig.isHuntMap(pc.getMapId()) && pc.getHuntCount() == 0) {
                    if (!pc.isAdmin()) {
                        L1LogUtils.debugLog("수배 못걸고 사냥터 이동한 사람 : " + pc.getName());
                        pc.sendPackets(new S_SystemMessage("수배를 해야 이용 가능한 사냥터입니다."));
                        L1TeleportUtils.teleportToGiran(pc);
                        return;
                    }
                }

                MapsTable.MapData mapData = MapsTable.getInstance().getMapData(pc.getMapId());

                if (pc.getLevel() < mapData.minLev) {
                    pc.sendPackets(new S_SystemMessage(mapData.mapName + "의 최소 이용레벨은 " + mapData.minLev + " 입니다"));
                    L1TeleportUtils.teleportToGiran(pc);
                    return;
                }

                if (pc.getLevel() > mapData.maxLev && mapData.maxLev > 0) {
                    pc.sendPackets(new S_SystemMessage(mapData.mapName + "의 최대 이용레벨은 " + mapData.maxLev + " 입니다"));
                    L1TeleportUtils.teleportToGiran(pc);
                }
            } catch (Exception e) {
                logger.error(e);
            }
        });
    }
}
