package ks.model.instance;

import ks.model.L1Npc;
import ks.model.pc.L1PcInstance;
import ks.system.auction.S_AuctionBoard;

public class L1AuctionBoardInstance extends L1NpcInstance {
    public L1AuctionBoardInstance(L1Npc template) {
        super(template);
    }

    @Override
    public void onAction(L1PcInstance pc) {
        if (pc.isCrown() && pc.getClanId() > 0) {
            if (pc.getClan().getHouseId() > 0) {
                pc.sendGreenMessage("아지트 소유중입니다. 경매에 참여하시면 소유아지트는 자동으로 삭제됩니다");
            }
        }

        pc.sendPackets(new S_AuctionBoard(this));
    }
}
