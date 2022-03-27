package ks.core.datatables.pet.model;

import ks.core.datatables.item.ItemTable;
import ks.model.instance.L1ItemInstance;

public class PetInventoryItem {
    private int objId;
    private int itemObjId;
    private int itemId;
    private String itemName;
    private int count;
    private int bless;
    private int enchant;
    private int attrEnchant;

    public int getObjId() {
        return objId;
    }

    public void setObjId(int objId) {
        this.objId = objId;
    }

    public int getItemObjId() {
        return itemObjId;
    }

    public void setItemObjId(int itemObjId) {
        this.itemObjId = itemObjId;
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

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getBless() {
        return bless;
    }

    public void setBless(int bless) {
        this.bless = bless;
    }

    public int getEnchant() {
        return enchant;
    }

    public void setEnchant(int enchant) {
        this.enchant = enchant;
    }

    public int getAttrEnchant() {
        return attrEnchant;
    }

    public void setAttrEnchant(int attrEnchant) {
        this.attrEnchant = attrEnchant;
    }

    public L1ItemInstance toItem() {
        L1ItemInstance item = ItemTable.getInstance().createItem(itemId);
        item.setId(itemObjId);
        item.setCount(count);
        item.setBless(bless);
        item.setAttrEnchantLevel(attrEnchant);
        item.setEnchantLevel(enchant);

        return item;
    }

    public static PetInventoryItem fromItem(int ownerId, L1ItemInstance item) {
        PetInventoryItem vo = new PetInventoryItem();
        vo.setObjId(ownerId);
        vo.setItemObjId(item.getId());
        vo.setItemId(item.getItemId());
        vo.setBless(item.getBless());
        vo.setAttrEnchant(item.getAttrEnchantLevel());
        vo.setEnchant(item.getEnchantLevel());
        vo.setCount(item.getCount());
        vo.setItemName(item.getName());

        return vo;
    }
}
