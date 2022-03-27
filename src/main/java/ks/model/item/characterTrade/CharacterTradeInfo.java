package ks.model.item.characterTrade;

import ks.model.pc.L1PcInstance;

public class CharacterTradeInfo {
    private int itemObjectId;
    private int targetCharId;
    private String targetName;
    private L1PcInstance targetPc;

    public String getTargetName() {
        return targetName;
    }

    public void setTargetName(String targetName) {
        this.targetName = targetName;
    }

    public L1PcInstance getTargetPc() {
        return targetPc;
    }

    public void setTargetPc(L1PcInstance targetPc) {
        this.targetPc = targetPc;
    }

    public int getItemObjectId() {
        return itemObjectId;
    }

    public void setItemObjectId(int itemObjectId) {
        this.itemObjectId = itemObjectId;
    }

    public int getTargetCharId() {
        return targetCharId;
    }

    public void setTargetCharId(int targetCharId) {
        this.targetCharId = targetCharId;
    }
}
