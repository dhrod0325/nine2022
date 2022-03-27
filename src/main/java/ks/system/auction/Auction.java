package ks.system.auction;

import java.util.Calendar;
import java.util.Date;

public class Auction {
    private int house_id;
    private String house_name;
    private int house_area;
    private Date deadline;
    private int price;
    private String location;
    private String old_owner;
    private int old_owner_id;
    private String bidder;
    private int bidder_id;

    public int getHouse_id() {
        return house_id;
    }

    public void setHouse_id(int house_id) {
        this.house_id = house_id;
    }

    public String getHouse_name() {
        return house_name;
    }

    public void setHouse_name(String house_name) {
        this.house_name = house_name;
    }

    public int getHouse_area() {
        return house_area;
    }

    public void setHouse_area(int house_area) {
        this.house_area = house_area;
    }

    public Calendar getDeadlineCal() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(deadline);

        return calendar;
    }

    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getOld_owner() {
        return old_owner;
    }

    public void setOld_owner(String old_owner) {
        this.old_owner = old_owner;
    }

    public int getOld_owner_id() {
        return old_owner_id;
    }

    public void setOld_owner_id(int old_owner_id) {
        this.old_owner_id = old_owner_id;
    }

    public String getBidder() {
        return bidder;
    }

    public void setBidder(String bidder) {
        this.bidder = bidder;
    }

    public int getBidder_id() {
        return bidder_id;
    }

    public void setBidder_id(int bidder_id) {
        this.bidder_id = bidder_id;
    }
}
