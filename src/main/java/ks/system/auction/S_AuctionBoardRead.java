package ks.system.auction;

import ks.core.network.opcode.L1Opcodes;
import ks.packets.serverpackets.ServerBasePacket;

import java.util.Calendar;

public class S_AuctionBoardRead extends ServerBasePacket {
    public S_AuctionBoardRead(int objectId, String houseNumber) {
        buildPacket(objectId, houseNumber);
    }

    private void buildPacket(int objectId, String houseNumber) {
        Auction auction = AuctionTable.getInstance().selectByHouseId(Integer.parseInt(houseNumber));

        if (auction == null) {
            return;
        }

        writeC(L1Opcodes.S_OPCODE_SHOWHTML);
        writeD(objectId);
        writeS("agsel");
        writeS(houseNumber);
        writeH(9);
        writeS(auction.getHouse_name());
        writeS(auction.getLocation());
        writeS(auction.getHouse_area() + "");
        writeS(auction.getOld_owner());
        writeS(auction.getBidder());
        writeS(auction.getPrice() + "");

        writeS(String.valueOf(auction.getDeadlineCal().get(Calendar.MONTH) + 1));
        writeS(String.valueOf(auction.getDeadlineCal().get(Calendar.DATE)));
        writeS(String.valueOf(auction.getDeadlineCal().get(Calendar.HOUR_OF_DAY)));
    }
}
