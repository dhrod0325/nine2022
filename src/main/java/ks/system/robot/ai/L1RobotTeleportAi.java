package ks.system.robot.ai;

import ks.app.LineageAppContext;
import ks.model.L1World;
import ks.model.instance.L1MonsterInstance;
import ks.system.robot.is.L1RobotInstance;

import java.time.Instant;
import java.util.List;

public class L1RobotTeleportAi extends L1RobotNormalAi {
    public L1RobotTeleportAi(L1RobotInstance robot) {
        super(robot);
    }

    @Override
    public void toSearchTarget() {
        super.toSearchTarget();

        LineageAppContext.commonTaskScheduler().schedule(() -> {
            List<L1MonsterInstance> visibleObjects = L1World.getInstance().getVisibleMonsters(robot, 10);

            if (visibleObjects.isEmpty() || hateList.isEmpty()) {
                giveUpAndTeleport();
            }
        }, Instant.now().plusMillis(2000));
    }
}
