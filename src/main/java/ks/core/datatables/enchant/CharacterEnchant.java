package ks.core.datatables.enchant;

import java.util.Date;

public class CharacterEnchant {
    private int charId;
    private String charName;
    private int itemObjId;
    private int itemId;
    private String itemName;
    private int bless;
    private int attrLevel;
    private int enchant;
    private int nextEnchant;
    private String enchantType;
    private boolean success;
    private Date regDate;

    public int getCharId() {
        return charId;
    }

    public void setCharId(int charId) {
        this.charId = charId;
    }

    public String getCharName() {
        return charName;
    }

    public void setCharName(String charName) {
        this.charName = charName;
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

    public int getBless() {
        return bless;
    }

    public void setBless(int bless) {
        this.bless = bless;
    }

    public int getAttrLevel() {
        return attrLevel;
    }

    public void setAttrLevel(int attrLevel) {
        this.attrLevel = attrLevel;
    }

    public int getNextEnchant() {
        return nextEnchant;
    }

    public void setNextEnchant(int nextEnchant) {
        this.nextEnchant = nextEnchant;
    }

    public int getEnchant() {
        return enchant;
    }

    public void setEnchant(int enchant) {
        this.enchant = enchant;
    }

    public String getEnchantType() {
        return enchantType;
    }

    public void setEnchantType(String enchantType) {
        this.enchantType = enchantType;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getItemObjId() {
        return itemObjId;
    }

    public void setItemObjId(int itemObjId) {
        this.itemObjId = itemObjId;
    }

    public Date getRegDate() {
        return regDate;
    }

    public void setRegDate(Date regDate) {
        this.regDate = regDate;
    }
}
