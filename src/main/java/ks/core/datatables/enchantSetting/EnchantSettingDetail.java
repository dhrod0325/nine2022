package ks.core.datatables.enchantSetting;

public class EnchantSettingDetail {
    private int enchantLevel;
    private int itemId;
    private String scrollType;
    private int per;
    private String note;

    public int getEnchantLevel() {
        return enchantLevel;
    }

    public void setEnchantLevel(int enchantLevel) {
        this.enchantLevel = enchantLevel;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public String getScrollType() {
        return scrollType;
    }

    public void setScrollType(String scrollType) {
        this.scrollType = scrollType;
    }

    public int getPer() {
        return per;
    }

    public void setPer(int per) {
        this.per = per;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
