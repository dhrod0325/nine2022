package ks.model.instance;

import ks.core.datatables.NPCTalkDataTable;
import ks.model.L1Npc;
import ks.model.L1NpcTalkData;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_NPCTalkReturn;

@SuppressWarnings("unused")
public class L1RequestInstance extends L1NpcInstance {
    public L1RequestInstance(L1Npc template) {
        super(template);
    }

    @Override
    public void onAction(L1PcInstance player) {
        int objid = getId();

        L1NpcTalkData talking = NPCTalkDataTable.getInstance().getTemplate(getTemplate().getNpcId());

        if (talking != null) {
            if (player.getLawful() < -1000) {
                player.sendPackets(new S_NPCTalkReturn(talking, objid, 2));
            } else {
                player.sendPackets(new S_NPCTalkReturn(talking, objid, 1));
            }
        }
    }

    @Override
    public void onFinalAction(L1PcInstance player, String action) {

    }

    public void doFinalAction(L1PcInstance player) {

    }
}
