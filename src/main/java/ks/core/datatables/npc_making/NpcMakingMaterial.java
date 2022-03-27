package ks.core.datatables.npc_making;

import java.text.NumberFormat;

public class NpcMakingMaterial {
    private int makingId;
    private int makingMaterialItemId;
    private String makingMaterialItemName;
    private int makingMaterialEnchant;
    private int makingMaterialCount;

    public int getMakingMaterialCount() {
        return makingMaterialCount;
    }

    public void setMakingMaterialCount(int makingMaterialCount) {
        this.makingMaterialCount = makingMaterialCount;
    }

    public int getMakingId() {
        return makingId;
    }

    public void setMakingId(int makingId) {
        this.makingId = makingId;
    }

    public int getMakingMaterialItemId() {
        return makingMaterialItemId;
    }

    public void setMakingMaterialItemId(int makingMaterialItemId) {
        this.makingMaterialItemId = makingMaterialItemId;
    }

    public String getMakingMaterialItemName() {
        return makingMaterialItemName;
    }

    public void setMakingMaterialItemName(String makingMaterialItemName) {
        this.makingMaterialItemName = makingMaterialItemName;
    }

    public int getMakingMaterialEnchant() {
        return makingMaterialEnchant;
    }

    public void setMakingMaterialEnchant(int makingMaterialEnchant) {
        this.makingMaterialEnchant = makingMaterialEnchant;
    }

    public String getPrintName() {
        return getPrintName(true);
    }

    public String getPrintName(boolean printCount) {
        StringBuilder sb = new StringBuilder();

        if (makingMaterialEnchant > 0) {
            sb.append("+").append(makingMaterialEnchant).append(" ");
        }

        sb.append(makingMaterialItemName);

        if (printCount) {
            sb.append("(").append(
                    NumberFormat.getInstance().format(getMakingMaterialCount())
            ).append(")");
        }

        return sb.toString();
    }
}
