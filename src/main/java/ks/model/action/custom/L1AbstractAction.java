package ks.model.action.custom;

import ks.core.datatables.item.ItemTable;
import ks.model.L1Item;
import ks.model.L1Object;
import ks.model.instance.L1ItemInstance;
import ks.model.instance.L1NpcInstance;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_NPCTalkReturn;
import ks.packets.serverpackets.S_ServerMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class L1AbstractAction implements L1ActionExecutor {
    protected Logger logger = LogManager.getLogger();

    protected String action;

    protected L1PcInstance pc;

    protected L1Object obj;

    protected int objId;

    protected String param;

    public L1AbstractAction(String action, L1PcInstance pc, L1Object obj, String param) {
        this.action = action;
        this.pc = pc;
        this.obj = obj;
        this.param = param;

        this.objId = this.obj.getId();
    }

    public void createItem(
            int[] createItem,
            int[] createCount,
            int[] materials,
            int[] counts,
            String successHtmlId,
            String failureHtmlId,
            String[] htmlData) {

        if (createItem != null) {
            boolean isCreate = true;

            for (int j = 0; j < materials.length; j++) {
                if (!pc.getInventory().checkItemNotEquipped(materials[j], counts[j])) {
                    L1Item temp = ItemTable.getInstance().getTemplate(materials[j]);
                    pc.sendPackets(new S_ServerMessage(337, temp.getName()));
                    isCreate = false;
                }
            }

            if (isCreate) {
                int createItemCount = 0;
                int createWeight = 0;

                for (int k = 0; k < createItem.length; k++) {
                    L1Item temp = ItemTable.getInstance().getTemplate(createItem[k]);
                    if (temp.isStackable()) {
                        if (!pc.getInventory().checkItem(createItem[k])) {
                            createItemCount += 1;
                        }
                    } else {
                        createItemCount += createCount[k];
                    }

                    createWeight += (temp.getWeight() / 1000) * createCount[k];
                }

                if (pc.getInventory().getSize() + createItemCount > 180) {
                    pc.sendPackets(new S_ServerMessage(263));
                    return;
                }

                if (pc.getMaxWeight() < pc.getInventory().getWeight() + createWeight) {
                    pc.sendPackets(new S_ServerMessage(82));
                    return;
                }

                for (int j = 0; j < materials.length; j++) {
                    pc.getInventory().consumeItem(materials[j], counts[j]);
                }

                for (int k = 0; k < createItem.length; k++) {
                    L1ItemInstance item = pc.getInventory().storeItem(createItem[k], createCount[k]);

                    if (item != null) {
                        String itemName = ItemTable.getInstance().getTemplate(createItem[k]).getName();
                        String creatorName = ((L1NpcInstance) obj).getTemplate().getName();

                        pc.sendPackets(new S_ServerMessage(143, creatorName, itemName));
                    }
                }
                if (successHtmlId != null) {
                    pc.sendPackets(new S_NPCTalkReturn(objId, successHtmlId, htmlData));
                }
            } else {
                if (failureHtmlId != null) {
                    pc.sendPackets(new S_NPCTalkReturn(objId, failureHtmlId, htmlData));
                }
            }
        }
    }

    public void createItem(L1PcInstance pc, int[] itemIds, int[] itemAmounts) {
        for (int i = 0; i < itemIds.length; i++) {
            L1ItemInstance item = pc.getInventory().storeItem(itemIds[i], itemAmounts[i]);
            pc.sendPackets(new S_ServerMessage(403, item.getLogName()));
        }
    }
}
