package ks.model.action.custom.impl.npc;

import ks.model.L1Object;
import ks.model.L1PolyMorph;
import ks.model.L1Teleport;
import ks.model.action.custom.L1AbstractNpcAction;
import ks.model.map.L1Map;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_NPCTalkReturn;
import ks.packets.serverpackets.S_NpcChatPacket;

public class ActionFish extends L1AbstractNpcAction {
    public ActionFish(String action, L1PcInstance pc, L1Object obj) {
        super(action, pc, obj);
    }

    @Override
    public void execute() {
        if (npcId == 80082) { // 낚시꼬마(IN)
            if (action.equalsIgnoreCase("a")) {
                if (pc.getInventory().checkItem(430520, 1)) {
                    L1PolyMorph.undoPoly(pc);

                    L1Teleport.teleport(pc, 32806, 32811, L1Map.MAP_FISHING, 6, true);

                    pc.sendPackets(new S_NPCTalkReturn(objId, "fk_in_3"));
                } else {
                    pc.sendPackets(new S_NpcChatPacket(npc, "낚시를 하려면 마법 낚시대가 필요합니다.", 0));
                }
            }
        } else if (npcId == 80083) {
            if (action.equalsIgnoreCase("a")) {
                L1Teleport.teleport(pc, 33439, 32814, (short) 4, 4, true);
            }
            if (action.equalsIgnoreCase("b")) {
                L1Teleport.teleport(pc, 32767, 32824, L1Map.MAP_FISHING, 5, true);
            }
            if (action.equalsIgnoreCase("c")) {
                L1Teleport.teleport(pc, 32789, 32870, L1Map.MAP_FISHING, 5, true);
            }
            if (action.equalsIgnoreCase("d")) {
                L1Teleport.teleport(pc, 32732, 32809, L1Map.MAP_FISHING, 5, true);
            }
            if (action.equalsIgnoreCase("e")) {
                L1Teleport.teleport(pc, 32732, 32869, L1Map.MAP_FISHING, 5, true);
            }
            if (action.equalsIgnoreCase("f")) {
                L1Teleport.teleport(pc, 32794, 32792, L1Map.MAP_FISHING, 5, true);
            }
        }
    }
}
