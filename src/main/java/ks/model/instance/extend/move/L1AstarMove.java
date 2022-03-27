package ks.model.instance.extend.move;

import ks.app.config.prop.CodeConfig;
import ks.model.L1AStar;
import ks.model.L1Character;
import ks.model.L1World;
import ks.model.pc.L1PcInstance;
import ks.util.L1CharPosUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class L1AstarMove implements L1Move {
    private final L1AStar aStar = new L1AStar();
    private final Map<Integer, Integer> astarCheckMap = new HashMap<>();

    private final Logger logger = LogManager.getLogger();

    @Override
    public void targetRemove(L1Character target) {
        if (target != null) {
            astarCheckMap.remove(target.getId());
        }
    }

    @Override
    public void targetInit(L1Character attacker, L1Character target) {
        if (target != null) {
            astarCheckMap.putIfAbsent(target.getId(), 0);
        }
    }

    @Override
    public void targetResetting(L1Character attacker, L1Character target) {
        astarCheckMap.put(target.getId(), 0);
    }

    @Override
    public void validateTarget(L1Character attacker, L1Character target) {
        Collection<Integer> astarCheckIdList = astarCheckMap.values();

        List<L1PcInstance> visiblePlayer = L1World.getInstance().getVisiblePlayer(attacker, -1);

        visiblePlayer.forEach(pc -> {
            if (!astarCheckIdList.contains(pc.getId())) {
                astarCheckIdList.remove(pc.getId());
            }
        });
    }

    @Override
    public int calcDirection(L1Character attacker, L1Character target) {
        int astarCheckCount = astarCheckMap.get(target.getId());

        int distance = attacker.getLocation().getTileDistance(target.getLocation());

        int dir;

        if (CodeConfig.ASTAR_USE && distance < CodeConfig.ASTAR_CHECK_RANGE && astarCheckCount < CodeConfig.ASTAR_CHECK_COUNT
        ) {
            logger.trace("moveUsing Astar");
            astarCheckMap.put(target.getId(), astarCheckCount + 1);
            dir = aStar.calcDir(attacker, target.getX(), target.getY());
        } else {
            logger.trace("moveUsing L1CharPosUtils.CalcMoveDirection");
            dir = L1CharPosUtils.calcMoveDirection(attacker, target.getX(), target.getY());
        }

        return dir;
    }

    @Override
    public int getTotalCheckCount() {
        return aStar.getTotalCheckCount();
    }
}
