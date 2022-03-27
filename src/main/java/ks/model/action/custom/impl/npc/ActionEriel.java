package ks.model.action.custom.impl.npc;

import ks.constants.L1ItemId;
import ks.model.L1Object;
import ks.model.L1PcInventory;
import ks.model.L1World;
import ks.model.action.custom.L1AbstractNpcAction;
import ks.model.instance.L1ItemInstance;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_ServerMessage;

public class ActionEriel extends L1AbstractNpcAction {
    public ActionEriel(String action, L1PcInstance pc, L1Object obj) {
        super(action, pc, obj);
    }

    @Override
    public void execute() {
        if (npcId == 70702) {
            if (pc.getPartnerId() != 0) {
                for (L1PcInstance partner : L1World.getInstance()
                        .getVisiblePlayer(pc, 3)) {
                    if (partner.getId() == pc.getPartnerId()) {
                        break;
                    }
                    return;
                }
                if (pc.getInventory().checkItem(40903)
                        || pc.getInventory().checkItem(40904)
                        || pc.getInventory().checkItem(40905)
                        || pc.getInventory().checkItem(40906)
                        || pc.getInventory().checkItem(40907)
                        || pc.getInventory().checkItem(40908)) {
                    if (pc.getInventory().checkItem(L1ItemId.ADENA, 1000)) {
                        int chargeCount = 0;
                        for (int itemId = 40903; itemId <= 40908; itemId++) {
                            L1ItemInstance item = pc.getInventory().findItemId(itemId);
                            if (itemId == 40903 || itemId == 40904 || itemId == 40905) {
                                chargeCount = itemId - 40902;
                            }
                            if (itemId == 40906) {
                                chargeCount = 5;
                            }
                            if (itemId == 40907 || itemId == 40908) {
                                chargeCount = 20;
                            }
                            if (item != null && item.getChargeCount() != chargeCount) {
                                item.setChargeCount(chargeCount);
                                pc.getInventory().updateItem(item, L1PcInventory.COL_CHARGE_COUNT);
                                pc.getInventory().consumeItem(L1ItemId.ADENA, 1000);
                            }
                        }
                    } else {
                        pc.sendPackets(new S_ServerMessage(337, "$4"));
                    }
                }
            }
        }
    }
}
