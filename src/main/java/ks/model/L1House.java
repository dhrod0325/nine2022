package ks.model;

import java.util.Calendar;

public class L1House {
    private int _houseId;
    private String _houseName;
    private int _houseArea;
    private String _location;
    private int _keeperId;
    private boolean _isOnSale;
    private boolean _isPurchaseBasement;
    private Calendar _taxDeadline;

    public L1House() {
    }

    public int getHouseId() {
        return _houseId;
    }

    public void setHouseId(int i) {
        _houseId = i;
    }

    public String getHouseName() {
        return _houseName;
    }

    public void setHouseName(String s) {
        _houseName = s;
    }

    public int getHouseArea() {
        return _houseArea;
    }

    public void setHouseArea(int i) {
        _houseArea = i;
    }

    public String getLocation() {
        return _location;
    }

    public void setLocation(String s) {
        _location = s;
    }

    public int getKeeperId() {
        return _keeperId;
    }

    public void setKeeperId(int i) {
        _keeperId = i;
    }

    public boolean isOnSale() {
        return _isOnSale;
    }

    public void setOnSale(boolean flag) {
        _isOnSale = flag;
    }

    public boolean isPurchaseBasement() {
        return _isPurchaseBasement;
    }

    public void setPurchaseBasement(boolean flag) {
        _isPurchaseBasement = flag;
    }

    public Calendar getTaxDeadline() {
        return _taxDeadline;
    }

    public void setTaxDeadline(Calendar i) {
        _taxDeadline = i;
    }

}