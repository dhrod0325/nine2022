package ks.system.userShop.table;

import ks.util.L1CommonUtils;

public class L1UserShop {
    private int charId;
    private int totalCount;
    private int price;
    private int count;
    private String type;
    private int itemObjectId;

    private int itemId;
    private int enchantLvl;
    private int durability;
    private int bless;
    private int attrLvl;

    private String charName;

    private String itemName;

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getCharName() {
        return charName;
    }

    public void setCharName(String charName) {
        this.charName = charName;
    }

    public int getItemObjectId() {
        return itemObjectId;
    }

    public void setItemObjectId(int itemObjectId) {
        this.itemObjectId = itemObjectId;
    }

    public int getCharId() {
        return charId;
    }

    public void setCharId(int charId) {
        this.charId = charId;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public int getEnchantLvl() {
        return enchantLvl;
    }

    public void setEnchantLvl(int enchantLvl) {
        this.enchantLvl = enchantLvl;
    }

    public int getDurability() {
        return durability;
    }

    public void setDurability(int durability) {
        this.durability = durability;
    }

    public int getBless() {
        return bless;
    }

    public void setBless(int bless) {
        this.bless = bless;
    }

    public int getAttrLvl() {
        return attrLvl;
    }

    public void setAttrLvl(int attrLvl) {
        this.attrLvl = attrLvl;
    }

    public String getItemViewName() {
        StringBuilder itemName = new StringBuilder();

        if (getBless() != 0) {
            String type = "";

            if (getBless() == 0) {
                type = "축 ";
            } else if (getBless() == -1) {
                type = "저 ";
            }

            itemName.append(type);
        }

        if (getAttrLvl() > 0) {
            itemName.append(L1CommonUtils.getAttrNameKr(getAttrLvl())).append(" ");
        }

        if (getEnchantLvl() > 0) {
            itemName.append("+ ").append(getEnchantLvl()).append(" ");
        }

        itemName.append(getItemName());

        return itemName.toString();
    }

}
