package ks.model.action.custom.impl.npc;

import ks.constants.L1ItemId;
import ks.model.L1Object;
import ks.model.action.custom.L1AbstractNpcAction;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_NPCTalkReturn;

public class ActionRafons extends L1AbstractNpcAction {
    public ActionRafons(String action, L1PcInstance pc, L1Object obj) {
        super(action, pc, obj);
    }

    @Override
    public void execute() {
        String s = action;
        String html = null;

        if (s.equalsIgnoreCase("A")) {
            if (pc.getInventory().checkItem(L1ItemId.ADENA, 10000)) {
                pc.getInventory().consumeItem(L1ItemId.ADENA, 10000);
                pc.getInventory().storeItem(41255, 1);
                html = "rrafons1";
            } else {
                html = "rrafons2";
            }
        } else if (s.equalsIgnoreCase("B")) {
            if (!pc.getInventory().checkItem(41256)) {
                if (pc.getInventory().checkItem(L1ItemId.ADENA, 3000)) {
                    if (pc.getInventory().checkItem(41255)) {
                        pc.getInventory().consumeItem(L1ItemId.ADENA, 3000);
                        pc.getInventory().consumeItem(41255, 1);
                        pc.getInventory().storeItem(41256, 1);
                        html = "rrafons4";
                    } else {
                        html = "rrafons5";
                    }
                } else {
                    html = "rrafons2";
                }
            } else {
                html = "rrafons3";
            }
        } else if (s.equalsIgnoreCase("q")) {
            if (!pc.getInventory().checkItem(41257)) {
                if (pc.getInventory().checkItem(L1ItemId.ADENA, 5000)) {
                    if (pc.getInventory().checkItem(41256)) {
                        pc.getInventory().consumeItem(L1ItemId.ADENA, 5000);
                        pc.getInventory().consumeItem(41256, 1);
                        pc.getInventory().storeItem(41257, 1);
                        html = "rrafons10";
                    } else {
                        html = "rrafons11";
                    }
                } else {
                    html = "rrafons2";
                }
            } else {
                html = "rrafons9";
            }
        }

        if (html != null) {
            pc.sendPackets(new S_NPCTalkReturn(objId, html));
        }
    }
}
