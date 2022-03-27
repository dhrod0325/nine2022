package ks.system.auction;

import ks.core.network.opcode.L1Opcodes;
import ks.model.instance.L1NpcInstance;
import ks.packets.serverpackets.ServerBasePacket;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class S_AuctionBoard extends ServerBasePacket {
    public S_AuctionBoard(L1NpcInstance board) {
        buildPacket(board);
    }

    private void buildPacket(L1NpcInstance board) {
        List<Auction> auctionList = AuctionTable.getInstance().selectListByBidderId(0);

        List<Auction> outAuctionList = new ArrayList<>();

        for (Auction auction : auctionList) {
            int houseId = auction.getHouse_id();

            if (board.getX() == 33421 && board.getY() == 32823) { // 경매
                if (houseId >= 262145 && houseId <= 262189) {
                    outAuctionList.add(auction);
                }
            } else if (board.getX() == 33585 && board.getY() == 33235) { // 경매
                if (houseId >= 327681 && houseId <= 327691) {
                    outAuctionList.add(auction);
                }
            } else if (board.getX() == 33959 && board.getY() == 33253) { // 경매
                if (houseId >= 458753 && houseId <= 458819) {
                    outAuctionList.add(auction);
                }
            } else if (board.getX() == 32611 && board.getY() == 32775) { // 경매
                if (houseId >= 524289 && houseId <= 524294) {
                    outAuctionList.add(auction);
                }
            }
        }

        writeC(L1Opcodes.S_OPCODE_HOUSELIST);
        writeD(board.getId());
        writeH(outAuctionList.size()); // 레코드수

        for (Auction auction : outAuctionList) {
            writeD(auction.getHouse_id());
            writeS(auction.getHouse_name());
            writeH(auction.getHouse_area()); // 아지트의 넓이
            writeC(auction.getDeadlineCal().get(Calendar.MONTH) + 1); // 마감월
            writeC(auction.getDeadlineCal().get(Calendar.DATE)); // 마감일
            writeD(auction.getPrice()); // 현재의 입찰 가격
        }
    }
}
