package ks.system.robot.ai;

import ks.system.robot.is.L1RobotInstance;

public class L1RobotAiFactory {
    public static L1RobotAi createAi(L1RobotInstance robot) {
        if (robot == null)
            return null;

        L1RobotAi ai = null;

        if (robot.getTpl() != null && !robot.getTpl().getHuntLocation().getWayList().isEmpty()) {
            ai = new L1RobotWayHuntAi(robot);
        } else {
            switch (robot.getRobotType()) {
                case JOMBI:
                    break;
                case STAND_BY:
                    ai = new L1RobotStandByAi(robot);
                    break;
                case TELEPORT:
                    ai = new L1RobotTeleportAi(robot);
                    break;
                case HUNT:
                    ai = new L1RobotTeleportAi(robot);
                    break;
                default:
                    ai = new L1RobotRandomMoveAi(robot);
                    break;
            }
        }

        return ai;
    }
}
