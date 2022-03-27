package ks.model;

import ks.util.L1CommonUtils;

public class GfxId {
    private int tempCharGfx; // ● 베이스 그래픽 ID

    private int gfxId; // ● 그래픽 ID

    public int getTempCharGfx() {
        return tempCharGfx;
    }

    public void setTempCharGfx(int i) {
        tempCharGfx = L1CommonUtils.changeGfx(i);
    }

    public int getGfxId() {
        return gfxId;
    }

    public void setGfxId(int i) {
        this.gfxId = L1CommonUtils.changeGfx(i);
    }
}
