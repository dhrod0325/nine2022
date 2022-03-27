package ks.model.pc.hackCheck.pierce;

import ks.model.L1Location;
import ks.model.L1World;
import ks.model.pc.L1PcInstance;
import ks.util.log.L1LogUtils;

import java.util.List;

public class L1PierceCheck {
    private final L1PcInstance pc;
    private L1Location prevLocation;
    private int movingCount = 0;

    public L1PierceCheck(L1PcInstance pc) {
        this.pc = pc;
    }

    public L1Location getPrevLocation() {
        return prevLocation;
    }

    public void setPrevLocation(L1Location prevLocation) {
        this.prevLocation = prevLocation;
    }

    public void onMove() {
        movingCount++;

        if (prevLocation != null) {
            double distance = pc.getLocation().getTileLineDistance(prevLocation);

            if (distance > 1) {
                log(pc);
            }
        }

        List<L1PcInstance> player = L1World.getInstance().getVisiblePlayer(pc, 0);

        if (!player.isEmpty()) {
            log(pc);
        }
    }

    private void log(L1PcInstance pc) {
        String msg = pc.getName() + " : 뚫어 버그사용이 의심됩니다";
        L1LogUtils.bugLog(msg, true);
    }

    public int getMovingCount() {
        return movingCount;
    }

    public void resetMove() {
        movingCount = 0;
    }
}
