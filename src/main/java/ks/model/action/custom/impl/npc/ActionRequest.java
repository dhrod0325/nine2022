package ks.model.action.custom.impl.npc;

import ks.model.L1Object;
import ks.model.action.custom.L1AbstractNpcAction;
import ks.model.pc.L1PcInstance;

public class ActionRequest extends L1AbstractNpcAction {
    public ActionRequest(String action, L1PcInstance pc, L1Object obj) {
        super(action, pc, obj);
    }

    @Override
    public void execute() {
        if (action.equalsIgnoreCase("request las history book")) {
            int[] materials = new int[]{41019, 41020, 41021, 41022, 41023, 41024, 41025, 41026};
            int[] counts = new int[]{1, 1, 1, 1, 1, 1, 1, 1};
            int[] createItem = new int[]{41027};
            int[] createCount = new int[]{1};

            createItem(createItem, createCount, materials, counts, "", "", null);
        }
    }
}
