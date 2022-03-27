package ks.model.action.custom.impl.npc;

import ks.model.L1Object;
import ks.model.action.custom.L1AbstractNpcAction;
import ks.model.pc.L1PcInstance;
import ks.util.L1CommonUtils;

public class ActionDragonT extends L1AbstractNpcAction {
    public ActionDragonT(String action, L1PcInstance pc, L1Object obj) {
        super(action, pc, obj);
    }

    @Override
    public void execute() {
        String act = action.replace("cc_dragont_", "");

        if (act.startsWith("a")) {
            int enchant = Integer.parseInt(act.replace("a", ""));
            createChangeItem(pc, enchant, 55000098, 155000098);
        } else if (act.startsWith("b")) {
            int enchant = Integer.parseInt(act.replace("b", ""));
            createChangeItem(pc, enchant, 55000097, 155000097);
        } else if (act.startsWith("c")) {
            int enchant = Integer.parseInt(act.replace("c", ""));
            createChangeItem(pc, enchant, 55000096, 155000096);
        } else if (act.startsWith("d")) {
            int enchant = Integer.parseInt(act.replace("d", ""));
            createChangeItem(pc, enchant, 55000095, 155000095);
        }
    }

    private void createChangeItem(L1PcInstance pc, int enchant, int oldItemId, int newItemId) {
        L1CommonUtils.createOldItemChange(pc, oldItemId, enchant, 2, newItemId, enchant);
    }
}
