package ks.system.portalsystem;

import ks.model.pc.L1PcInstance;
import ks.scheduler.timer.BaseTime;
import ks.system.portalsystem.model.L1PortalData;

import java.util.Date;

public interface L1PortalSystem {
    void open(BaseTime time);

    void run(BaseTime time);

    void shutdown();

    void close(BaseTime time);

    void waiting(BaseTime time);

    L1PortalData getPortalData();

    void setPortalData(L1PortalData portalData);

    boolean isOpen(BaseTime time);

    Date getCloseTime();

    void teleportToStartLocation(L1PcInstance pc, int idx);

    String getName();
}
