package ks.system.robot.ai;

import ks.model.Broadcaster;
import ks.model.L1World;
import ks.packets.serverpackets.S_MoveCharPacket;
import ks.system.robot.is.L1RobotInstance;
import ks.util.L1CharPosUtils;
import ks.util.common.random.RandomUtils;

public class L1RobotRandomMoveAi extends L1RobotNormalAi {
    int moveDir = 0;
    private int maxMoveCount = 5;

    private int moveCount = 0;

    public L1RobotRandomMoveAi(L1RobotInstance robot) {
        super(robot);
    }

    @Override
    public void toAiWalk() {
        super.toAiWalk();

        if (moveCount % 7 == 0) {
            moveDir = RandomUtils.nextInt(0, 7);
        }

        int dir = L1CharPosUtils.checkObject(robot.getX(), robot.getY(), robot.getMapId(), moveDir);

        if (dir != -1) {
            toRandomMoving(dir);
        }
    }

    public void toRandomMoving(int dir) {
        if (L1World.getInstance().getVisibleMonsters(robot, 10).isEmpty()) {
            giveUpAndTeleport();
            return;
        }

        if (dir >= 0) {
            int nx = HEADING_TABLE_X[dir];
            int ny = HEADING_TABLE_Y[dir];

            robot.setHeading(dir);
            robot.getMap().setPassable(robot.getLocation(), true);

            int nnx = robot.getX() + nx;
            int nny = robot.getY() + ny;

            robot.setX(nnx);
            robot.setY(nny);

            robot.getMap().setPassable(robot.getLocation(), false);
            Broadcaster.broadcastPacket(robot, new S_MoveCharPacket(robot));

            if (maxMoveCount > 0) {
                if (moveCount > maxMoveCount) {
                    giveUpAndTeleport();
                    moveCount = 0;
                } else {
                    moveCount++;
                }
            }

            aiTime = getFrame(robot.getGfxId().getGfxId(), 0);
        }
    }

    public void setMaxMoveCount(int maxMoveCount) {
        this.maxMoveCount = maxMoveCount;
    }

    @Override
    public void noUseResetAiCount() {
        super.noUseResetAiCount();
        setMaxMoveCount(-1);
    }
}
