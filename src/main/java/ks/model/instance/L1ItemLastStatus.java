package ks.model.instance;

import java.sql.Timestamp;

public class L1ItemLastStatus {
    private final L1ItemInstance item;
    public int clock;

    public Timestamp endTime = null;

    public int count;

    public int itemId;

    public boolean isEquipped = false;

    public int enchantLevel;

    public boolean isIdentified = true;

    public int durability;

    public int chargeCount;

    public int remainingTime;

    public Timestamp lastUsed = null;

    public int bless;

    public int attrenchantLevel;

    public int protection; //추가

    public int optionGrade;

    public int nextReq;

    public L1ItemLastStatus(L1ItemInstance item) {
        this.item = item;
    }

    public void updateAll() {
        count = item.getCount();
        itemId = item.getItemId();
        isEquipped = item.isEquipped();
        isIdentified = item.isIdentified();
        enchantLevel = item.getEnchantLevel();
        durability = item.getDurability();
        chargeCount = item.getChargeCount();
        remainingTime = item.getRemainingTime();
        lastUsed = item.getLastUsed();
        bless = item.getBless();
        attrenchantLevel = item.getAttrEnchantLevel();
        protection = item.getProtection();
        clock = item.getClock();
        endTime = item.getEndTime();
        optionGrade = item.getOptionGrade();
        nextReq = item.getNextReq();
    }

    public void updateOptionGrade() {
        optionGrade = item.getOptionGrade();
    }

    public void updateCount() {
        count = item.getCount();
    }

    public void updateItemId() {
        itemId = item.getItemId();
    }

    public void updateEquipped() {
        isEquipped = item.isEquipped();
    }

    public void updateIdentified() {
        isIdentified = item.isIdentified();
    }

    public void updateEnchantLevel() {
        enchantLevel = item.getEnchantLevel();
    }

    public void updateDuraility() {
        durability = item.getDurability();
    }

    public void updateChargeCount() {
        chargeCount = item.getChargeCount();
    }

    public void updateRemainingTime() {
        remainingTime = item.getRemainingTime();
    }

    public void updateLastUsed() {
        lastUsed = item.getLastUsed();
    }

    public void updateBless() {
        bless = item.getBless();
    }

    public void updateAttrEnchantLevel() {
        attrenchantLevel = item.getAttrEnchantLevel();
    }

    public void updateProtection() {
        protection = item.getProtection();
    } //추가

    public void updateClock() {
        clock = item.getClock();
    }

    public void updateEndTime() {
        endTime = item.getEndTime();
    }   //추가

    public void updateNextReq() {
        nextReq = item.getNextReq();
    }
}
