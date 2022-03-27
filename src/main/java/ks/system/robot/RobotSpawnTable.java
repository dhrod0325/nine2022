package ks.system.robot;

import ks.app.LineageAppContext;
import ks.app.aop.LogTime;
import ks.model.L1World;
import ks.system.robot.is.L1RobotInstance;
import ks.system.robot.model.L1RobotSpawn;
import ks.util.common.SqlUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class RobotSpawnTable {
    private final List<L1RobotSpawn> list = new ArrayList<>();

    public static RobotSpawnTable getInstance() {
        return LineageAppContext.getBean(RobotSpawnTable.class);
    }

    @LogTime
    public void load() {
        list.clear();
        list.addAll(selectList());

        doSpawn();
    }

    public List<L1RobotSpawn> selectList() {
        String sql = "SELECT * FROM robot_spawn";
        return SqlUtils.query(sql, new BeanPropertyRowMapper<>(L1RobotSpawn.class));
    }

    public void doSpawn() {
        for (L1RobotSpawn s : list) {
            for (L1RobotInstance o : L1World.getInstance().getRobotPlayers()) {
                if (s.getRobotName().equalsIgnoreCase(o.getName())) {
                    o.logout();
                }
            }
        }

        for (L1RobotSpawn s : list) {
            String name = s.getRobotName();
            L1RobotInstance robot = L1RobotTable.getInstance().createRobot(name);

            if (robot != null) {
                robot.setRobotType(L1RobotType.JOMBI);

                robot.setX(s.getLocx());
                robot.setY(s.getLocy());
                robot.setMap((short) s.getMapid());
                robot.setHeading(s.getHeading());

                L1World.getInstance().storeObject(robot);
                L1World.getInstance().addVisibleObject(robot);

            }
        }
    }

    public List<L1RobotSpawn> getList() {
        return list;
    }
}
