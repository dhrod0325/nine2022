package ks.system.robot.ai;

import ks.model.L1Teleport;
import ks.model.instance.L1MonsterInstance;
import ks.system.robot.is.L1RobotInstance;
import ks.system.robot.model.L1RobotHuntLocation;
import ks.system.robot.model.L1RobotHuntLocationWay;

import java.util.Collections;
import java.util.List;

public class L1RobotWayHuntAi extends L1RobotNormalAi {
    private final List<L1RobotHuntLocationWay> huntWayList;

    private int giveUpCount = 0;

    private int currentNextLocationWayIdx = 0;

    public L1RobotWayHuntAi(L1RobotInstance robot) {
        super(robot);

        L1RobotHuntLocation huntLocation = robot.getTpl().getHuntLocation();
        this.huntWayList = huntLocation.getWayList();

        setAiMaxTryCount(200);
    }

    @Override
    public void toAiWalk() {
        super.toAiWalk();

        if (getNextLocationWay().isEqualsLocation(robot.getX(), robot.getY(), robot.getMapId())) {
            currentNextLocationWayIdx++;
        }

        toMove(getNextLocationWay().getLocX(), getNextLocationWay().getLocY());
    }

    @Override
    public void giveUp() {
        super.giveUp();

        if (giveUpCount > 5) {
            if (getAttackTarget() instanceof L1MonsterInstance) {
                ((L1MonsterInstance) getAttackTarget()).deleteMe();
            }

            giveUpCount = 0;
        }

        L1Teleport.teleport(robot, getNextLocationWay().getLocX(), getNextLocationWay().getLocY(), robot.getMapId(), robot.getHeading(), false);

        giveUpCount++;
    }

    @Override
    public void teleport() {
    }

    public L1RobotHuntLocationWay getNextLocationWay() {
        if (currentNextLocationWayIdx >= huntWayList.size()) {
            currentNextLocationWayIdx = 0;
            Collections.reverse(huntWayList);
        }

        return huntWayList.get(currentNextLocationWayIdx);
    }
}
