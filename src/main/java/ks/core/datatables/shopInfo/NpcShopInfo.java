package ks.core.datatables.shopInfo;

public class NpcShopInfo {
    private int npcId;
    private String npcName;
    private int targetItemId;
    private String targetItemName;

    public int getNpcId() {
        return npcId;
    }

    public void setNpcId(int npcId) {
        this.npcId = npcId;
    }

    public String getNpcName() {
        return npcName;
    }

    public void setNpcName(String npcName) {
        this.npcName = npcName;
    }

    public int getTargetItemId() {
        return targetItemId;
    }

    public void setTargetItemId(int targetItemId) {
        this.targetItemId = targetItemId;
    }

    public String getTargetItemName() {
        return targetItemName;
    }

    public void setTargetItemName(String targetItemName) {
        this.targetItemName = targetItemName;
    }
}
