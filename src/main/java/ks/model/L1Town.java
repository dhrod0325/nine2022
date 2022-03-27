package ks.model;

public class L1Town {
    private int townId;
    private String name;
    private int leaderId;
    private String leaderName;
    private int taxRate;
    private int taxRateReserved;
    private int salesMoney;
    private int salesMoneyYesterday;
    private int townTax;
    private int townFixTax;

    public int getTownId() {
        return townId;
    }

    public void setTownId(int i) {
        townId = i;
    }

    public String getName() {
        return name;
    }

    public void setName(String s) {
        name = s;
    }

    public int getLeaderId() {
        return leaderId;
    }

    public void setLeaderId(int i) {
        leaderId = i;
    }

    public String getLeaderName() {
        return leaderName;
    }

    public void setLeaderName(String s) {
        leaderName = s;
    }

    public int getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(int i) {
        taxRate = i;
    }

    public int getTaxRateReserved() {
        return taxRateReserved;
    }

    public void setTaxRateReserved(int i) {
        taxRateReserved = i;
    }

    public int getSalesMoney() {
        return salesMoney;
    }

    public void setSalesMoney(int i) {
        salesMoney = i;
    }

    public int getSalesMoneyYesterday() {
        return salesMoneyYesterday;
    }

    public void setSalesMoneyYesterday(int i) {
        salesMoneyYesterday = i;
    }

    public int getTownTax() {
        return townTax;
    }

    public void setTownTax(int i) {
        townTax = i;
    }

    public int getTownFixTax() {
        return townFixTax;
    }

    public void setTownFixTax(int i) {
        townFixTax = i;
    }
}
