package ks.model.instance;

import ks.core.datatables.NPCTalkDataTable;
import ks.model.L1Npc;
import ks.model.L1NpcTalkData;
import ks.model.attack.physics.L1AttackRun;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_NPCTalkReturn;
import ks.packets.serverpackets.S_ServerMessage;
import org.apache.commons.lang3.StringUtils;

public class L1DwarfInstance extends L1NpcInstance {
    public L1DwarfInstance(L1Npc template) {
        super(template);
    }

    @Override
    public void onAction(L1PcInstance pc) {
        L1AttackRun attack = new L1AttackRun(pc, this);
        attack.action();
    }

    @Override
    public void onTalkAction(L1PcInstance pc) {
        int objid = getId();
        L1NpcTalkData talking = NPCTalkDataTable.getInstance().getTemplate(getTemplate().getNpcId());
        int npcId = getTemplate().getNpcId();
        String htmlid = null;

        if (talking != null) {
            if (npcId == 60028) {
                if (!pc.isElf()) {
                    htmlid = "elCE1";
                }
            }

            if (htmlid != null) {
                pc.sendPackets(new S_NPCTalkReturn(objid, htmlid));
            } else {
                if (pc.getLevel() < 5) {
                    pc.sendPackets(new S_NPCTalkReturn(talking, objid, 2));
                } else {
                    pc.sendPackets(new S_NPCTalkReturn(talking, objid, 1));
                }
            }
        }
    }

    @Override
    public void onFinalAction(L1PcInstance pc, String Action) {
        if (Action.equalsIgnoreCase("retrieve-pledge")) {
            if (StringUtils.isEmpty(pc.getClanName())) {
                pc.sendPackets(new S_ServerMessage((S_ServerMessage.NO_PLEDGE), Action));
            }
        }
    }
}