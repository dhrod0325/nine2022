package ks.core.datatables.transform;

import ks.util.common.random.ChanceAble;

public class NpcTransform implements ChanceAble {
    private int npcId;
    private int transformId;
    private int gfxId;
    private double per;

    public int getNpcId() {
        return npcId;
    }

    public void setNpcId(int npcId) {
        this.npcId = npcId;
    }

    public int getTransformId() {
        return transformId;
    }

    public void setTransformId(int transformId) {
        this.transformId = transformId;
    }

    public int getGfxId() {
        return gfxId;
    }

    public void setGfxId(int gfxId) {
        this.gfxId = gfxId;
    }

    @Override
    public double getPer() {
        return per;
    }

    public void setPer(double per) {
        this.per = per;
    }
}
