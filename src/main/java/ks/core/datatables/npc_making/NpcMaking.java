package ks.core.datatables.npc_making;

import java.util.ArrayList;
import java.util.List;

public class NpcMaking {
    private int id;
    private int npcId;
    private int makingItemId;
    private String makingItemName;
    private int makingItemEnchant;
    private int makingItemBless;
    private int makingCount;

    private List<NpcMakingMaterial> makingMaterialList = new ArrayList<>();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNpcId() {
        return npcId;
    }

    public void setNpcId(int npcId) {
        this.npcId = npcId;
    }

    public int getMakingItemId() {
        return makingItemId;
    }

    public void setMakingItemId(int makingItemId) {
        this.makingItemId = makingItemId;
    }

    public String getMakingItemName() {
        return makingItemName;
    }

    public void setMakingItemName(String makingItemName) {
        this.makingItemName = makingItemName;
    }

    public int getMakingItemEnchant() {
        return makingItemEnchant;
    }

    public void setMakingItemEnchant(int makingItemEnchant) {
        this.makingItemEnchant = makingItemEnchant;
    }

    public int getMakingItemBless() {
        return makingItemBless;
    }

    public void setMakingItemBless(int makingItemBless) {
        this.makingItemBless = makingItemBless;
    }

    public List<NpcMakingMaterial> getMakingMaterialList() {
        return makingMaterialList;
    }

    public void setMakingMaterialList(List<NpcMakingMaterial> makingMaterialList) {
        this.makingMaterialList = makingMaterialList;
    }

    public int getMakingCount() {
        return makingCount;
    }

    public void setMakingCount(int makingCount) {
        this.makingCount = makingCount;
    }
}
