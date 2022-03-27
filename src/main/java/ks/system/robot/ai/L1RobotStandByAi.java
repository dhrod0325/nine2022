package ks.system.robot.ai;

import ks.system.robot.is.L1RobotInstance;
import ks.util.L1CharPosUtils;
import ks.util.common.random.RandomUtils;

public class L1RobotStandByAi extends L1RobotRandomMoveAi {
    public L1RobotStandByAi(L1RobotInstance robot) {
        super(robot);

        noUseResetAiCount();
    }

    @Override
    public void executeBuff() {
    }

    @Override
    public void toSearchTarget() {
        int randomX = RandomUtils.nextInt(-1, 1);
        int randomY = RandomUtils.nextInt(-1, 1);

        int newMoveX = robot.getX() + randomX;
        int newMoveY = robot.getY() + randomY;

        int dir = L1CharPosUtils.calcMoveDirection(robot, newMoveX, newMoveY);

        if (dir != -1) {
            L1CharPosUtils.setDirectionMove(robot, dir);
            robot.setAiSleepTime(1000);
        }
    }

    @Override
    public void giveUp() {
    }

    @Override
    public void teleport() {
    }
}
