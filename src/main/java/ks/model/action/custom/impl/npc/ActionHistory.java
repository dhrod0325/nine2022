package ks.model.action.custom.impl.npc;

import ks.constants.L1ClanRankId;
import ks.constants.L1PacketBoxType;
import ks.model.L1Object;
import ks.model.action.custom.L1AbstractNpcAction;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_PacketBox;
import ks.packets.serverpackets.S_ServerMessage;
import ks.util.L1CommonUtils;

public class ActionHistory extends L1AbstractNpcAction {
    public ActionHistory(String action, L1PcInstance pc, L1Object obj) {
        super(action, pc, obj);
    }

    @Override
    public void execute() {
        if (pc.isCrown() && pc.getClanId() > 0) {
            if (L1CommonUtils.isTwoLogin(pc))
                return;
            if (pc.getClan() != null) {
                if (!(pc.getClanRank() == L1ClanRankId.CLAN_RANK_PROBATION || pc.getClanRank() == L1ClanRankId.CLAN_RANK_PUBLIC)) {
                    pc.sendPackets(new S_PacketBox(pc, L1PacketBoxType.CLAN_WAREHOUSE_LIST));
                } else {
                    pc.sendPackets(new S_ServerMessage(728));
                }

            } else {
                pc.sendPackets(new S_ServerMessage(208));
            }
        }
    }
}
