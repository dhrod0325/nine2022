package ks.system.adenBoard.model;

import java.util.Date;

public class AdenBankAccount {
    private Integer id;
    private String account_id;
    private String bank_no;
    private String bank_owner_name;
    private String bank_name;
    private String phone;
    private Date reg_date;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }


    public String getBank_owner_name() {
        return bank_owner_name;
    }

    public void setBank_owner_name(String bank_owner_name) {
        this.bank_owner_name = bank_owner_name;
    }

    public String getBank_name() {
        return bank_name;
    }

    public void setBank_name(String bank_name) {
        this.bank_name = bank_name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Date getReg_date() {
        return reg_date;
    }

    public void setReg_date(Date reg_date) {
        this.reg_date = reg_date;
    }

    public String getAccount_id() {
        return account_id;
    }

    public void setAccount_id(String account_id) {
        this.account_id = account_id;
    }

    public String getBank_no() {
        return bank_no;
    }

    public void setBank_no(String bank_no) {
        this.bank_no = bank_no;
    }
}
