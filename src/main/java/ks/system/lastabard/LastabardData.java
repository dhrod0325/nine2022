package ks.system.lastabard;

import ks.core.datatables.getback.GetBackTable;
import ks.model.L1Teleport;
import ks.model.L1World;
import ks.model.pc.L1PcInstance;
import ks.util.L1TeleportUtils;

import java.util.Collection;

public class LastabardData {
    public static int getDelayTime(int mapId) {
        int delayTime;

        switch (mapId) {
            // 30분
            case 452:
            case 454:
            case 455:
            case 456:
            case 471:
            case 472:
            case 475:
            case 476:
            case 477:
            case 478:
            case 492:
            case 495:
            case 531:
            case 461:
            case 465:
            case 490:
            case 453:
            case 462:
            case 463:
            case 473:
            case 533:
                delayTime = 1800;
                break;
            case 466:
            case 474:
            case 493:
            case 494:
            case 496:
                delayTime = 2400; // 60 * 35
                break;
            case 530:
            case 532:
            case 534:
                delayTime = 300; // 60 * 5
                break;
            default:
                delayTime = 1100;
        }

        return delayTime;
    }

    public static void doHomeTeleport(int mapId) {
        if (mapId == 0)
            return;

        Collection<L1PcInstance> players = L1World.getInstance().getAllPlayers();

        for (L1PcInstance pc : players) {
            if (pc.getMapId() != mapId)
                continue;

            if (pc.getMapId() == 534) {
                L1TeleportUtils.teleportToGiran(pc);
            } else {
                int[] loc = GetBackTable.getInstance().getBackLocation(pc);
                L1Teleport.teleport(pc, loc[0], loc[1], (short) loc[2], 5, true);
            }
        }
    }

    public static int relatedTime(int mapId) {
        if (mapId == 531)
            return 530;

        if (mapId == 533)
            return 532;

        return 0;
    }
}
