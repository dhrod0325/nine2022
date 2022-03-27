package ks.model.action.xml;

import ks.model.L1Object;
import ks.model.pc.L1PcInstance;

public interface L1NpcAction {
    boolean acceptsRequest(String actionName, L1PcInstance pc, L1Object obj);

    L1NpcHtml execute(String actionName, L1PcInstance pc, L1Object obj, byte[] args);

    L1NpcHtml executeWithAmount(String actionName, L1PcInstance pc, L1Object obj, int amount);
}
