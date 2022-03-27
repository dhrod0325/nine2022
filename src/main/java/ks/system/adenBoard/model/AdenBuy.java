package ks.system.adenBoard.model;

import java.util.Date;

public class AdenBuy {
    private int id;
    private int aden_sell_id;
    private String buyer_id;
    private String buyer_name;
    private Date reg_date;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAden_sell_id() {
        return aden_sell_id;
    }

    public void setAden_sell_id(int aden_sell_id) {
        this.aden_sell_id = aden_sell_id;
    }

    public String getBuyer_id() {
        return buyer_id;
    }

    public void setBuyer_id(String buyer_id) {
        this.buyer_id = buyer_id;
    }

    public String getBuyer_name() {
        return buyer_name;
    }

    public void setBuyer_name(String buyer_name) {
        this.buyer_name = buyer_name;
    }

    public Date getReg_date() {
        return reg_date;
    }

    public void setReg_date(Date reg_date) {
        this.reg_date = reg_date;
    }
}
