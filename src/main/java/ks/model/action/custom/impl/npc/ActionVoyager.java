package ks.model.action.custom.impl.npc;

import ks.model.L1Object;
import ks.model.action.custom.L1AbstractNpcAction;
import ks.model.instance.L1ItemInstance;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_NPCTalkReturn;
import ks.packets.serverpackets.S_ServerMessage;
import ks.util.common.random.RandomUtils;

public class ActionVoyager extends L1AbstractNpcAction {
    public ActionVoyager(String action, L1PcInstance pc, L1Object obj) {
        super(action, pc, obj);
    }

    @Override
    public void execute() {
        String htmlid = null;

        if (action.equalsIgnoreCase("A")) {
            int[] diaryNo = {49082, 49083};
            int pid = RandomUtils.nextInt(diaryNo.length);
            int di = diaryNo[pid];

            if (di == 49082) {
                htmlid = "voyager6a";

                L1ItemInstance item = pc.getInventory().storeItem(di, 1);
                String npcName = npc.getTemplate().getName();
                String itemName = item.getItem().getName();
                pc.sendPackets(new S_ServerMessage(143, npcName, itemName));
            } else {
                htmlid = "voyager6b";

                L1ItemInstance item = pc.getInventory().storeItem(di, 1);
                String npcName = npc.getTemplate().getName();
                String itemName = item.getItem().getName();
                pc.sendPackets(new S_ServerMessage(143, npcName, itemName));
            }
        }

        if (htmlid != null) {
            pc.sendPackets(new S_NPCTalkReturn(objId, htmlid));
        }
    }
}
