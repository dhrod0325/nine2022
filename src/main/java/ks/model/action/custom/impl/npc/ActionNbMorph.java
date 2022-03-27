package ks.model.action.custom.impl.npc;

import ks.constants.L1ItemId;
import ks.model.L1Object;
import ks.model.L1PolyMorph;
import ks.model.action.custom.L1AbstractNpcAction;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_ServerMessage;

public class ActionNbMorph extends L1AbstractNpcAction {
    public ActionNbMorph(String actionName, L1PcInstance pc, L1Object obj) {
        super(actionName, pc, obj);
    }

    @Override
    public void execute() {
        if (action.equalsIgnoreCase("skeleton nbmorph")) {
            if (pc.getLevel() < 13) {
                if (pc.getInventory().checkItem(L1ItemId.ADENA, 100)) {
                    poly(pc, 2374);
                    pc.getInventory().consumeItem(L1ItemId.ADENA, 100);
                } else {
                    pc.sendPackets(new S_ServerMessage(189)); // \f1아데나가 부족합니다.
                }
            }
        } else if (action.equalsIgnoreCase("lycanthrope nbmorph")) {
            if (pc.getInventory().checkItem(L1ItemId.ADENA, 100)) {
                poly(pc, 3874);
                pc.getInventory().consumeItem(L1ItemId.ADENA, 100);
            } else {
                pc.sendPackets(new S_ServerMessage(189)); // \f1아데나가 부족합니다.
            }
        } else if (action.equalsIgnoreCase("shelob nbmorph")) {
            if (pc.getLevel() < 13) {
                if (pc.getInventory().checkItem(L1ItemId.ADENA, 100)) {
                    poly(pc, 95);
                    pc.getInventory().consumeItem(L1ItemId.ADENA, 100);
                } else {
                    pc.sendPackets(new S_ServerMessage(189)); // \f1아데나가 부족합니다.
                }
            }
        } else if (action.equalsIgnoreCase("ghoul nbmorph")) {
            if (pc.getLevel() < 13) {
                if (pc.getInventory().checkItem(L1ItemId.ADENA, 100)) {
                    poly(pc, 3873);
                    pc.getInventory().consumeItem(L1ItemId.ADENA, 100);
                } else {
                    pc.sendPackets(new S_ServerMessage(189)); // \f1아데나가 부족합니다.
                }
            }
        } else if (action.equalsIgnoreCase("ghast nbmorph")) {
            if (pc.getLevel() < 13) {
                if (pc.getInventory().checkItem(L1ItemId.ADENA, 100)) {
                    poly(pc, 3875);
                    pc.getInventory().consumeItem(L1ItemId.ADENA, 100);
                } else {
                    pc.sendPackets(new S_ServerMessage(189)); // \f1아데나가 부족합니다.
                }
            }
        } else if (action.equalsIgnoreCase("atuba orc nbmorph")) {
            if (pc.getLevel() < 13) {
                if (pc.getInventory().checkItem(L1ItemId.ADENA, 100)) {
                    poly(pc, 3868);
                    pc.getInventory().consumeItem(L1ItemId.ADENA, 100);
                } else {
                    pc.sendPackets(new S_ServerMessage(189)); // \f1아데나가 부족합니다.
                }
            }
        } else if (action.equalsIgnoreCase("skeleton axeman nbmorph")) {
            if (pc.getLevel() < 13) {
                if (pc.getInventory().checkItem(L1ItemId.ADENA, 100)) {
                    poly(pc, 2376);
                    pc.getInventory().consumeItem(L1ItemId.ADENA, 100);
                } else {
                    pc.sendPackets(new S_ServerMessage(189)); // \f1아데나가 부족합니다.
                }
            }
        } else if (action.equalsIgnoreCase("troll nbmorph")) {
            if (pc.getLevel() < 13) {
                if (pc.getInventory().checkItem(L1ItemId.ADENA, 100)) {
                    poly(pc, 3878);
                    pc.getInventory().consumeItem(L1ItemId.ADENA, 100);
                } else {
                    pc.sendPackets(new S_ServerMessage(189)); // \f1아데나가 부족합니다.
                }
            }
        }
    }

    private void poly(L1PcInstance pc, int polyId) {
        if (pc.getInventory().checkItem(L1ItemId.ADENA, 100)) {
            pc.getInventory().consumeItem(L1ItemId.ADENA, 100);

            L1PolyMorph.doPoly(pc, polyId, 1800, L1PolyMorph.MORPH_BY_NPC);
        } else {
            pc.sendPackets(new S_ServerMessage(337, "$4"));
        }
    }
}
