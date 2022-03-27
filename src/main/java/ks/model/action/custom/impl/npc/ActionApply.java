package ks.model.action.custom.impl.npc;

import ks.model.L1Clan;
import ks.model.L1Object;
import ks.model.L1World;
import ks.model.action.custom.L1AbstractNpcAction;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_ServerMessage;
import ks.system.auction.S_AuctionApply;

public class ActionApply extends L1AbstractNpcAction {
    public ActionApply(String action, L1PcInstance pc, L1Object obj, String param) {
        super(action, pc, obj, param);
    }

    @Override
    public void execute() {
        L1Clan clan = L1World.getInstance().getClan(pc.getClanName());

        if (clan != null) {
            if (pc.isCrown() && pc.getId() == clan.getLeaderId()) {
                if (pc.getLevel() >= 15) {
                    pc.sendPackets(new S_AuctionApply(objId, param));
                } else {
                    pc.sendPackets(new S_ServerMessage(519));
                }
            } else {
                pc.sendPackets(new S_ServerMessage(518));
            }
        } else {
            pc.sendPackets(new S_ServerMessage(518));
        }
    }
}
