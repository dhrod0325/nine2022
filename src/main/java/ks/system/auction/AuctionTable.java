package ks.system.auction;

import ks.app.LineageAppContext;
import ks.util.common.SqlUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.List;

@Component
public class AuctionTable {
    public static AuctionTable getInstance() {
        return LineageAppContext.getBean(AuctionTable.class);
    }

    public List<Auction> selectListAll() {
        String sql = "SELECT * FROM board_auction ORDER BY house_area desc ";
        return SqlUtils.query(sql, new BeanPropertyRowMapper<>(Auction.class));
    }

    public List<Auction> selectListByBidderId(int bidderId) {
        String sql = "SELECT * FROM board_auction where bidder_id = ? AND house_id NOT IN (SELECT hashouse FROM clan_data WHERE hashouse > 0) ORDER BY house_area desc";
        return SqlUtils.query(sql, new BeanPropertyRowMapper<>(Auction.class), bidderId);
    }

    public Auction selectByHouseId(int houseId) {
        return SqlUtils.select("SELECT * FROM board_auction WHERE house_id=? ", new BeanPropertyRowMapper<>(Auction.class), houseId);
    }

    public void updateAuctionBoard(Auction auction) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String fm = formatter.format(auction.getDeadline().getTime());

        SqlUtils.update("UPDATE board_auction SET house_name=?, house_area=?, deadline=?, price=?, location=?, old_owner=?, old_owner_id=?, bidder=?, bidder_id=? WHERE house_id=?",
                auction.getHouse_name(),
                auction.getHouse_area(),
                fm,
                auction.getPrice(),
                auction.getLocation(),
                auction.getOld_owner(),
                auction.getOld_owner_id(),
                auction.getBidder(),
                auction.getBidder_id(),
                auction.getHouse_id()
        );
    }
}
