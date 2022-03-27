package ks.model.instance;

import ks.constants.L1SkillId;
import ks.core.datatables.MapsTable;
import ks.model.L1Location;
import ks.model.L1Object;
import ks.model.map.L1Map;
import ks.model.pc.L1PcInstance;
import ks.model.trap.L1Trap;
import ks.model.types.Point;
import ks.packets.serverpackets.S_RemoveObject;
import ks.packets.serverpackets.S_Trap;
import ks.util.common.random.RandomUtils;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class L1TrapInstance extends L1Object {
    private final L1Trap trap;

    private final Point rndPt;

    private final int span;
    private final String nameForView;
    private final List<L1PcInstance> knownPlayers = new CopyOnWriteArrayList<>();
    private boolean isEnable = true;

    public L1TrapInstance(int id, L1Trap trap, L1Location loc, Point rndPt, int span) {
        setId(id);
        this.trap = trap;
        this.span = span;
        setLocation(loc);
        this.rndPt = rndPt;
        nameForView = "trap";
        resetLocation();
    }

    public L1Location setupLocation() {
        if (rndPt.getX() == 0 && rndPt.getY() == 0 && getLocation().getX() == 0 && getLocation().getY() == 0) {
            MapsTable.MapData m = MapsTable.getInstance().getMaps().get(getLocation().getMapId());

            int x1 = m.startX;
            int x2 = m.endX;
            int y1 = m.startY;
            int y2 = m.endY;

            int rangeX = x2 - x1;
            int rangeY = y2 - y1;

            L1Map map = getLocation().getMap();

            L1Location loc = new L1Location();

            while (!map.isPassable(loc)) {
                loc.setX(RandomUtils.nextInt(rangeX) + x1);
                loc.setY(RandomUtils.nextInt(rangeY) + y1);
                loc.setMap(map);
            }

            return loc;
        } else if (rndPt.getX() != 0 && rndPt.getY() != 0) {
            return L1Location.randomLocation(getLocation(), 0, rndPt.getX(), false);
        }

        return null;
    }

    public void resetLocation() {
        L1Location s = setupLocation();
        if (s != null) {
            getLocation().set(s);
        }
    }

    public void enableTrap() {
        isEnable = true;
    }

    public void disableTrap() {
        isEnable = false;

        for (L1PcInstance pc : knownPlayers) {
            if (pc == null)
                continue;

            pc.getNearObjects().removeKnownObject(this);
            pc.sendPackets(new S_RemoveObject(this));
        }

        knownPlayers.clear();
    }

    public boolean isEnable() {
        return isEnable;
    }

    public int getSpan() {
        return span;
    }

    public void onTrod(L1PcInstance trodFrom) {
        if (trap != null)
            trap.onTrod(trodFrom, this);
    }

    public void onDetection() {
        trap.onDetection(this);
    }

    @Override
    public void onPerceive(L1PcInstance perceivedFrom) {
        if (perceivedFrom == null)
            return;
        if (perceivedFrom.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.GMSTATUS_SHOWTRAPS)) {
            perceivedFrom.getNearObjects().addKnownObject(this);
            perceivedFrom.sendPackets(new S_Trap(this, nameForView));
            knownPlayers.add(perceivedFrom);
        }
    }
}
