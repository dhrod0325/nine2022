package ks.system.portalsystem;

import ks.listener.TimeListenerAdapter;
import ks.scheduler.timer.BaseTime;
import ks.system.portalsystem.model.L1PortalData;
import ks.util.L1CommonUtils;

public class L1PortalSystemListener extends TimeListenerAdapter {
    private L1PortalSystem portalSystem;

    private boolean open;

    public void setPortalSystem(L1PortalSystem portalSystem) {
        this.portalSystem = portalSystem;
    }

    @Override
    public void onSecondChanged(BaseTime time) {
        if (L1CommonUtils.isStandByServer())
            return;

        if (isOpen()) {
            if (portalSystem.isOpen(time)) {
                portalSystem.run(time);
            } else {
                portalSystem.close(time);
                setOpen(false);
            }
        } else {
            if (portalSystem.isOpen(time)) {
                portalSystem.open(time);
                setOpen(true);
            } else {
                portalSystem.waiting(time);
            }
        }
    }

    public void shutDown() {
        portalSystem.shutdown();
        setOpen(false);
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    @Override
    public String toString() {
        String result = "L1PortalSystemListener - ";

        if (portalSystem.getPortalData() != null) {
            L1PortalData data = portalSystem.getPortalData();
            result += data.getName();
        }

        return result;
    }
}
