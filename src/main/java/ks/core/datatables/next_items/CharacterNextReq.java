package ks.core.datatables.next_items;

import java.util.Date;

public class CharacterNextReq {
    private int id;
    private String password;
    private int itemId;
    private String itemName;
    private int itemCount;
    private Date regDate;
    private int serverState;
    private int itemEnchant;
    private int itemBless;
    private int itemAttr;
    private int itemObjId;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public int getItemCount() {
        return itemCount;
    }

    public void setItemCount(int itemCount) {
        this.itemCount = itemCount;
    }

    public Date getRegDate() {
        return regDate;
    }

    public void setRegDate(Date regDate) {
        this.regDate = regDate;
    }

    public int getServerState() {
        return serverState;
    }

    public void setServerState(int serverState) {
        this.serverState = serverState;
    }

    public int getItemEnchant() {
        return itemEnchant;
    }

    public void setItemEnchant(int itemEnchant) {
        this.itemEnchant = itemEnchant;
    }

    public int getItemBless() {
        return itemBless;
    }

    public void setItemBless(int itemBless) {
        this.itemBless = itemBless;
    }

    public int getItemAttr() {
        return itemAttr;
    }

    public void setItemAttr(int itemAttr) {
        this.itemAttr = itemAttr;
    }

    public int getItemObjId() {
        return itemObjId;
    }

    public void setItemObjId(int itemObjId) {
        this.itemObjId = itemObjId;
    }
}
