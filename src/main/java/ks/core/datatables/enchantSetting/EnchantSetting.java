package ks.core.datatables.enchantSetting;

public class EnchantSetting {
    private int enchantLevel;
    private int safeEnchant;
    private String scrollType;
    private int per;
    private int item;
    private int value;
    private String note;

    public int getEnchantLevel() {
        return enchantLevel;
    }

    public void setEnchantLevel(int enchantLevel) {
        this.enchantLevel = enchantLevel;
    }

    public int getSafeEnchant() {
        return safeEnchant;
    }

    public void setSafeEnchant(int safeEnchant) {
        this.safeEnchant = safeEnchant;
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

    public int getItem() {
        return item;
    }

    public void setItem(int item) {
        this.item = item;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
