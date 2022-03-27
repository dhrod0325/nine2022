package ks.system.portalsystem;

import ks.model.pc.L1PcInstance;
import ks.util.L1CommonUtils;

import java.util.List;

public class L1DefaultPortalSystem extends L1AbstractPortalSystem {
    @Override
    protected List<L1PcInstance> getInnerPlayers() {
        return L1CommonUtils.getInnerPlayers(getPortalData().getMapId());
    }

    @Override
    public String getName() {
        return getPortalData().getName();
    }
}