package ks.model.action.custom.impl.npc;

import ks.constants.L1ClanRankId;
import ks.model.L1Object;
import ks.model.action.custom.L1AbstractNpcAction;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_RetrievePledgeList;
import ks.packets.serverpackets.S_ServerMessage;
import ks.util.L1CommonUtils;

public class ActionRetrievePledge extends L1AbstractNpcAction {
    public ActionRetrievePledge(String action, L1PcInstance pc, L1Object obj) {
        super(action, pc, obj);
    }

    @Override
    public void execute() {
        if (pc.getLevel() >= 5) {
            if (L1CommonUtils.isTwoLogin(pc))
                return;
            if (pc.getClan() != null) {
                if (!(pc.getClanRank() == L1ClanRankId.CLAN_RANK_PROBATION || pc.getClanRank() == L1ClanRankId.CLAN_RANK_PUBLIC)) {
                    if (pc.getAccount().getGamePassword() != 0)
                        pc.sendPackets(new S_ServerMessage(834));// 비번띄우기
                    else
                        pc.sendPackets(new S_RetrievePledgeList(objId, pc));
                } else
                    pc.sendPackets(new S_ServerMessage(728));
            } else
                pc.sendPackets(new S_ServerMessage(208));
        }
    }
}
