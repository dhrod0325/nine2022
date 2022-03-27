package ks.model.trap;

import ks.core.ObjectIdFactory;
import ks.core.datatables.TrapTable;
import ks.model.L1Location;
import ks.model.L1World;
import ks.model.instance.L1TrapInstance;
import ks.model.pc.L1PcInstance;
import ks.model.types.Point;
import ks.util.common.SqlUtils;
import org.springframework.jdbc.core.RowMapper;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;

public class L1WorldTraps {
    private static final L1WorldTraps instance = new L1WorldTraps();
    private final List<L1TrapInstance> allTraps = new CopyOnWriteArrayList<>();
    private Timer timer = new Timer();

    public static L1WorldTraps getInstance() {
        return instance;
    }

    public void load() {
        removeTraps(allTraps);

        SqlUtils.query("SELECT * FROM spawnlist_trap", (RowMapper<L1TrapInstance>) (rs, s) -> {
            int trapId = rs.getInt("trapId");
            L1Trap trapTemp = TrapTable.getInstance().getTemplate(trapId);
            L1Location loc = new L1Location();
            loc.setMap(rs.getInt("mapId"));
            loc.setX(rs.getInt("locX"));
            loc.setY(rs.getInt("locY"));

            Point rndPt = new Point();
            rndPt.setX(rs.getInt("locRndX"));
            rndPt.setY(rs.getInt("locRndY"));

            int count = rs.getInt("count");
            int span = rs.getInt("span");

            for (int i = 0; i < count; i++) {
                L1TrapInstance trap = new L1TrapInstance(ObjectIdFactory.getInstance().nextId(), trapTemp, loc, rndPt, span);
                L1World.getInstance().addVisibleObject(trap);
                allTraps.add(trap);
            }

            return null;
        });

        resetTimer();
    }

    private void removeTraps(List<L1TrapInstance> traps) {
        for (L1TrapInstance trap : traps) {
            trap.disableTrap();
            L1World.getInstance().removeVisibleObject(trap);
            allTraps.remove(trap);
        }
    }

    private synchronized void resetTimer() {
        if (timer != null) {
            timer.cancel();
            timer = new Timer();
        }
    }

    private void disableTrap(L1TrapInstance trap) {
        trap.disableTrap();

        synchronized (this) {
            timer.schedule(new TrapSpawnTimer(trap), trap.getSpan());
        }
    }

    public void stopTraps(short mapId) {
        for (L1TrapInstance trap : allTraps) {
            if (trap.getMapId() == mapId) {
                trap.disableTrap();
            }
        }
    }

    public void resetAllTraps() {
        for (L1TrapInstance trap : allTraps) {
            trap.disableTrap();
            trap.resetLocation();
            trap.enableTrap();
        }
    }

    public void resetAllTraps(short mapId) {
        for (L1TrapInstance trap : allTraps) {
            if (trap.getMapId() == mapId) {
                trap.disableTrap();
                trap.resetLocation();
                trap.enableTrap();
            }
        }
    }

    public void onPlayerMoved(L1PcInstance player) {
        L1Location loc = player.getLocation();

        for (L1TrapInstance trap : allTraps) {
            if (trap.isEnable() && loc.equals(trap.getLocation())) {
                trap.onTrod(player);
                disableTrap(trap);
            }
        }
    }

    public void onDetection(L1PcInstance caster) {
        L1Location loc = caster.getLocation();

        for (L1TrapInstance trap : allTraps) {
            if (trap.isEnable() && loc.isInScreen(trap.getLocation())) {
                trap.onDetection();
                disableTrap(trap);
            }
        }
    }

    private static class TrapSpawnTimer extends TimerTask {
        private final L1TrapInstance targetTrap;

        public TrapSpawnTimer(L1TrapInstance trap) {
            targetTrap = trap;
        }

        @Override
        public void run() {
            targetTrap.resetLocation();
            targetTrap.enableTrap();
        }
    }
}
