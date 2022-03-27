package ks.model;

public class L1EtcItem extends L1Item {
    private boolean stackable;
    private int locX;
    private int locY;
    private short mapid;
    private int delayId;
    private int delayTime;
    private int delayEffect;
    private int maxChargeCount;
    private boolean isCanSeal;

    public L1EtcItem() {
    }

    @Override
    public boolean isStackable() {
        return stackable;
    }

    public void setStackable(boolean stackable) {
        this.stackable = stackable;
    }

    @Override
    public int getLocx() {
        return locX;
    }

    @Override
    public int getLocY() {
        return locY;
    }

    public void setLocY(int locY) {
        this.locY = locY;
    }

    @Override
    public short getMapid() {
        return mapid;
    }

    public void setMapid(short mapid) {
        this.mapid = mapid;
    }

    @Override
    public int getDelayId() {
        return delayId;
    }

    public void setDelayId(int delayId) {
        this.delayId = delayId;
    }

    public void setDdelayTime(int delay_time) {
        delayTime = delay_time;
    }

    @Override
    public int getDelayTime() {
        return delayTime;
    }

    public void setDelayTime(int delayTime) {
        this.delayTime = delayTime;
    }

    public void set_delayEffect(int delay_effect) {
        delayEffect = delay_effect;
    }

    public int getDelayEffect() {
        return delayEffect;
    }

    public void setDelayEffect(int delayEffect) {
        this.delayEffect = delayEffect;
    }

    @Override
    public int getMaxChargeCount() {
        return maxChargeCount;
    }

    public void setMaxChargeCount(int i) {
        maxChargeCount = i;
    }

    public int getLocX() {
        return locX;
    }

    public void setLocX(int locx) {
        locX = locx;
    }

    public boolean isCanSeal() {
        return isCanSeal;
    }

    public void setCanSeal(boolean flag) {
        isCanSeal = flag;
    }
}
