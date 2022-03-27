package ks.system.auction;

import ks.core.network.opcode.L1Opcodes;
import ks.packets.serverpackets.ServerBasePacket;

public class S_AuctionApply extends ServerBasePacket {
    public S_AuctionApply(int objectId, String houseNumber) {
        buildPacket(objectId, houseNumber);
    }

    private void buildPacket(int objectId, String houseNumber) {
        Auction auction = AuctionTable.getInstance().selectByHouseId(Integer.parseInt(houseNumber));

        if (auction == null) {
            return;
        }

        int nowPrice = auction.getPrice();

        writeC(L1Opcodes.S_OPCODE_INPUTAMOUNT);
        writeD(objectId);
        writeD(0);
        writeD(nowPrice);
        writeD(nowPrice);
        writeD(nowPrice);
        writeH(0);
        writeS("agapply");
        writeS("agapply " + houseNumber);
    }
}
